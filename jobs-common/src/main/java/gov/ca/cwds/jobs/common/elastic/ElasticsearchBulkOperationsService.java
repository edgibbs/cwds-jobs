package gov.ca.cwds.jobs.common.elastic;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Inject;
import gov.ca.cwds.jobs.common.inject.IndexName;
import java.io.IOException;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.common.xcontent.XContentType;

/**
 * Created by Alexander Serbin on 4/1/2019
 */
public class ElasticsearchBulkOperationsService {

  @Inject
  @IndexName
  private String indexName;

  @Inject
  private ElasticsearchConfiguration config;

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
      throws IOException {
    return new IndexRequest(indexName,
        config.getElasticsearchDocType(), id).source(mapper.writeValueAsBytes(obj), XContentType.JSON);
  }

  public IndexRequest bulkAdd(final String id, final String json) {
    return new IndexRequest(indexName,
        config.getElasticsearchDocType(), id).source(json, XContentType.JSON);
  }

  /**
   * Prepare an delete request for bulk operations.
   *
   * @param id ES document id
   * @return prepared DeleteRequest
   */
  public DeleteRequest bulkDelete(final String id) {
    return new DeleteRequest(indexName,
        config.getElasticsearchDocType(), id);
  }

}
