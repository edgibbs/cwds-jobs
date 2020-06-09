package gov.ca.cwds.jobs.common.mode;

import java.time.LocalDateTime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;

import gov.ca.cwds.jobs.common.inject.PrimaryContainerService;
import gov.ca.cwds.jobs.common.savepoint.LocalDateTimeSavePoint;
import gov.ca.cwds.jobs.common.savepoint.LocalDateTimeSavePointContainer;
import gov.ca.cwds.jobs.common.savepoint.SavePointContainerService;
import gov.ca.cwds.jobs.common.savepoint.SavePointService;
import gov.ca.cwds.jobs.common.savepoint.TimestampSavePoint;

/**
 * Created by Alexander Serbin on 6/30/2018.
 */
public class LocalDateTimeJobModeFinalizer implements JobModeFinalizer {

  private static final Logger LOGGER = LoggerFactory.getLogger(LocalDateTimeJobModeFinalizer.class);

  @Inject
  @PrimaryContainerService
  private SavePointContainerService<TimestampSavePoint<LocalDateTime>> savePointContainerService;

  @Inject
  private SavePointService<TimestampSavePoint<LocalDateTime>> savePointService;

  @Override
  public void doFinalizeJob() {
    final LocalDateTimeSavePoint timestampSavePoint =
        (LocalDateTimeSavePoint) savePointService.loadSavePoint();
    LOGGER.info("Updating job save point to the last batch save point {}", timestampSavePoint);

    final JobMode nextJobMode = JobMode.INCREMENTAL_LOAD;
    LOGGER.info("Updating next job mode to the {}", nextJobMode);

    final LocalDateTimeSavePointContainer savePointContainer =
        new LocalDateTimeSavePointContainer();
    savePointContainer.setJobMode(nextJobMode);
    savePointContainer.setSavePoint(timestampSavePoint);
    savePointContainerService.writeSavePointContainer(savePointContainer);
  }

}
