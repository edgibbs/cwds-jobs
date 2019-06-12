package gov.ca.cwds.jobs.cap.users.service;

import com.google.inject.Inject;
import gov.ca.cwds.jobs.cap.users.savepoint.CapUsersSavePoint;
import gov.ca.cwds.jobs.common.inject.LastRunDir;
import gov.ca.cwds.jobs.common.savepoint.SavePointContainerServiceImpl;

/**
 * Created by Alexander Serbin on 11/6/2018
 */
public class CapUsersSavePointContainerService extends
    SavePointContainerServiceImpl<CapUsersSavePoint> {

  @Inject
  public CapUsersSavePointContainerService(@LastRunDir String outputDir) {
    super(outputDir);
  }

}

