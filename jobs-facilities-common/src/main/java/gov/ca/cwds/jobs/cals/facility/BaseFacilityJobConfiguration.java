package gov.ca.cwds.jobs.cals.facility;

import com.fasterxml.jackson.annotation.JsonProperty;
import gov.ca.cwds.jobs.common.configuration.MultiThreadJobConfiguration;
import io.dropwizard.db.DataSourceFactory;

/**
 * Created by Ievgenii Drozd on 4/30/2018.
 */
public class BaseFacilityJobConfiguration extends MultiThreadJobConfiguration {

  private DataSourceFactory calsnsDataSourceFactory;

  @JsonProperty
  public DataSourceFactory getCalsnsDataSourceFactory() {
    return calsnsDataSourceFactory;
  }

  public void setCalsnsDataSourceFactory(DataSourceFactory calsnsDataSourceFactory) {
    this.calsnsDataSourceFactory = calsnsDataSourceFactory;
  }
}
