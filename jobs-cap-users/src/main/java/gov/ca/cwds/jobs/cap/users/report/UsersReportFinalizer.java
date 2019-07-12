package gov.ca.cwds.jobs.cap.users.report;

import com.google.inject.Inject;
import gov.ca.cwds.jobs.cap.users.dto.ChangedUserDto;
import gov.ca.cwds.jobs.common.BulkWriter;
import gov.ca.cwds.jobs.common.mode.JobModeFinalizer;

public class UsersReportFinalizer implements JobModeFinalizer {

  @Inject
  private BulkWriter<ChangedUserDto> jobWriter;


  @Override
  public void doFinalizeJob() {
    jobWriter.flush();
  }
}
