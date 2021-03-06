package gov.ca.cwds.jobs.cals.facility.lisfas.savepoint;

import com.google.inject.Inject;
import gov.ca.cwds.jobs.cals.facility.lisfas.mode.LisJobModeService;
import gov.ca.cwds.jobs.common.inject.PrimaryContainerService;
import gov.ca.cwds.jobs.common.mode.JobMode;
import gov.ca.cwds.jobs.common.savepoint.SavePointContainer;
import gov.ca.cwds.jobs.common.savepoint.SavePointContainerService;
import gov.ca.cwds.jobs.common.savepoint.SavePointServiceImpl;
import gov.ca.cwds.jobs.common.savepoint.TimestampSavePoint;
import java.math.BigInteger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by Alexander Serbin on 6/29/2018.
 */
public class LisTimestampSavePointService extends
    SavePointServiceImpl<TimestampSavePoint<BigInteger>> {

  private static final Logger LOGGER = LoggerFactory
      .getLogger(LisTimestampSavePointService.class);

  @Inject
  private LisJobModeService jobModeService;

  @Inject
  @PrimaryContainerService
  private SavePointContainerService<TimestampSavePoint<BigInteger>> savePointContainerService;

  @Override
  public void saveSavePoint(TimestampSavePoint<BigInteger> savePoint) {
    if (savePoint.getTimestamp() != null) {
      JobMode jobMode = jobModeService.getCurrentJobMode();
      SavePointContainer<LisTimestampSavePoint> savePointContainer = new LisTimestampSavePointContainer();
      savePointContainer.setJobMode(jobMode);
      savePointContainer.setSavePoint((LisTimestampSavePoint) savePoint);
      savePointContainerService.writeSavePointContainer(savePointContainer);
    } else {
      LOGGER.info("Save point is empty. Ignoring it");
    }
  }

  @Override
  public Class<? extends SavePointContainer<? extends TimestampSavePoint<BigInteger>>> getSavePointContainerClass() {
    return LisTimestampSavePointContainer.class;
  }

}
