package gov.ca.cwds.jobs.cals.facility.cws;

import com.fasterxml.jackson.annotation.JsonProperty;
import gov.ca.cwds.jobs.common.BaseJobConfiguration;
import io.dropwizard.db.DataSourceFactory;

/**
 * Created by Alexander Serbin on 1/18/2018.
 */
public class CwsFacilityJobConfiguration extends BaseJobConfiguration {

  private DataSourceFactory cmsDataSourceFactory;

  @JsonProperty
  public DataSourceFactory getCmsDataSourceFactory() {
    return cmsDataSourceFactory;
  }

  public void setCmsDataSourceFactory(DataSourceFactory cmsDataSourceFactory) {
    this.cmsDataSourceFactory = cmsDataSourceFactory;
  }

}
