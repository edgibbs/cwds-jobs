package gov.ca.cwds.jobs.cap.users;

import com.fasterxml.jackson.annotation.JsonProperty;
import gov.ca.cwds.jobs.common.configuration.JobConfiguration;
import gov.ca.cwds.jobs.common.elastic.ElasticsearchConfiguration;
import io.dropwizard.db.DataSourceFactory;

public class CapUsersJobConfiguration implements JobConfiguration {

  private String perryApiUrl;
  private String perryApiUser;
  private String perryApiPassword;
  private int jerseyClientConnectTimeout;
  private int jerseyClientReadTimeout;

  private int sleepTime;
  private int batchSize;

  private boolean performanceTestMode;

  private int elasticSearchBulkSize;

  private DataSourceFactory cmsDataSourceFactory;

  private ElasticsearchConfiguration elasticsearch;

  public String getPerryApiUrl() {
    return perryApiUrl;
  }

  public void setPerryApiUrl(String perryApiUrl) {
    this.perryApiUrl = perryApiUrl;
  }

  public String getPerryApiUser() {
    return perryApiUser;
  }

  public void setPerryApiUser(String perryApiUser) {
    this.perryApiUser = perryApiUser;
  }

  public String getPerryApiPassword() {
    return perryApiPassword;
  }

  public void setPerryApiPassword(String perryApiPassword) {
    this.perryApiPassword = perryApiPassword;
  }

  public int getJerseyClientConnectTimeout() {
    return jerseyClientConnectTimeout;
  }

  public void setJerseyClientConnectTimeout(int jerseyClientConnectTimeout) {
    this.jerseyClientConnectTimeout = jerseyClientConnectTimeout;
  }

  public int getJerseyClientReadTimeout() {
    return jerseyClientReadTimeout;
  }

  public void setJerseyClientReadTimeout(int jerseyClientReadTimeout) {
    this.jerseyClientReadTimeout = jerseyClientReadTimeout;
  }

  public int getElasticSearchBulkSize() {
    return elasticSearchBulkSize;
  }

  public void setElasticSearchBulkSize(int elasticSearchBulkSize) {
    this.elasticSearchBulkSize = elasticSearchBulkSize;
  }

  public boolean isPerformanceTestMode() {
    return performanceTestMode;
  }

  public void setPerformanceTestMode(boolean performanceTestMode) {
    this.performanceTestMode = performanceTestMode;
  }

  public int getSleepTime() {
    return sleepTime;
  }

  public void setSleepTime(int sleepTime) {
    this.sleepTime = sleepTime;
  }

  public int getBatchSize() {
    return batchSize;
  }

  public void setBatchSize(int batchSize) {
    this.batchSize = batchSize;
  }

  @JsonProperty
  public DataSourceFactory getCmsDataSourceFactory() {
    return cmsDataSourceFactory;
  }

  public void setCmsDataSourceFactory(DataSourceFactory cmsDataSourceFactory) {
    this.cmsDataSourceFactory = cmsDataSourceFactory;
  }

  @JsonProperty
  public ElasticsearchConfiguration getElasticsearch() {
    return elasticsearch;
  }

  public void setElasticsearch(ElasticsearchConfiguration elasticsearch) {
    this.elasticsearch = elasticsearch;
  }

}
