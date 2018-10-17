package gov.ca.cwds.jobs.common;

import gov.ca.cwds.jobs.common.configuration.JobConfiguration;
import gov.ca.cwds.jobs.common.configuration.MultiThreadConfiguration;
import gov.ca.cwds.jobs.common.configuration.MultiThreadJobConfiguration;
import io.dropwizard.db.DataSourceFactory;

/**
 * Created by Alexander Serbin on 3/4/2018.
 */
class TestJobConfiguration implements JobConfiguration {

  private MultiThreadConfiguration multiThread;

  private DataSourceFactory testDataSourceFactory;

  public MultiThreadConfiguration getMultiThread() {
    return multiThread;
  }

  public void setMultiThread(MultiThreadConfiguration multiThread) {
    this.multiThread = multiThread;
  }

  public DataSourceFactory getTestDataSourceFactory() {
    return testDataSourceFactory;
  }

  public void setTestDataSourceFactory(DataSourceFactory testDataSourceFactory) {
    this.testDataSourceFactory = testDataSourceFactory;
  }

}
