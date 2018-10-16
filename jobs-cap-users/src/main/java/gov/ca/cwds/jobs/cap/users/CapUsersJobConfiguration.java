package gov.ca.cwds.jobs.cap.users;

import com.fasterxml.jackson.annotation.JsonProperty;
import gov.ca.cwds.jobs.common.configuration.MultiThreadJobConfiguration;
import io.dropwizard.db.DataSourceFactory;

public class CapUsersJobConfiguration extends MultiThreadJobConfiguration {

  private String perryApiUrl;
  private String perryApiUser;
  private String perryApiPassword;
  private int jerseyClientConnectTimeout;
  private int jerseyClientReadTimeout;
  private DataSourceFactory cmsDataSourceFactory;

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

  @JsonProperty
  public DataSourceFactory getCmsDataSourceFactory() {
    return cmsDataSourceFactory;
  }

  public void setCmsDataSourceFactory(DataSourceFactory cmsDataSourceFactory) {
    this.cmsDataSourceFactory = cmsDataSourceFactory;
  }
}
