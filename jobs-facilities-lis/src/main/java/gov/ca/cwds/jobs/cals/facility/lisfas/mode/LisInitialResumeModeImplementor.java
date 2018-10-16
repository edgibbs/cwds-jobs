package gov.ca.cwds.jobs.cals.facility.lisfas.mode;

import gov.ca.cwds.jobs.cals.facility.lisfas.savepoint.LicenseNumberSavePoint;
import gov.ca.cwds.jobs.cals.facility.lisfas.savepoint.LicenseNumberSavePointContainer;
import gov.ca.cwds.jobs.common.mode.DefaultJobMode;
import gov.ca.cwds.jobs.common.savepoint.SavePointContainer;

/**
 * Created by Alexander Serbin on 6/29/2018.
 */
public class LisInitialResumeModeImplementor extends AbstractLisInitialModeImplementor {

  @Override
  public void init() {
    lastId = loadSavePoint().getLicenseNumber();
  }

  @Override
  public Class<? extends SavePointContainer<? extends LicenseNumberSavePoint, DefaultJobMode>> getSavePointContainerClass() {
    return LicenseNumberSavePointContainer.class;
  }
}
