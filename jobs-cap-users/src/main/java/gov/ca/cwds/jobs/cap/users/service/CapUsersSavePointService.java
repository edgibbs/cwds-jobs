package gov.ca.cwds.jobs.cap.users.service;

import static gov.ca.cwds.jobs.cap.users.CwsCmsDataAccessModule.CWS;
import static gov.ca.cwds.jobs.common.mode.JobMode.INCREMENTAL_LOAD;

import com.google.inject.Inject;
import gov.ca.cwds.jobs.cap.users.dao.CwsUsersDao;
import gov.ca.cwds.jobs.cap.users.savepoint.CapUsersSavePoint;
import gov.ca.cwds.jobs.cap.users.savepoint.CapUsersSavePointContainer;
import gov.ca.cwds.jobs.common.savepoint.SavePointContainer;
import gov.ca.cwds.jobs.common.savepoint.SavePointContainerService;
import gov.ca.cwds.jobs.common.savepoint.SavePointServiceImpl;
import io.dropwizard.hibernate.UnitOfWork;
import java.time.LocalDateTime;

/**
 * Created by Alexander Serbin on 11/6/2018
 */
public class CapUsersSavePointService extends
    SavePointServiceImpl<CapUsersSavePoint> {

  @Inject
  private SavePointContainerService<CapUsersSavePoint> savePointContainerService;

  @Inject
  private CwsUsersDao dao;

  @Override
  public void saveSavePoint(CapUsersSavePoint savePoint) {
    SavePointContainer<CapUsersSavePoint> savePointContainer
        = new CapUsersSavePointContainer();
    savePointContainer.setJobMode(INCREMENTAL_LOAD);
    savePointContainer.setSavePoint(savePoint);
    savePointContainerService.writeSavePointContainer(savePointContainer);
  }

  @UnitOfWork(CWS)
  public CapUsersSavePoint createSavePoint() {
    CapUsersSavePoint savePoint = new CapUsersSavePoint();
    savePoint.setCognitoTimestamp(LocalDateTime.now());
    savePoint.setCwsOfficeTimestamp(dao.getOfficeMaxLastUpdatedTime());
    savePoint.setStaffPersonTimestamp(dao.getStaffPersonLastUpdatedTime());
    savePoint.setUserIdTimestamp(dao.getUserIdMaxLastUpdatedTime());
    return savePoint;
  }

  @Override
  public Class<? extends SavePointContainer<? extends CapUsersSavePoint>> getSavePointContainerClass() {
    return CapUsersSavePointContainer.class;
  }
}

