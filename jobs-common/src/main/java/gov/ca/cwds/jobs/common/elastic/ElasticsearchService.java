package gov.ca.cwds.jobs.common.elastic;

import com.google.inject.Inject;
import gov.ca.cwds.jobs.common.exception.JobsException;
import gov.ca.cwds.jobs.common.inject.IndexName;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.apache.commons.lang3.Validate;
import org.elasticsearch.action.admin.indices.alias.IndicesAliasesRequest;
import org.elasticsearch.action.admin.indices.alias.IndicesAliasesRequest.AliasActions;
import org.elasticsearch.action.admin.indices.alias.get.GetAliasesRequest;
import org.elasticsearch.action.admin.indices.create.CreateIndexRequestBuilder;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.admin.indices.exists.indices.IndicesExistsRequest;
import org.elasticsearch.action.admin.indices.mapping.get.GetMappingsRequest;
import org.elasticsearch.client.Client;
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
  private Client client;

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
    client.admin().indices().getAliases(request).actionGet().getAliases()
        .keysIt().forEachRemaining(indexes::add);
    return indexes;
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

    CreateIndexRequestBuilder createIndexRequestBuilder = elasticApiWrapper
        .prepareCreateIndexBuilder(newIndexName);
    createIndexRequestBuilder
        .setSettings(configuration.getIndexSettings(), XContentType.JSON);
    createIndexRequestBuilder
        .addMapping(configuration.getElasticsearchDocType(), configuration.getDocumentMapping(),
            XContentType.JSON);

    elasticApiWrapper.createIndex(createIndexRequestBuilder);
    checkIndexCreatedProperly(newIndexName);
    return newIndexName;
  }

  private void checkIndexCreatedProperly(String newIndexName) {
    try {
      GetMappingsRequest mappingsRequest = new GetMappingsRequest();
      mappingsRequest.indices(newIndexName);

      Map<String, Object> mapping = elasticApiWrapper
          .getIndexMapping(mappingsRequest, newIndexName);

      if (!((Map) mapping.get("properties")).containsKey(CUSTOM_CHECK)) {
        throw new JobsException("Index was not created properly. Please restart the job");
      }
      LOGGER.info("Index has been created properly. Custom mapping is found");
    } catch (IOException e) {
      throw new JobsException("Can't check index created properly", e);
    }
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
    AliasActions aliasAction = AliasActions.add()
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
    IndicesExistsRequest request = new IndicesExistsRequest();
    request.indices(configuration.getElasticsearchAlias());
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

  public void setClient(Client client) {
    this.client = client;
  }

  public String getIndexName() {
    return indexName;
  }

  public void setElasticApiWrapper(ElasticApiWrapper elasticApiWrapper) {
    this.elasticApiWrapper = elasticApiWrapper;
  }
}
