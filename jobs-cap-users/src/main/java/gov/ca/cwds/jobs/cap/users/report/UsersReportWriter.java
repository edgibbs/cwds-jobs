package gov.ca.cwds.jobs.cap.users.report;

import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.ObjectMetadata;
import gov.ca.cwds.jobs.cap.users.dto.ChangedUserDto;
import gov.ca.cwds.jobs.common.BulkWriter;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UsersReportWriter implements BulkWriter<ChangedUserDto> {

  private static final Logger LOGGER = LoggerFactory.getLogger(UsersReportWriter.class);

  public static final String REPORT_FILE_PATH = "./";
  public static final String REPORT_FILE_NAME = "CapUsersReport";
  public static final String REPORT_FILE_EXT = ".csv";
  public static final DateTimeFormatter TIMESTAMP_FORMATTER =
      DateTimeFormatter.ofPattern("_yyyy-MM-dd_HH-mm");

  public static final String COGNITO_CLIENT_REGION = "us-east-2";
  public static final String COGNITO_IAM_ACCESS_ID = "*********";
  public static final String COGNITO_IAM_SECRET_KEY = "******************";
  public static final String S3_BUCKET_NAME = "cap-users-reports";
  public static final Charset ENCODING = StandardCharsets.UTF_8;

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

    AWSCredentialsProvider credentialsProvider =
        new AWSStaticCredentialsProvider(
            new BasicAWSCredentials(COGNITO_IAM_ACCESS_ID, COGNITO_IAM_SECRET_KEY));

    AmazonS3 s3Client = AmazonS3ClientBuilder.standard()
        .withRegion(COGNITO_CLIENT_REGION)
        .withCredentials(credentialsProvider)
        .build();

    String filename = createReportFileName();
    String reportStr = stringBuilder.toString();
    InputStream inputStream = IOUtils.toInputStream(reportStr, ENCODING);

    ObjectMetadata metadata = new ObjectMetadata();
    metadata.setContentType("plain/text");
    long contentLength = reportStr.getBytes(ENCODING).length;
    metadata.setContentLength(contentLength);

    s3Client.putObject(S3_BUCKET_NAME, filename, inputStream, metadata);

    LOGGER.info("CAP users report with filename {} is successfully created ", filename);
  }

  private String createReportFileName() {
    return REPORT_FILE_NAME + getTimestampString() + REPORT_FILE_EXT;
  }

  private String getTimestampString(){
    return LocalDateTime.now().format(TIMESTAMP_FORMATTER);
  }
}
