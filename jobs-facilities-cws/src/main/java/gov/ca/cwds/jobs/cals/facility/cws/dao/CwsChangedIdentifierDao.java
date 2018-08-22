package gov.ca.cwds.jobs.cals.facility.cws.dao;

import com.google.inject.Inject;
import gov.ca.cwds.data.BaseDaoImpl;
import gov.ca.cwds.inject.CmsSessionFactory;
import gov.ca.cwds.jobs.cals.facility.cws.identifier.CwsChangedIdentifier;
import gov.ca.cwds.jobs.common.batch.PageRequest;
import gov.ca.cwds.jobs.common.identifier.ChangedEntityIdentifier;
import gov.ca.cwds.jobs.common.savepoint.TimestampSavePoint;
import java.time.LocalDateTime;
import java.util.List;
import org.hibernate.SessionFactory;

/**
 * @author CWDS TPT-2
 */
public class CwsChangedIdentifierDao extends BaseDaoImpl<CwsChangedIdentifier> {

  @Inject
  public CwsChangedIdentifierDao(@CmsSessionFactory SessionFactory sessionFactory) {
    super(sessionFactory);
  }

  public List<ChangedEntityIdentifier<TimestampSavePoint<LocalDateTime>>> getInitialLoadStream(
      PageRequest pageRequest) {
    return getCwsChangedIdentifiers(LocalDateTime.of(1970, 1, 1, 1, 1),
        CwsChangedIdentifier.CWSCMS_INITIAL_LOAD_QUERY_NAME, pageRequest);
  }

  public List<ChangedEntityIdentifier<TimestampSavePoint<LocalDateTime>>> getIncrementalLoadStream(
      final LocalDateTime dateAfter,
      PageRequest pageRequest) {
    return getCwsChangedIdentifiers(dateAfter,
        CwsChangedIdentifier.CWSCMS_INCREMENTAL_LOAD_QUERY_NAME,
        pageRequest);
  }

  public List<ChangedEntityIdentifier<TimestampSavePoint<LocalDateTime>>> getResumeInitialLoadStream(
      LocalDateTime timeStampAfter,
      PageRequest pageRequest) {
    return getCwsChangedIdentifiers(timeStampAfter,
        CwsChangedIdentifier.CWSCMS_INITIAL_LOAD_QUERY_NAME,
        pageRequest);
  }

  @SuppressWarnings("unchecked")
  private List<ChangedEntityIdentifier<TimestampSavePoint<LocalDateTime>>> getCwsChangedIdentifiers(
      LocalDateTime timeStampAfter,
      String queryName, PageRequest pageRequest) {
    return currentSession().createNamedQuery(queryName)
        .setParameter("dateAfter", timeStampAfter)
        .setMaxResults(pageRequest.getLimit())
        .setFirstResult(pageRequest.getOffset())
        .setReadOnly(true).list();
  }

}
