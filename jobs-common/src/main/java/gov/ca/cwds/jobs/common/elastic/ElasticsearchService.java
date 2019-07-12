package gov.ca.cwds.jobs.common.elastic;

import com.google.inject.Inject;
import gov.ca.cwds.jobs.common.exception.JobsException;
import gov.ca.cwds.jobs.common.inject.IndexName;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.apache.commons.lang3.Validate;
import org.elasticsearch.action.admin.indices.alias.IndicesAliasesRequest;
import org.elasticsearch.action.admin.indices.alias.IndicesAliasesRequest.AliasActions;
import org.elasticsearch.action.admin.indices.alias.get.GetAliasesRequest;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;

import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.client.indices.GetIndexRequest;
import org.elasticsearch.client.indices.GetMappingsRequest;
import org.elasticsearch.common.xcontent.XContentType;
import org.slf4j.LoggerFactory;

/**
 * Created by Alexander Serbin on 3/31/2019
 */
public class ElasticsearchService {

  private static final org.slf4j.Logger LOGGER = LoggerFactory
      .getLogger(ElasticsearchService.class);
  public static final String CUSTOM_CHECK = "custom_check";

  @Inject
  private ElasticsearchConfiguration configuration;

  @Inject
  @IndexName
  private String indexName;

  @Inject
  private RestHighLevelClient client;

  @Inject
  private ElasticApiWrapper elasticApiWrapper;

  public void handleAliases() {
    //acquire lock
    if (checkAliasExists()) {
      performAliasOperations();
    } else {
      createAliasWithOneIndex();
    }
    //release lock
  }

  public boolean checkAliasExists() {
    GetAliasesRequest request = new GetAliasesRequest();
    request.aliases(configuration.getElasticsearchAlias());
    return elasticApiWrapper.checkAliasExists(request);
  }

  public List<String> getIndexesForAlias() {
    List<String> indexes = new ArrayList<>(2);
    GetAliasesRequest request = new GetAliasesRequest(configuration.getElasticsearchAlias());
    try {
      client.indices().getAlias(request, RequestOptions.DEFAULT).getAliases().keySet().forEach(indexes::add);
      return indexes;
    } catch (IOException e) {
      LOGGER.error("Unable to get indexs for alias [" + Arrays.toString(request.aliases()) + "]", e);
      throw new RuntimeException(e);
    }
  }

  /**
   * Create an index before blasting documents into it.
   *
   * @return name of new index
   */
  public String createNewIndex() {
    String newIndexName =
        configuration.getElasticSearchIndexPrefix() + "_" + System.currentTimeMillis();
    LOGGER.info("Creating new index [{}] for type [{}]", newIndexName,
        configuration.getElasticsearchDocType());

    CreateIndexRequest createIndexRequest = new CreateIndexRequest(newIndexName);
    createIndexRequest.settings(configuration.getIndexSettings(), XContentType.JSON);
    elasticApiWrapper.createIndex(createIndexRequest);
    elasticApiWrapper.putMapping(newIndexName, configuration.getElasticsearchDocType(), configuration.getDocumentMapping());
    checkIndexCreatedProperly(newIndexName);

    return newIndexName;
  }

  private void checkIndexCreatedProperly(String newIndexName) {
    GetMappingsRequest mappingsRequest = new GetMappingsRequest();
    mappingsRequest.indices(newIndexName);

    Map<String, Object> mapping = elasticApiWrapper.getIndexMapping(mappingsRequest, newIndexName);

    if (!((Map) mapping.get("properties")).containsKey(CUSTOM_CHECK)) {
      throw new JobsException("Index was not created properly. Please restart the job");
    }
    LOGGER.info("Index has been created properly. Custom mapping is found");
  }

  public String getExistingIndex() {
    Validate.isTrue(checkAliasExists(), "Alias %s does not exist",
        configuration.getElasticsearchAlias());
    List<String> indexes = getIndexesForAlias();
    LOGGER.info("Discovered indexes {} for alias [{}]", indexes,
        configuration.getElasticsearchAlias());
    String existingIndexName = indexes.stream()
        .filter(s -> s.startsWith(configuration.getElasticSearchIndexPrefix())).findAny()
        .orElseThrow(IllegalStateException::new);
    LOGGER.info("Found index name [{}] to work with", existingIndexName);
    return existingIndexName;
  }

  private void performAliasOperations() {
    List<String> indexes = getIndexesForAlias();
    List<String> indexesToDelete = indexes.stream()
        .filter(s -> s.startsWith(configuration.getElasticSearchIndexPrefix())).collect(
            Collectors.toList());
    LOGGER.info("Found old indexes {} for alias [{}]", indexesToDelete,
        configuration.getElasticsearchAlias());
    LOGGER.info("Adding new index [{}] and removing old indexes {} for alias [{}] ", indexName,
        indexesToDelete, configuration.getElasticsearchAlias());
    IndicesAliasesRequest request = new IndicesAliasesRequest();
    AliasActions addIndexToAliasAction = AliasActions.add()
        .index(getIndexName())
        .alias(configuration.getElasticsearchAlias());
    AliasActions removeOldIndexesAction = AliasActions.remove();
    indexesToDelete.forEach(removeOldIndexesAction::index);
    removeOldIndexesAction.alias(configuration.getElasticsearchAlias());
    request.addAliasAction(addIndexToAliasAction);
    if (!indexesToDelete.isEmpty()) {
      request.addAliasAction(removeOldIndexesAction);
    }
    elasticApiWrapper.getAliasesAction(request);
    if (LOGGER.isInfoEnabled()) {
      verifyIndexesForAlias();
    }
  }

  private void verifyIndexesForAlias() {
    LOGGER.info("Verification: alias [{}], indexes {}", configuration.getElasticsearchAlias(),
        getIndexesForAlias());
  }

  private void createAliasWithOneIndex() {
    removeRedundantIndexIfExists();
    IndicesAliasesRequest request = new IndicesAliasesRequest();
    AliasActions aliasAction = new AliasActions(AliasActions.Type.ADD)
        .index(getIndexName())
        .alias(configuration.getElasticsearchAlias());
    request.addAliasAction(aliasAction);
    if (LOGGER.isInfoEnabled()) {
      LOGGER.info("Creating alias [{}] for index [{}] ", configuration.getElasticsearchAlias(),
          getIndexName());
    }
    elasticApiWrapper.getAliasesAction(request);
  }

  private void removeRedundantIndexIfExists() {
    GetIndexRequest request = new GetIndexRequest(configuration.getElasticsearchAlias());
    if (elasticApiWrapper.checkIndicesExists(request)) {
      LOGGER.warn("Orphan ES index [{}] discovered ", configuration.getElasticsearchAlias());
      DeleteIndexRequest deleteIndexRequest = new DeleteIndexRequest();
      deleteIndexRequest.indices(configuration.getElasticsearchAlias());
      LOGGER
          .info("Removing orphan ES index [{}] ", configuration.getElasticsearchAlias());
      elasticApiWrapper.deleteIndex(deleteIndexRequest);
    }
  }

  public void setConfiguration(ElasticsearchConfiguration configuration) {
    this.configuration = configuration;
  }

  public void setClient(RestHighLevelClient client) {
    this.client = client;
  }

  public String getIndexName() {
    return indexName;
  }

  public void setElasticApiWrapper(ElasticApiWrapper elasticApiWrapper) {
    this.elasticApiWrapper = elasticApiWrapper;
  }
}
