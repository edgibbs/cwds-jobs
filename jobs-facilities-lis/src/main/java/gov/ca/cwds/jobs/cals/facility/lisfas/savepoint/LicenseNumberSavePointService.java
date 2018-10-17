package gov.ca.cwds.jobs.cals.facility.lisfas.savepoint;

import com.google.inject.Inject;
import gov.ca.cwds.jobs.cals.facility.lisfas.mode.LisJobModeService;
import gov.ca.cwds.jobs.common.mode.DefaultJobMode;
import gov.ca.cwds.jobs.common.savepoint.SavePointContainer;
import gov.ca.cwds.jobs.common.savepoint.SavePointServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by Alexander Serbin on 6/29/2018.
 */
public class LicenseNumberSavePointService extends
    SavePointServiceImpl<LicenseNumberSavePoint, DefaultJobMode> {

  private static final Logger LOGGER = LoggerFactory
      .getLogger(LicenseNumberSavePointService.class);

  @Inject
  private LisJobModeService jobModeService;

  @Inject
  private LicenseNumberSavePointContainerService savePointContainerService;

  @Override
  public void saveSavePoint(LicenseNumberSavePoint savePoint) {
    if (savePoint.getLicenseNumber() != 0) {
      DefaultJobMode jobMode = jobModeService.getCurrentJobMode();
      LicenseNumberSavePointContainer savePointContainer = new LicenseNumberSavePointContainer();
      savePointContainer.setJobMode(jobMode);
      savePointContainer.setSavePoint(savePoint);
      savePointContainerService.writeSavePointContainer(savePointContainer);
    } else {
      LOGGER.info("Save point is empty. Ignoring it");
    }
  }

  @Override
  public Class<? extends SavePointContainer<? extends LicenseNumberSavePoint, DefaultJobMode>> getSavePointContainerClass() {
    return LicenseNumberSavePointContainer.class;
  }
}
