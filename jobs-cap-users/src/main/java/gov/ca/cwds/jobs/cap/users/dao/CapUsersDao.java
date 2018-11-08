package gov.ca.cwds.jobs.cap.users.dao;

import com.google.inject.Inject;
import gov.ca.cwds.data.BaseDaoImpl;
import gov.ca.cwds.inject.CmsSessionFactory;
import gov.ca.cwds.jobs.cap.users.entity.CwsOffice;
import gov.ca.cwds.jobs.cap.users.entity.StaffPerson;
import gov.ca.cwds.jobs.cap.users.entity.UserId;
import gov.ca.cwds.jobs.cap.users.savepoint.CapUsersSavePoint;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.hibernate.SessionFactory;

public class CapUsersDao extends BaseDaoImpl {

  @Inject
  public CapUsersDao(@CmsSessionFactory SessionFactory sessionFactory) {
    super(sessionFactory);
  }

  public Set<String> getChangedRacfIds(CapUsersSavePoint savePoint) {
    List<String> racfIds = currentSession()
        .createNamedQuery(UserId.CWSCMS_INCREMENTAL_LOAD_QUERY_NAME, String.class)
        .setParameter(UserId.OFFICE_DATE_AFTER, savePoint.getCwsOfficeTimestamp())
        .setParameter(UserId.STAFF_PERSON_DATE_AFTER, savePoint.getStaffPersonTimestamp())
        .setParameter(UserId.USERID_DATE_AFTER, savePoint.getUserIdTimestamp())
        .list();

    return new HashSet<>(racfIds);
  }

  public LocalDateTime getUserIdMaxLastUpdatedTime() {
    return getMaxLastUpdatedTime(UserId.GET_MAX_LAST_UPDATED_TIME);
  }

  public LocalDateTime getOfficeMaxLastUpdatedTime() {
    return getMaxLastUpdatedTime(CwsOffice.GET_MAX_LAST_UPDATED_TIME);
  }

  public LocalDateTime getStaffPersonLastUpdatedTime() {
    return getMaxLastUpdatedTime(StaffPerson.GET_MAX_LAST_UPDATED_TIME);
  }

  private LocalDateTime getMaxLastUpdatedTime(String query) {
    return currentSession().createNamedQuery(query, LocalDateTime.class).uniqueResult();
  }
}
