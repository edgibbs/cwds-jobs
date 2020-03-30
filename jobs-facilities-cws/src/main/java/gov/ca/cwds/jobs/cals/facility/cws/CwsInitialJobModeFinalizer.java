package gov.ca.cwds.jobs.cals.facility.cws;

import static gov.ca.cwds.cals.Constants.UnitOfWork.CMS;

import java.time.LocalDateTime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;

import gov.ca.cwds.jobs.cals.facility.cws.savepoint.CwsTimestampSavePointService;
import gov.ca.cwds.jobs.common.inject.PrimaryContainerService;
import gov.ca.cwds.jobs.common.mode.JobMode;
import gov.ca.cwds.jobs.common.mode.JobModeFinalizer;
import gov.ca.cwds.jobs.common.savepoint.LocalDateTimeSavePoint;
import gov.ca.cwds.jobs.common.savepoint.LocalDateTimeSavePointContainer;
import gov.ca.cwds.jobs.common.savepoint.SavePointContainerService;
import gov.ca.cwds.jobs.common.savepoint.TimestampSavePoint;
import io.dropwizard.hibernate.UnitOfWork;

/**
 * Created by Alexander Serbin on 7/1/2018.
 */
public class CwsInitialJobModeFinalizer implements JobModeFinalizer {

  private static final Logger LOGGER = LoggerFactory.getLogger(CwsInitialJobModeFinalizer.class);

  @Inject
  private CwsTimestampSavePointService savePointService;

  @Inject
  @PrimaryContainerService
  private SavePointContainerService<TimestampSavePoint<LocalDateTime>> savePointContainerService;

  @Override
  @UnitOfWork(CMS)
  public void doFinalizeJob() {
    LocalDateTimeSavePoint timestampSavePoint = savePointService.findFirstIncrementalSavePoint();
    LOGGER.info("Updating job save point to the last batch save point {}", timestampSavePoint);

    JobMode nextJobMode = JobMode.INCREMENTAL_LOAD;
    LOGGER.info("Updating next job mode to the {}", nextJobMode);

    LocalDateTimeSavePointContainer savePointContainer = new LocalDateTimeSavePointContainer();
    savePointContainer.setJobMode(nextJobMode);
    savePointContainer.setSavePoint(timestampSavePoint);
    savePointContainerService.writeSavePointContainer(savePointContainer);
  }

}
