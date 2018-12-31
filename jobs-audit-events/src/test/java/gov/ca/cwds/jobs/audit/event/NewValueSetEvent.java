package gov.ca.cwds.jobs.audit.event;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

/**
 * Created by Alexander Serbin on 12/30/2018
 */
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class NewValueSetEvent extends CustomEvent {

  private String oldValue;
  private String newValue;

  public String getOldValue() {
    return oldValue;
  }

  public void setOldValue(String oldValue) {
    this.oldValue = oldValue;
  }

  public String getNewValue() {
    return newValue;
  }

  public void setNewValue(String newValue) {
    this.newValue = newValue;
  }
}
