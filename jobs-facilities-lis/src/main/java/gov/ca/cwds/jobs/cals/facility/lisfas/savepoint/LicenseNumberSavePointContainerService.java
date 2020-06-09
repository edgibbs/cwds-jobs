package gov.ca.cwds.jobs.cals.facility.lisfas.savepoint;

import com.google.inject.Inject;

import gov.ca.cwds.jobs.common.inject.LastRunDir;
import gov.ca.cwds.jobs.common.mode.JobMode;
import gov.ca.cwds.jobs.common.savepoint.SavePointContainer;
import gov.ca.cwds.jobs.common.savepoint.SavePointContainerServiceImpl;

/**
 * Created by Alexander Serbin on 6/29/2018.
 */
public class LicenseNumberSavePointContainerService
    extends SavePointContainerServiceImpl<LicenseNumberSavePoint> {

  @Inject
  public LicenseNumberSavePointContainerService(@LastRunDir String outputDir) {
    super(outputDir);
  }

  @Override
  public SavePointContainer<? extends LicenseNumberSavePoint> readSavePointContainer(
      Class<? extends SavePointContainer<? extends LicenseNumberSavePoint>> savePointContainerClass) {
    if (savePointContainerExists()) {
      return super.readSavePointContainer(savePointContainerClass);
    } else {
      final LicenseNumberSavePointContainer container = new LicenseNumberSavePointContainer();
      container.setJobMode(JobMode.INITIAL_LOAD);
      LicenseNumberSavePoint savePoint = new LicenseNumberSavePoint(0);
      container.setSavePoint(savePoint);
      return container;
    }

  }

}
