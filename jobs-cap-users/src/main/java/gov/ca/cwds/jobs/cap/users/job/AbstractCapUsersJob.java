package gov.ca.cwds.jobs.cap.users.job;

import com.google.inject.Inject;
import gov.ca.cwds.jobs.common.core.Job;
import gov.ca.cwds.jobs.common.mode.DefaultJobMode;
import gov.ca.cwds.jobs.common.savepoint.LocalDateTimeSavePoint;
import gov.ca.cwds.jobs.common.savepoint.LocalDateTimeSavePointContainer;
import gov.ca.cwds.jobs.common.savepoint.SavePointContainerService;
import gov.ca.cwds.jobs.common.savepoint.TimestampSavePoint;
import gov.ca.cwds.jobs.common.timereport.JobTimeReport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;

public abstract class AbstractCapUsersJob implements Job {
  private static final Logger LOGGER = LoggerFactory.getLogger(AbstractCapUsersJob.class);

  @Inject
  private SavePointContainerService<TimestampSavePoint<LocalDateTime>, DefaultJobMode> savePointContainerService;

  @Override
  public void run() {
    JobTimeReport jobTimeReport = new JobTimeReport();
    runJob();
    if (LOGGER.isInfoEnabled()) {
      jobTimeReport.printTimeSpent();
    }
  }

  abstract void runJob();

  void createSavePoint(LocalDateTime dateTime){
    LocalDateTimeSavePoint timestampSavePoint = new LocalDateTimeSavePoint(dateTime);
    DefaultJobMode nextJobMode = DefaultJobMode.INCREMENTAL_LOAD;
    LocalDateTimeSavePointContainer savePointContainer = new LocalDateTimeSavePointContainer();
    savePointContainer.setJobMode(nextJobMode);
    savePointContainer.setSavePoint(timestampSavePoint);
    savePointContainerService.writeSavePointContainer(savePointContainer);
  }
}
