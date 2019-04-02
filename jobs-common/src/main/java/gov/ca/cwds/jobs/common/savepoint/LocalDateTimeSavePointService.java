package gov.ca.cwds.jobs.common.savepoint;

import com.google.inject.Inject;
import gov.ca.cwds.jobs.common.mode.JobMode;
import gov.ca.cwds.jobs.common.mode.LocalDateTimeJobModeService;
import java.time.LocalDateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by Alexander Serbin on 6/29/2018.
 */
public class LocalDateTimeSavePointService extends
    SavePointServiceImpl<TimestampSavePoint<LocalDateTime>> {

  private static final Logger LOGGER = LoggerFactory
      .getLogger(LocalDateTimeSavePointService.class);

  @Inject
  private LocalDateTimeJobModeService jobModeService;

  @Inject
  private SavePointContainerService<TimestampSavePoint<LocalDateTime>> savePointContainerService;

  @Override
  public void saveSavePoint(TimestampSavePoint<LocalDateTime> savePoint) {
    if (savePoint.getTimestamp() != null) {
      JobMode jobMode = jobModeService.getCurrentJobMode();
      SavePointContainer<LocalDateTimeSavePoint> savePointContainer
          = new LocalDateTimeSavePointContainer();
      savePointContainer.setJobMode(jobMode);
      savePointContainer.setSavePoint((LocalDateTimeSavePoint) savePoint);
      savePointContainerService.writeSavePointContainer(savePointContainer);
    } else {
      LOGGER.info("Save point is empty. Ignoring it");
    }
  }

  @Override
  public Class<? extends SavePointContainer<? extends TimestampSavePoint<LocalDateTime>>> getSavePointContainerClass() {
    return LocalDateTimeSavePointContainer.class;
  }

}
