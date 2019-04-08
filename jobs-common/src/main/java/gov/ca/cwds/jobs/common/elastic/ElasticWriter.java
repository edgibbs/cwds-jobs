package gov.ca.cwds.jobs.common.elastic;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Inject;
import gov.ca.cwds.jobs.common.BulkWriter;
import gov.ca.cwds.jobs.common.ChangedDTO;
import gov.ca.cwds.jobs.common.RecordChangeOperation;
import gov.ca.cwds.jobs.common.exception.JobsException;
import gov.ca.cwds.jobs.common.inject.IndexName;
import gov.ca.cwds.jobs.common.util.ConsumerCounter;
import java.util.List;
import java.util.concurrent.TimeUnit;
import org.elasticsearch.action.bulk.BulkProcessor;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.client.Client;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * @param <T> persistence class type
 * @author CWDS TPT-2
 */
public class ElasticWriter<T extends ChangedDTO<?>> implements BulkWriter<T> {

  private static final Logger LOGGER = LoggerFactory.getLogger(ElasticWriter.class);

  protected BulkProcessor bulkProcessor;

  protected ObjectMapper objectMapper;

  private Client client;

  private ElasticsearchBulkOperationsService bulkService;

  private String indexName;

  @Inject
  public ElasticWriter(Client client, ObjectMapper objectMapper,
      ElasticsearchBulkOperationsService bulkService, @IndexName String indexName) {
    this.objectMapper = objectMapper;
    this.bulkService = bulkService;
    this.client = client;
    this.indexName = indexName;
    bulkProcessor =
        BulkProcessor.builder(client, new BulkProcessor.Listener() {
          @Override
          public void beforeBulk(long executionId, BulkRequest request) {
            LOGGER.warn("Ready to execute bulk of {} actions", request.numberOfActions());
          }

          @Override
          public void afterBulk(long executionId, BulkRequest request, BulkResponse response) {
            LOGGER.warn("Response from bulk: {} ", response.getItems().length);
          }

          @Override
          public void afterBulk(long executionId, BulkRequest request, Throwable failure) {
            LOGGER.error("ERROR EXECUTING BULK", failure);
          }
        }).build();
  }

  @Override
  public void write(List<T> items) {
    LOGGER.info("Writing to index {}", indexName);
    items.forEach(item -> {
      try {
        RecordChangeOperation recordChangeOperation = item.getRecordChangeOperation();

        if (RecordChangeOperation.I == recordChangeOperation
            || RecordChangeOperation.U == recordChangeOperation) {
          LOGGER.debug("Preparing to insert item: ID {}", item.getId());
          bulkProcessor.add(bulkService.bulkAdd(objectMapper, item.getId(), item.getDTO()));
        } else if (RecordChangeOperation.D == recordChangeOperation) {
          LOGGER.debug("Preparing to delete item: ID {}", item.getId());
          bulkProcessor.add(bulkService.bulkDelete(item.getId()));
        } else {
          LOGGER.warn("No operation found for facility with ID: {}", item.getId());
        }
      } catch (JsonProcessingException e) {
        throw new JobsException(e);
      }
    });
    bulkProcessor.flush();
    ConsumerCounter.addToCounter(items.size());
  }

  @Override
  public void destroy() {
    try {
      try {
        bulkProcessor.awaitClose(3000, TimeUnit.MILLISECONDS);
      } finally {
        if (client != null) {
          this.client.close();
        }
      }
    } catch (InterruptedException e) {
      LOGGER.warn("Interrupted!!!");
      Thread.currentThread().interrupt();
    }
  }

  public ElasticsearchBulkOperationsService getBulkService() {
    return bulkService;
  }

}
