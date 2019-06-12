package gov.ca.cwds.jobs.cap.users.job;

import com.google.inject.Inject;
import gov.ca.cwds.jobs.cap.users.dto.CapJobResult;
import gov.ca.cwds.jobs.cap.users.savepoint.CapUsersSavePoint;
import gov.ca.cwds.jobs.cap.users.service.CapUsersSavePointService;
import gov.ca.cwds.jobs.common.core.Job;
import gov.ca.cwds.jobs.common.timereport.JobTimeReport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractCapUsersJob implements Job {

  private static final Logger LOGGER = LoggerFactory.getLogger(AbstractCapUsersJob.class);

  @Inject
  private CapUsersSavePointService savePointService;

  @Override
  public void run() {
    JobTimeReport jobTimeReport = new JobTimeReport();
    CapUsersSavePoint newSavePoint = savePointService.createSavePoint();
    CapJobResult jobResult = runJob();
    processJobResult(jobResult, newSavePoint);
    if (LOGGER.isInfoEnabled()) {
      jobTimeReport.printTimeSpent();
    }
  }

  private void processJobResult(CapJobResult jobResult,
      CapUsersSavePoint newSavePoint) {
    if (!jobResult.isCwsPartSuccess() || !jobResult.isCapPartSuccess()) {
      newSavePoint = editSavePoint(jobResult, newSavePoint);
    }
    LOGGER.info("Creating save point savePoint {}", newSavePoint);
    savePointService.saveSavePoint(newSavePoint);
  }

  private CapUsersSavePoint editSavePoint(CapJobResult jobResult, CapUsersSavePoint savePoint) {
    CapUsersSavePoint previousSavePoint = savePointService.loadSavePoint();
    if (!jobResult.isCapPartSuccess()) {
      savePoint.setCognitoTimestamp(previousSavePoint.getCognitoTimestamp());
    }
    if (!jobResult.isCwsPartSuccess()) {
      savePoint.setCwsOfficeTimestamp(previousSavePoint.getCwsOfficeTimestamp());
      savePoint.setStaffPersonTimestamp(previousSavePoint.getStaffPersonTimestamp());
      savePoint.setUserIdTimestamp(previousSavePoint.getUserIdTimestamp());
    }
    return savePoint;
  }

  abstract CapJobResult runJob();

}
