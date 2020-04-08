package gov.ca.cwds.jobs.cals.facility;

import javax.validation.Valid;

import com.fasterxml.jackson.annotation.JsonProperty;

import gov.ca.cwds.jobs.common.configuration.JobConfiguration;
import gov.ca.cwds.jobs.common.configuration.MultiThreadConfiguration;
import gov.ca.cwds.jobs.common.elastic.ElasticsearchConfiguration;
import io.dropwizard.db.DataSourceFactory;

/**
 * Created by Ievgenii Drozd on 4/30/2018.
 */
public class BaseFacilityJobConfiguration implements JobConfiguration {

  private DataSourceFactory calsnsDataSourceFactory;

  private MultiThreadConfiguration multiThread;

  @Valid
  private ElasticsearchConfiguration elasticsearch;

  @JsonProperty
  public DataSourceFactory getCalsnsDataSourceFactory() {
    return calsnsDataSourceFactory;
  }

  public void setCalsnsDataSourceFactory(DataSourceFactory calsnsDataSourceFactory) {
    this.calsnsDataSourceFactory = calsnsDataSourceFactory;
  }

  public MultiThreadConfiguration getMultiThread() {
    return multiThread;
  }

  public void setMultiThread(MultiThreadConfiguration multiThread) {
    this.multiThread = multiThread;
  }

  @JsonProperty
  public ElasticsearchConfiguration getElasticsearch() {
    return elasticsearch;
  }

  public void setElasticsearch(ElasticsearchConfiguration elasticsearch) {
    this.elasticsearch = elasticsearch;
  }

}
