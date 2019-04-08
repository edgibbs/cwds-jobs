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
import org.elasticsearch.action.admin.indices.alias.exists.AliasesExistResponse;
import org.elasticsearch.action.admin.indices.alias.get.GetAliasesRequest;
import org.elasticsearch.action.admin.indices.create.CreateIndexRequest;
import org.elasticsearch.action.admin.indices.create.CreateIndexRequestBuilder;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.admin.indices.exists.indices.IndicesExistsRequest;
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
      createAliasWithOneIndex();
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
    return indexes;
  }

  /**
   * Create an index before blasting documents into it.
   *
   * @return name of new index
   */
  public String createNewIndex() {
    String newIndexName = configuration.getElasticSearchIndexPrefix() + "_" + System.currentTimeMillis();
    LOGGER.info("Creating new index [{}] for type [{}]", newIndexName,
        configuration.getElasticsearchDocType());

    CreateIndexRequestBuilder createIndexRequestBuilder =
        client.admin().indices().prepareCreate(newIndexName);
    createIndexRequestBuilder
        .setSettings(configuration.getIndexSettings(), XContentType.JSON);
    createIndexRequestBuilder
        .addMapping(configuration.getElasticsearchDocType(), configuration.getDocumentMapping(),
            XContentType.JSON);

    CreateIndexRequest indexRequest = createIndexRequestBuilder.request();
    client.admin().indices().create(indexRequest).actionGet();
    return newIndexName;
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

  private void deleteOldIndexes(List<String> indexesToDelete) {
    LOGGER.info("Enumerating orphan indexes [{}]", indexesToDelete);
    //As for now, we don't delete orphan indexes
    //client.admin().indices()
    //    .delete(new DeleteIndexRequest(indexesToDelete.toArray(new String[]{}))).actionGet();
  }

  private List<String> performAliasOperations() {
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
        .index(indexName)
        .alias(configuration.getElasticsearchAlias());
    AliasActions removeOldIndexesAction = AliasActions.remove();
    indexesToDelete.forEach(removeOldIndexesAction::index);
    removeOldIndexesAction.alias(configuration.getElasticsearchAlias());
    request.addAliasAction(addIndexToAliasAction);
    request.addAliasAction(removeOldIndexesAction);
    client.admin().indices().aliases(request).actionGet();
    if (LOGGER.isInfoEnabled()) {
      verifyIndexesForAlias();
    }
    return indexesToDelete;
  }

  private void verifyIndexesForAlias() {
      LOGGER.info("Verification: alias {}, indexes [{}]", configuration.getElasticsearchAlias(),
          getIndexesForAlias());
  }

  private void createAliasWithOneIndex() {
    removeRedundantIndexIfExists();
    IndicesAliasesRequest request = new IndicesAliasesRequest();
    AliasActions aliasAction = AliasActions.add()
        .index(indexName)
        .alias(configuration.getElasticsearchAlias());
    request.addAliasAction(aliasAction);
    if (LOGGER.isInfoEnabled()) {
      LOGGER.info("Creating alias [{}] for index [{}] ", configuration.getElasticsearchAlias(),
          indexName);
    }
    client.admin().indices().aliases(request).actionGet();
  }

  private void removeRedundantIndexIfExists() {
    IndicesExistsRequest request = new IndicesExistsRequest();
    request.indices(configuration.getElasticsearchAlias());
    if (client.admin().indices().exists(request).actionGet().isExists()) {
      LOGGER.warn("Orphan ES index [{}] discovered ", configuration.getElasticsearchAlias());
      DeleteIndexRequest deleteIndexRequest = new DeleteIndexRequest();
      deleteIndexRequest.indices(configuration.getElasticsearchAlias());
      LOGGER
          .info("Removing orphan ES index [{}] ", configuration.getElasticsearchAlias());
      client.admin().indices().delete(deleteIndexRequest).actionGet();
    }
  }

  public void setConfiguration(ElasticsearchConfiguration configuration) {
    this.configuration = configuration;
  }

  public void setClient(Client client) {
    this.client = client;
  }
}
