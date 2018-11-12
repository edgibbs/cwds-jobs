package gov.ca.cwds.jobs.cap.users.dto;

import java.io.Serializable;

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
}
