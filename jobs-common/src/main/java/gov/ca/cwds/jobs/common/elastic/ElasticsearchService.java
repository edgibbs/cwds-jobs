package gov.ca.cwds.jobs.common.elastic;

import com.google.inject.Inject;
import gov.ca.cwds.jobs.common.inject.IndexName;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.apache.commons.lang3.Validate;
import org.elasticsearch.action.ActionFuture;
import org.elasticsearch.action.admin.indices.alias.IndicesAliasesRequest;
import org.elasticsearch.action.admin.indices.alias.IndicesAliasesRequest.AliasActions;
import org.elasticsearch.action.admin.indices.alias.IndicesAliasesResponse;
import org.elasticsearch.action.admin.indices.alias.exists.AliasesExistResponse;
import org.elasticsearch.action.admin.indices.alias.get.GetAliasesRequest;
import org.elasticsearch.action.admin.indices.create.CreateIndexRequest;
import org.elasticsearch.action.admin.indices.create.CreateIndexRequestBuilder;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.xcontent.XContentType;
import org.slf4j.LoggerFactory;

/**
 * Created by Alexander Serbin on 3/31/2019
 */
public class ElasticsearchService {

  private static final org.slf4j.Logger LOGGER = LoggerFactory
      .getLogger(ElasticsearchService.class);

  @Inject
  private ElasticsearchConfiguration configuration;

  @Inject
  @IndexName
  private String indexName;

  @Inject
  private Client client;

  public void handleAliases() {
    //acquire lock
    if (checkAliasExists()) {
      List<String> indexesToDelete = performAliasOperations();
      deleteOldIndexes(indexesToDelete);
    } else {
      createAlias();
    }
    //release lock
  }

  public boolean checkAliasExists() {
    GetAliasesRequest request = new GetAliasesRequest();
    request.aliases(configuration.getElasticsearchAlias());
    ActionFuture<AliasesExistResponse> response = client.admin().indices().aliasesExist(request);
    return response.actionGet().exists();
  }

  public List<String> getIndexesForAlias() {
    List<String> indexes = new ArrayList<>(2);
    GetAliasesRequest request = new GetAliasesRequest(configuration.getElasticsearchAlias());
    client.admin().indices().getAliases(request).actionGet().getAliases()
        .keysIt().forEachRemaining(indexes::add);
    LOGGER.info("Discovered indexes {} for alias {}", indexes,
        configuration.getElasticsearchAlias());
    return indexes;
  }

  /**
   * Create an index before blasting documents into it.
   *
   * @return name of new index
   */
  public String createNewIndex() {
    String newIndexName = configuration.getElasticSearchIndexPrefix() + System.currentTimeMillis();
    LOGGER.info("Creating new ES index [{}] for type [{}] ...",
        newIndexName, configuration.getElasticsearchDocType());
    CreateIndexRequestBuilder createIndexRequestBuilder =
        client.admin().indices().prepareCreate(newIndexName);
    createIndexRequestBuilder
        .setSettings(configuration.getIndexSettings(), XContentType.JSON);
    createIndexRequestBuilder
        .addMapping(configuration.getElasticsearchDocType(), configuration.getDocumentMapping(),
            XContentType.JSON);

    CreateIndexRequest indexRequest = createIndexRequestBuilder.request();
    client.admin().indices().create(indexRequest).actionGet();
    LOGGER.info("Created new index {}", newIndexName);
    return newIndexName;
  }

  public String getExistingIndex() {
    Validate.isTrue(checkAliasExists(), "Alias %s does not exist",
        configuration.getElasticsearchAlias());
    List<String> indexes = getIndexesForAlias();
    String existingIndexName = indexes.stream()
        .filter(s -> s.startsWith(configuration.getElasticSearchIndexPrefix())).findAny()
        .orElseThrow(IllegalStateException::new);
    LOGGER.info("Found index name {} to work with", existingIndexName);
    return existingIndexName;
  }

  private void deleteOldIndexes(List<String> indexesToDelete) {
    LOGGER.info("Deleting old indexes {}", indexesToDelete);
    DeleteIndexResponse response = client.admin().indices()
        .delete(new DeleteIndexRequest(indexesToDelete.toArray(new String[]{}))).actionGet();
    LOGGER.info("Operation acknowledgment is {}", response.isAcknowledged());
  }

  private List<String> performAliasOperations() {
    List<String> indexes = getIndexesForAlias();
    List<String> indexesToDelete = indexes.stream()
        .filter(s -> s.startsWith(configuration.getElasticSearchIndexPrefix())).collect(
            Collectors.toList());
    LOGGER.info("Found old indexes {} for alias {}. Detaching those from alias", indexesToDelete,
        configuration.getElasticsearchAlias());
    LOGGER.info("And adding new index {} for alias {}", indexName,
        configuration.getElasticsearchAlias());
    IndicesAliasesRequest request = new IndicesAliasesRequest();
    AliasActions addIndexToAliasAction = AliasActions.add()
        .index(indexName)
        .alias(configuration.getElasticsearchAlias());
    AliasActions removeOldIndexesAction = AliasActions.remove();
    indexesToDelete.forEach(removeOldIndexesAction::index);
    removeOldIndexesAction.alias(configuration.getElasticsearchAlias());
    request.addAliasAction(addIndexToAliasAction);
    request.addAliasAction(removeOldIndexesAction);
    IndicesAliasesResponse indicesAliasesResponse = client.admin().indices().aliases(request)
        .actionGet();
    LOGGER.info("Operation acknowledgment is {}", indicesAliasesResponse.isAcknowledged());
    return indexesToDelete;
  }

  private void createAlias() {
    IndicesAliasesRequest request = new IndicesAliasesRequest();
    AliasActions aliasAction = AliasActions.add()
        .index(indexName)
        .alias(configuration.getElasticsearchAlias());
    request.addAliasAction(aliasAction);
    client.admin().indices().aliases(request).actionGet();
  }

  public void setConfiguration(ElasticsearchConfiguration configuration) {
    this.configuration = configuration;
  }

  public void setClient(Client client) {
    this.client = client;
  }
}
