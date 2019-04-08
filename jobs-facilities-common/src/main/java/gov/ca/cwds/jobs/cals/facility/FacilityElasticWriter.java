package gov.ca.cwds.jobs.cals.facility;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Inject;
import gov.ca.cwds.jobs.common.elastic.ElasticWriter;
import gov.ca.cwds.jobs.common.elastic.ElasticsearchBulkOperationsService;
import gov.ca.cwds.jobs.common.inject.IndexName;
import org.elasticsearch.client.Client;

/**
 * Created by Alexander Serbin on 3/28/2018.
 */
public class FacilityElasticWriter extends ElasticWriter<ChangedFacilityDto> {

  @Inject
  public FacilityElasticWriter(Client client, ObjectMapper objectMapper,
      ElasticsearchBulkOperationsService bulkService, @IndexName String indexName) {
    super(client, objectMapper, bulkService, indexName);
  }
}
