package gov.ca.cwds.jobs.cals.facility.lisfas.savepoint;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;

import gov.ca.cwds.jobs.cals.facility.lisfas.mode.LisJobModeService;
import gov.ca.cwds.jobs.common.inject.PrimaryContainerService;
import gov.ca.cwds.jobs.common.mode.JobMode;
import gov.ca.cwds.jobs.common.savepoint.SavePointContainer;
import gov.ca.cwds.jobs.common.savepoint.SavePointContainerService;
import gov.ca.cwds.jobs.common.savepoint.SavePointServiceImpl;

/**
 * Created by Alexander Serbin on 6/29/2018.
 */
public class LicenseNumberSavePointService extends SavePointServiceImpl<LicenseNumberSavePoint> {

  private static final Logger LOGGER = LoggerFactory.getLogger(LicenseNumberSavePointService.class);

  @Inject
  private LisJobModeService jobModeService;

  @Inject
  @PrimaryContainerService
  private SavePointContainerService<LicenseNumberSavePoint> savePointContainerService;

  @Override
  public void saveSavePoint(LicenseNumberSavePoint savePoint) {
    if (savePoint.getLicenseNumber() != 0) {
      final JobMode jobMode = jobModeService.getCurrentJobMode();
      final LicenseNumberSavePointContainer savePointContainer =
          new LicenseNumberSavePointContainer();

      savePointContainer.setJobMode(jobMode);
      savePointContainer.setSavePoint(savePoint);
      savePointContainerService.writeSavePointContainer(savePointContainer);
    } else {
      LOGGER.info("Save point is empty. Ignoring it");
    }
  }

  @Override
  public Class<? extends SavePointContainer<? extends LicenseNumberSavePoint>> getSavePointContainerClass() {
    return LicenseNumberSavePointContainer.class;
  }

}
