package gov.ca.cwds.jobs.common.elastic;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Inject;
import gov.ca.cwds.jobs.common.BaseJobConfiguration;
import java.io.Closeable;
import java.io.IOException;
import org.elasticsearch.action.admin.indices.create.CreateIndexRequest;
import org.elasticsearch.action.admin.indices.create.CreateIndexRequestBuilder;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.Requests;
import org.elasticsearch.cluster.metadata.IndexMetaData;
import org.elasticsearch.common.xcontent.XContentType;
import org.slf4j.LoggerFactory;

/**
 * A DAO for Elasticsearch with writing indexes functionality. It is not intended for searching, nor
 * it can contain any index-specific code or hardcoded mapping.
 *
 * <p> Let Guice manage inject object instances. Don't manage instances in this class. </p>
 *
 * @author CWDS TPT-2
 */
public class ElasticSearchIndexerDao implements Closeable {

  private static final org.slf4j.Logger LOGGER = LoggerFactory
      .getLogger(ElasticSearchIndexerDao.class);

  /**
   * Client is thread safe.
   */
  private Client client;

  /**
   * Elasticsearch configuration
   */
  private BaseJobConfiguration config;

  /**
   * Constructor.
   *
   * @param client The ElasticSearch client
   * @param config The ElasticSearch configuration which is read from .yaml file
   */
  @Inject
  public ElasticSearchIndexerDao(Client client, BaseJobConfiguration config) {
    this.client = client;
    this.config = config;
  }

  /**
   * Check whether Elasticsearch already has the chosen index.
   *
   * @param index index name or alias
   * @return whether the index exists
   */
  private boolean doesIndexExist(final String index) {
    final IndexMetaData indexMetaData = client.admin().cluster()
        .state(Requests.clusterStateRequest()).actionGet().getState().getMetaData().index(index);
    return indexMetaData != null;
  }

  /**
   * Create an index before blasting documents into it.
   */
  private void createIndex() {
    LOGGER.warn("CREATING ES INDEX [{}] for type [{}]",
        config.getElasticsearchAlias(), config.getElasticsearchDocType());

    CreateIndexRequestBuilder createIndexRequestBuilder =
        getClient().admin().indices().prepareCreate(config.getElasticsearchAlias());

    createIndexRequestBuilder
        .setSettings(config.getIndexSettings(), XContentType.JSON);
    createIndexRequestBuilder
        .addMapping(config.getElasticsearchDocType(), config.getDocumentMapping(),
            XContentType.JSON);
    LOGGER.warn("  createIndex index settings [{}]", config.getIndexSettings());
    LOGGER.warn("  createIndex with document mapping [{}]", config.getDocumentMapping());
    CreateIndexRequest indexRequest = createIndexRequestBuilder.request();
    getClient().admin().indices().create(indexRequest).actionGet();
  }

  /**
   * Create an index, if missing.
   *
   * <p> Method is intentionally synchronized to prevent race conditions and multiple attempts to
   * create the same index. </p>
   */
  @SuppressWarnings({"findbugs:SWL_SLEEP_WITH_LOCK_HELD", "squid:S2276"})
  public synchronized void createIndexIfMissing() {
    final String index = config.getElasticsearchAlias();
    if (!doesIndexExist(index)) {
      LOGGER.warn("ES INDEX {} DOES NOT EXIST!!", index);
      createIndex();
      try {
        // Give Elasticsearch a moment to catch its breath.
        // Thread.currentThread().wait(2000L); // thread monitor error
        Thread.sleep(100000L);
      } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
        LOGGER.warn("Interrupted!");
      }
    }
  }

  /**
   * Prepare an index request for bulk operations.
   *
   * @param mapper Jackson ObjectMapper
   * @param id ES document id
   * @param obj document object
   * @return prepared IndexRequest
   * @throws JsonProcessingException if unable to serialize JSON
   */
  public IndexRequest bulkAdd(final ObjectMapper mapper, final String id, final Object obj)
      throws JsonProcessingException {
    return client.prepareIndex(config.getElasticsearchAlias(),
        config.getElasticsearchDocType(), id)
        .setSource(mapper.writeValueAsBytes(obj), XContentType.JSON).request();
  }

  /**
   * Prepare an delete request for bulk operations.
   *
   * @param id ES document id
   * @return prepared DeleteRequest
   */
  public DeleteRequest bulkDelete(final String id) {
    return client.prepareDelete(config.getElasticsearchAlias(),
        config.getElasticsearchDocType(), id).request();
  }

  /**
   * Stop the ES client, if started.
   */
  private void stop() {
    if (client != null) {
      this.client.close();
    }
  }

  @Override
  public void close() throws IOException {
    try {
      stop();
    } catch (Exception e) {
      final String msg = "Error closing ElasticSearch DAO: " + e.getMessage();
      LOGGER.error(msg, e);
      throw new IOException(msg, e);
    }
  }

  /**
   * @return the client
   */
  public Client getClient() {
    return client;
  }
}
