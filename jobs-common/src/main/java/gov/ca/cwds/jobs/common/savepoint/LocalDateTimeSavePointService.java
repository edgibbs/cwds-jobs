package gov.ca.cwds.jobs.common.savepoint;

import com.google.inject.Inject;
import gov.ca.cwds.jobs.common.mode.DefaultJobMode;
import gov.ca.cwds.jobs.common.mode.LocalDateTimeDefaultJobModeService;
import java.time.LocalDateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by Alexander Serbin on 6/29/2018.
 */
public class LocalDateTimeSavePointService extends
    SavePointServiceImpl<TimestampSavePoint<LocalDateTime>, DefaultJobMode> {

  private static final Logger LOGGER = LoggerFactory
      .getLogger(LocalDateTimeSavePointService.class);

  @Inject
  private LocalDateTimeDefaultJobModeService jobModeService;

  @Inject
  private SavePointContainerService<TimestampSavePoint<LocalDateTime>, DefaultJobMode> savePointContainerService;

  @Override
  public void saveSavePoint(TimestampSavePoint<LocalDateTime> savePoint) {
    if (savePoint.getTimestamp() != null) {
      DefaultJobMode jobMode = jobModeService.getCurrentJobMode();
      SavePointContainer<LocalDateTimeSavePoint, DefaultJobMode> savePointContainer
          = new LocalDateTimeSavePointContainer();
      savePointContainer.setJobMode(jobMode);
      savePointContainer.setSavePoint((LocalDateTimeSavePoint) savePoint);
      savePointContainerService.writeSavePointContainer(savePointContainer);
    } else {
      LOGGER.info("Save point is empty. Ignoring it");
    }
  }

  @Override
  public Class<? extends SavePointContainer<? extends TimestampSavePoint<LocalDateTime>, DefaultJobMode>> getSavePointContainerClass() {
    return LocalDateTimeSavePointContainer.class;
  }

}
