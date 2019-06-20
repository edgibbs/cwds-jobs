package gov.ca.cwds.jobs.cap.users.report;

import com.google.inject.Inject;
import gov.ca.cwds.jobs.common.inject.SecondaryFinalizer;
import gov.ca.cwds.jobs.common.mode.JobModeFinalizer;


public class UsersReportFinalizer implements JobModeFinalizer {

  @Inject
  @SecondaryFinalizer
  private JobModeFinalizer jobModeFinalizer;

  @Override
  public void doFinalizeJob() {
    jobModeFinalizer.doFinalizeJob();

  }
}
