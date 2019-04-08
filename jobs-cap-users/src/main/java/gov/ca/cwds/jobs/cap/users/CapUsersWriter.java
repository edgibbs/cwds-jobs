package gov.ca.cwds.jobs.cap.users;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.google.inject.Inject;
import gov.ca.cwds.jobs.cap.users.dto.ChangedUserDto;
import gov.ca.cwds.jobs.common.elastic.ElasticWriter;
import gov.ca.cwds.jobs.common.elastic.ElasticsearchBulkOperationsService;
import gov.ca.cwds.jobs.common.inject.IndexName;
import org.elasticsearch.client.Client;

public class CapUsersWriter extends ElasticWriter<ChangedUserDto> {

  @Inject
  public CapUsersWriter(Client client, ObjectMapper objectMapper,
      ElasticsearchBulkOperationsService bulkService, @IndexName String indexName) {
    super(client, objectMapper, bulkService, indexName);
    objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    objectMapper.registerModule(new JavaTimeModule());
  }
}
