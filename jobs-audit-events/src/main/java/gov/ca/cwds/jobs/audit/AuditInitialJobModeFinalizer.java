package gov.ca.cwds.jobs.audit;

import com.google.inject.Inject;
import gov.ca.cwds.jobs.audit.inject.NsDataAccessModule;
import gov.ca.cwds.jobs.common.mode.DefaultJobMode;
import gov.ca.cwds.jobs.common.mode.JobModeFinalizer;
import gov.ca.cwds.jobs.common.savepoint.LocalDateTimeSavePoint;
import gov.ca.cwds.jobs.common.savepoint.LocalDateTimeSavePointContainer;
import gov.ca.cwds.jobs.common.savepoint.LocalDateTimeSavePointContainerService;
import gov.ca.cwds.jobs.common.savepoint.SavePointService;
import gov.ca.cwds.jobs.common.savepoint.TimestampSavePoint;
import io.dropwizard.hibernate.UnitOfWork;
import java.time.LocalDateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by Alexander Serbin on 02/17/2018.
 */
public class AuditInitialJobModeFinalizer implements JobModeFinalizer {

  private static final Logger LOGGER = LoggerFactory
      .getLogger(AuditInitialJobModeFinalizer.class);

  @Inject
  private LocalDateTimeSavePointContainerService savePointContainerService;

  @Inject
  private SavePointService<TimestampSavePoint<LocalDateTime>, DefaultJobMode> savePointService;

  @Override
  @UnitOfWork(NsDataAccessModule.NS)
  public void doFinalizeJob() {
    DefaultJobMode nextJobMode = DefaultJobMode.INCREMENTAL_LOAD;
    LOGGER.info("Updating next job mode to the {}", nextJobMode);
    LocalDateTimeSavePointContainer savePointContainer = new LocalDateTimeSavePointContainer();
    savePointContainer.setJobMode(nextJobMode);
    LocalDateTimeSavePoint savePoint = new LocalDateTimeSavePoint();
    savePoint.setTimestamp(savePointService.loadSavePoint().getTimestamp());
    savePointContainer.setSavePoint(savePoint);
    savePointContainerService.writeSavePointContainer(savePointContainer);
  }
}
