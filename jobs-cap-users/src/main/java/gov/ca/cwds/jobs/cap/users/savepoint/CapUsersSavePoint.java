package gov.ca.cwds.jobs.cap.users.savepoint;

import gov.ca.cwds.jobs.common.savepoint.SavePoint;
import java.time.LocalDateTime;

/**
 * Created by Alexander Serbin on 11/6/2018
 */
public class CapUsersSavePoint implements SavePoint {

  private LocalDateTime cognitoTimestamp;
  private LocalDateTime userIdTimestamp;
  private LocalDateTime cwsOfficeTimestamp;
  private LocalDateTime staffPersonTimestamp;

  public LocalDateTime getCognitoTimestamp() {
    return cognitoTimestamp;
  }

  public void setCognitoTimestamp(LocalDateTime cognitoTimestamp) {
    this.cognitoTimestamp = cognitoTimestamp;
  }

  public LocalDateTime getUserIdTimestamp() {
    return userIdTimestamp;
  }

  public void setUserIdTimestamp(LocalDateTime userIdTimestamp) {
    this.userIdTimestamp = userIdTimestamp;
  }

  public LocalDateTime getCwsOfficeTimestamp() {
    return cwsOfficeTimestamp;
  }

  public void setCwsOfficeTimestamp(LocalDateTime cwsOfficeTimestamp) {
    this.cwsOfficeTimestamp = cwsOfficeTimestamp;
  }

  public LocalDateTime getStaffPersonTimestamp() {
    return staffPersonTimestamp;
  }

  public void setStaffPersonTimestamp(LocalDateTime staffPersonTimestamp) {
    this.staffPersonTimestamp = staffPersonTimestamp;
  }

}
