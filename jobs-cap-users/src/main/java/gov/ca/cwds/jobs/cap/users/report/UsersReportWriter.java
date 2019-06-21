package gov.ca.cwds.jobs.cap.users.report;

import gov.ca.cwds.jobs.cap.users.dto.ChangedUserDto;
import gov.ca.cwds.jobs.common.BulkWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class UsersReportWriter implements BulkWriter<ChangedUserDto> {

  private StringBuilder stringBuilder;
  private UsersReportBuilder usersReportBuilder;

  public UsersReportWriter() {
    stringBuilder = new StringBuilder();
    usersReportBuilder = new UsersReportBuilder();
    stringBuilder.append(usersReportBuilder.buildHeader());
  }

  @Override
  public void write(List<ChangedUserDto> items) {
    stringBuilder.append(usersReportBuilder.buildRows(items));
  }

  @Override
  public void destroy() {

  }

  @Override
  public void flush() {
    String reportStr = stringBuilder.toString();

    try {
      Files.write(Paths.get("./cap_users_report.csv"), reportStr.getBytes(StandardCharsets.UTF_8));
    } catch (IOException e) {
      throw new RuntimeException("Error at writing users report to file", e);
    }
  }
}
