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

  public LocalDateTime getFirstChangedTimestampForInitialLoad(LocalDateTime timeStampAfter) {
    return getFirstChangedTimestamp(
        timeStampAfter,
        CwsChangedIdentifier.CWSCMS_INITIAL_LOAD_GET_FIRST_CHANGED_TIMESTAMP_QUERY_NAME);
  }

  public LocalDateTime getFirstChangedTimestampForIncrementalLoad(LocalDateTime timeStampAfter) {
    return getFirstChangedTimestamp(
        timeStampAfter,
        CwsChangedIdentifier.CWSCMS_INCREMENTAL_LOAD_GET_FIRST_CHANGED_TIMESTAMP_QUERY_NAME);
  }

  public List<ChangedEntityIdentifier<TimestampSavePoint<LocalDateTime>>>
      getIdentifiersBeforeTimestampForInitialLoad(LocalDateTime timeStampBefore, int offset) {
    return getIdentifiersBeforeTimestamp(
        timeStampBefore,
        offset,
        CwsChangedIdentifier
            .CWSCMS_INITIAL_LOAD_GET_IDENTIFIERS_BEFORE_CHANGED_TIMESTAMP_QUERY_NAME);
  }

  public List<ChangedEntityIdentifier<TimestampSavePoint<LocalDateTime>>>
      getIdentifiersBeforeTimestampForIncrementalLoad(LocalDateTime timeStampBefore, int offset) {
    return getIdentifiersBeforeTimestamp(
        timeStampBefore,
        offset,
        CwsChangedIdentifier
            .CWSCMS_INCREMENTAL_LOAD_GET_IDENTIFIERS_BEFORE_CHANGED_TIMESTAMP_QUERY_NAME);
  }

  @SuppressWarnings("unchecked")
  private List<ChangedEntityIdentifier<TimestampSavePoint<LocalDateTime>>> getCwsChangedIdentifiers(
      LocalDateTime timeStampAfter,
      String queryName, PageRequest pageRequest) {
    return currentSession().createNamedQuery(queryName)
        .setParameter("dateAfter", timeStampAfter)
        .setMaxResults(pageRequest.getLimit())
        .setFirstResult(pageRequest.getOffset())
        .setReadOnly(true)
        .list();
  }

  private LocalDateTime getFirstChangedTimestamp(LocalDateTime timeStampAfter, final String query) {
    return currentSession()
        .createNamedQuery(query, LocalDateTime.class)
        .setParameter("dateAfter", timeStampAfter)
        .setReadOnly(true)
        .uniqueResult();
  }

  @SuppressWarnings("unchecked")
  private List<ChangedEntityIdentifier<TimestampSavePoint<LocalDateTime>>>
      getIdentifiersBeforeTimestamp(LocalDateTime timeStampBefore, int offset, String query) {
    return currentSession()
        .createNamedQuery(query)
        .setParameter("dateBefore", timeStampBefore)
        .setFirstResult(offset)
        .setReadOnly(true)
        .list();
  }
}
