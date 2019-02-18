package gov.ca.cwds.jobs.audit;

import com.fasterxml.jackson.annotation.JsonProperty;
import gov.ca.cwds.jobs.common.configuration.JobConfiguration;
import gov.ca.cwds.jobs.common.configuration.MultiThreadConfiguration;
import gov.ca.cwds.jobs.common.elastic.ElasticsearchConfiguration;
import io.dropwizard.db.DataSourceFactory;

public class AuditEventsJobConfiguration implements JobConfiguration {

  private ElasticsearchConfiguration elasticsearch;

  private DataSourceFactory nsDataSourceFactory;

  private MultiThreadConfiguration multiThread;

  @JsonProperty
  public ElasticsearchConfiguration getElasticsearch() {
    return elasticsearch;
  }

  public void setElasticsearch(ElasticsearchConfiguration elasticsearch) {
    this.elasticsearch = elasticsearch;
  }

  @JsonProperty
  public DataSourceFactory getNsDataSourceFactory() {
    return nsDataSourceFactory;
  }

  public void setNsDataSourceFactory(DataSourceFactory nsDataSourceFactory) {
    this.nsDataSourceFactory = nsDataSourceFactory;
  }

  @JsonProperty
  public MultiThreadConfiguration getMultiThread() {
    return multiThread;
  }

  public void setMultiThread(MultiThreadConfiguration multiThread) {
    this.multiThread = multiThread;
  }


}
