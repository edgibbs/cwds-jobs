package gov.ca.cwds.jobs.cap.users;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.google.inject.Inject;
import gov.ca.cwds.jobs.cap.users.dto.ChangedUserDto;
import gov.ca.cwds.jobs.common.elastic.ElasticSearchIndexerDao;
import gov.ca.cwds.jobs.common.elastic.ElasticWriter;

public class CapUsersWriter extends ElasticWriter<ChangedUserDto> {

  @Inject
  public CapUsersWriter(ElasticSearchIndexerDao elasticsearchDao, ObjectMapper objectMapper) {
    super(elasticsearchDao, objectMapper);
    objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    objectMapper.registerModule(new JavaTimeModule());
  }
}
