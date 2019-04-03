package gov.ca.cwds.jobs.cals.facility;

import com.google.inject.Inject;
import gov.ca.cwds.jobs.common.elastic.ElasticWriter;
import org.elasticsearch.client.Client;

/**
 * Created by Alexander Serbin on 3/28/2018.
 */
public class FacilityElasticWriter extends ElasticWriter<ChangedFacilityDto> {

  @Inject
  public FacilityElasticWriter(Client client) {
    super(client);
  }
}
