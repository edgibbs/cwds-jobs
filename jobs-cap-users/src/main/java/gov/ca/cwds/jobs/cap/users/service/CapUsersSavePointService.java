package gov.ca.cwds.jobs.cap.users.service;

import static gov.ca.cwds.jobs.cap.users.CwsCmsDataAccessModule.CWS;
import static gov.ca.cwds.jobs.common.mode.DefaultJobMode.INCREMENTAL_LOAD;

import com.google.inject.Inject;
import gov.ca.cwds.jobs.cap.users.dao.CapUsersDao;
import gov.ca.cwds.jobs.cap.users.savepoint.CapUsersSavePoint;
import gov.ca.cwds.jobs.cap.users.savepoint.CapUsersSavePointContainer;
import gov.ca.cwds.jobs.common.mode.DefaultJobMode;
import gov.ca.cwds.jobs.common.savepoint.SavePointContainer;
import gov.ca.cwds.jobs.common.savepoint.SavePointContainerService;
import gov.ca.cwds.jobs.common.savepoint.SavePointServiceImpl;
import io.dropwizard.hibernate.UnitOfWork;
import java.time.LocalDateTime;

/**
 * Created by Alexander Serbin on 11/6/2018
 */
public class CapUsersSavePointService extends
    SavePointServiceImpl<CapUsersSavePoint, DefaultJobMode> {

  @Inject
  private SavePointContainerService<CapUsersSavePoint, DefaultJobMode> savePointContainerService;

  @Inject
  private CapUsersDao dao;

  @Override
  public void saveSavePoint(CapUsersSavePoint savePoint) {
    SavePointContainer<CapUsersSavePoint, DefaultJobMode> savePointContainer
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
  public Class<? extends SavePointContainer<? extends CapUsersSavePoint, DefaultJobMode>> getSavePointContainerClass() {
    return CapUsersSavePointContainer.class;
  }
}

