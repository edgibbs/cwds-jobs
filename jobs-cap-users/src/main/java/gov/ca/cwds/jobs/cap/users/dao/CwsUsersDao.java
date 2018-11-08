package gov.ca.cwds.jobs.cap.users.dao;

import com.google.inject.Inject;
import gov.ca.cwds.data.BaseDaoImpl;
import gov.ca.cwds.inject.CmsSessionFactory;
import gov.ca.cwds.jobs.cap.users.entity.UserId;
import org.hibernate.SessionFactory;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CwsUsersDao extends BaseDaoImpl {

  @Inject
  public CwsUsersDao(@CmsSessionFactory SessionFactory sessionFactory) {
    super(sessionFactory);
  }


  public Set<String> getChangedRacfIds(LocalDateTime savePointTime) {
    List<String> racfIds = grabSession()
            .createNamedQuery(UserId.CWSCMS_INCREMENTAL_LOAD_QUERY_NAME, String.class)
            .setParameter("dateAfter", savePointTime)
            .list();

    return new HashSet<>(racfIds);
  }

  public Set<String> getAllRacfIds() {
    List<String> racfIds = grabSession()
        .createNamedQuery(UserId.CWSCMS_All_RACFIDS_QUERY_NAME, String.class)
        .list();

    return new HashSet<>(racfIds);
  }
}
