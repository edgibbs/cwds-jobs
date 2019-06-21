package gov.ca.cwds.jobs.cap.users.report;

import gov.ca.cwds.jobs.cap.users.dto.ChangedUserDto;
import gov.ca.cwds.jobs.common.BulkWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UsersReportWriter implements BulkWriter<ChangedUserDto> {

  private static final Logger LOGGER = LoggerFactory.getLogger(UsersReportWriter.class);

  public static final String REPORT_FILE_PATH = "./";
  public static final String REPORT_FILE_NAME = "CapUsersReport";
  public static final String REPORT_FILE_EXT = ".csv";
  public static final DateTimeFormatter TIMESTAMP_FORMATTER =
      DateTimeFormatter.ofPattern("_yyyy-MM-dd_HH-mm");

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
  @SuppressWarnings("findsecbugs:PATH_TRAVERSAL_IN")//filename is hardcoded
  public void flush() {
    String reportStr = stringBuilder.toString();
    byte[] reportBytes = reportStr.getBytes(StandardCharsets.UTF_8);
    String filename = createReportFileName();

    try {
      Files.write(Paths.get(REPORT_FILE_PATH + filename), reportBytes);
    } catch (IOException e) {
      throw new RuntimeException("IO error at writing CAP users report to the file", e);
    }
    LOGGER.info("CAP users report with filename {} is successfully created ", filename);
  }

  private String createReportFileName() {
    return REPORT_FILE_NAME + getTimestampString() + REPORT_FILE_EXT;
  }

  private String getTimestampString(){
    return LocalDateTime.now().format(TIMESTAMP_FORMATTER);
  }
}
