package gov.ca.cwds.jobs.cap.users.report;

import gov.ca.cwds.jobs.cap.users.dto.ChangedUserDto;
import gov.ca.cwds.jobs.common.BulkWriter;
import java.util.List;

public class UsersReportFileWriter implements BulkWriter<ChangedUserDto>{

  @Override
  public void write(List<ChangedUserDto> items) {

  }

  @Override
  public void destroy() {

  }
}
