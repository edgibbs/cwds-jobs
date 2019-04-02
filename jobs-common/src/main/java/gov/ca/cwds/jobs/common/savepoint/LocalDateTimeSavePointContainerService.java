package gov.ca.cwds.jobs.common.savepoint;

import com.google.inject.Inject;
import gov.ca.cwds.jobs.common.inject.LastRunDir;
import gov.ca.cwds.jobs.common.mode.JobMode;
import java.time.LocalDateTime;

/**
 * Created by Alexander Serbin on 6/21/2018.
 */
public class LocalDateTimeSavePointContainerService extends
    SavePointContainerServiceImpl<TimestampSavePoint<LocalDateTime>> {

  public static final LocalDateTime VERY_FIRST_TIMESTAMP = LocalDateTime.of(1970, 1, 1, 1, 1);

  @Inject
  public LocalDateTimeSavePointContainerService(@LastRunDir String outputDir) {
    super(outputDir);
  }

  @Override
  public SavePointContainer<? extends TimestampSavePoint<LocalDateTime>> readSavePointContainer(
      Class<? extends SavePointContainer<? extends TimestampSavePoint<LocalDateTime>>> savePointContainerClass) {
    if (savePointContainerExists()) {
      return super.readSavePointContainer(savePointContainerClass);
    } else {
      LocalDateTimeSavePointContainer container = new LocalDateTimeSavePointContainer();
      container.setJobMode(JobMode.INITIAL_LOAD);
      LocalDateTimeSavePoint savePoint = new LocalDateTimeSavePoint();
      savePoint.setTimestamp(VERY_FIRST_TIMESTAMP);
      container.setSavePoint(savePoint);
      return container;
    }
  }
}

