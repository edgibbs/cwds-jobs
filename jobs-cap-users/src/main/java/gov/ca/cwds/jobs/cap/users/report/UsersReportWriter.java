package gov.ca.cwds.jobs.cap.users.report;

import gov.ca.cwds.jobs.cap.users.dto.ChangedUserDto;
import gov.ca.cwds.jobs.common.BulkWriter;
import java.util.List;

public class UsersReportWriter implements BulkWriter<ChangedUserDto> {

  @Override
  public void write(List<ChangedUserDto> items) {
    System.out.println("write report items");
  }

  @Override
  public void destroy() {
    System.out.println("destroying report writer");
  }
}
