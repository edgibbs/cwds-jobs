package gov.ca.cwds.jobs.cap.users.dto;

import java.io.Serializable;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public class CapJobResult implements Serializable {

  private static final long serialVersionUID = -3388974376628728605L;

  private boolean cwsPartSuccess;
  private boolean capPartSuccess;

  public CapJobResult(boolean cwsPartSuccess, boolean capPartSuccess) {
    this.cwsPartSuccess = cwsPartSuccess;
    this.capPartSuccess = capPartSuccess;
  }

  public boolean isCwsPartSuccess() {
    return cwsPartSuccess;
  }

  public void setCwsPartSuccess(boolean cwsPartSuccess) {
    this.cwsPartSuccess = cwsPartSuccess;
  }

  public boolean isCapPartSuccess() {
    return capPartSuccess;
  }

  public void setCapPartSuccess(boolean capPartSuccess) {
    this.capPartSuccess = capPartSuccess;
  }

  @Override
  public boolean equals(Object o) {
    return EqualsBuilder.reflectionEquals(this, o);
  }

  @Override
  public int hashCode() {
    return HashCodeBuilder.reflectionHashCode(this);
  }
}
