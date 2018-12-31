package gov.ca.cwds.jobs.audit;

import com.fasterxml.jackson.annotation.JsonProperty;
import gov.ca.cwds.jobs.common.configuration.JobConfiguration;
import gov.ca.cwds.jobs.common.elastic.ElasticsearchConfiguration;

public class AuditEventsJobConfiguration implements JobConfiguration {

  private ElasticsearchConfiguration elasticsearch;

  @JsonProperty
  public ElasticsearchConfiguration getElasticsearch() {
    return elasticsearch;
  }

  public void setElasticsearch(ElasticsearchConfiguration elasticsearch) {
    this.elasticsearch = elasticsearch;
  }

}
