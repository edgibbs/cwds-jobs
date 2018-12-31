package gov.ca.cwds.jobs.audit.event;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

/**
 * Created by Alexander Serbin on 12/30/2018
 */
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public abstract class CustomEvent {

  private int countyType;
  private String role;

  public int getCountyType() {
    return countyType;
  }

  public void setCountyType(int countyType) {
    this.countyType = countyType;
  }

  public String getRole() {
    return role;
  }

  public void setRole(String role) {
    this.role = role;
  }

}
