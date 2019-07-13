package gov.ca.cwds.jobs.cap.users.report;

import java.util.Objects;

public class Utils {

  private Utils(){}

  public static String emptyIfNull(String value) {
    return Objects.toString(value, "");
  }
}
