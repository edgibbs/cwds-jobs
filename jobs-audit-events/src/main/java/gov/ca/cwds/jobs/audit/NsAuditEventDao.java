package gov.ca.cwds.jobs.audit;

import com.google.inject.Inject;
import gov.ca.cwds.idm.persistence.ns.entity.NsAuditEvent;
import gov.ca.cwds.jobs.audit.inject.NsSessionFactory;
import gov.ca.cwds.jobs.common.dao.CustomDao;
import gov.ca.cwds.jobs.common.identifier.ChangedEntityIdentifier;
import gov.ca.cwds.jobs.common.savepoint.TimestampSavePoint;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.hibernate.SessionFactory;

public class NsAuditEventDao extends CustomDao {

  private static final String DATE_AFTER = "dateAfter";
  private static final String DATE_BEFORE = "dateBefore";

  private static final String ORDER_BY_CLAUSE = " order by entity.eventTimestamp, entity.id";

  private static final String GET_NEXT_SAVEPOINT_QUERY_BASE =
      "select entity.eventTimestamp from NsAuditEvent entity "
          + " where entity.eventTimestamp > :" + DATE_AFTER;

  private static final String NOT_PROCESSED_ONLY = " and entity.processed = false";

  private static final String GET_IDENTIFIERS_BASE =
      "select new gov.ca.cwds.jobs.audit.identifier.AuditEventIdentifier(entity.id, entity.eventTimestamp) "
          + " from NsAuditEvent entity"
          + " where entity.eventTimestamp > :" + DATE_AFTER;

  private static final String BEFORE_CLAUSE =
      " and entity.eventTimestamp < :" + DATE_BEFORE;

  @Inject
  public NsAuditEventDao(@NsSessionFactory SessionFactory sessionFactory) {
    super(sessionFactory);
  }

  public Optional<LocalDateTime> getNextSavePoint(LocalDateTime timestamp,
      int batchSize) {
    return currentSession()
        .createQuery(GET_NEXT_SAVEPOINT_QUERY_BASE + ORDER_BY_CLAUSE, LocalDateTime.class)
        .setParameter(DATE_AFTER, timestamp)
        .setMaxResults(1)
        .setFirstResult(batchSize - 1)
        .setReadOnly(true).uniqueResultOptional();
  }

  public Optional<LocalDateTime> getFirstChangedTimestampAfterSavepoint(
      LocalDateTime timestamp) {
    return currentSession()
        .createQuery(GET_NEXT_SAVEPOINT_QUERY_BASE + ORDER_BY_CLAUSE, LocalDateTime.class)
        .setParameter(DATE_AFTER, timestamp)
        .setMaxResults(1)
        .setFirstResult(0)
        .setReadOnly(true).uniqueResultOptional();
  }

  public List<ChangedEntityIdentifier<TimestampSavePoint<LocalDateTime>>> getIdentifiers(
      LocalDateTime afterTimestamp, LocalDateTime beforeTimestamp) {
    return currentSession().createQuery(GET_IDENTIFIERS_BASE + BEFORE_CLAUSE + ORDER_BY_CLAUSE)
        .setParameter(DATE_AFTER, afterTimestamp)
        .setParameter(DATE_BEFORE, beforeTimestamp)
        .setReadOnly(true).list();
  }

  public List<ChangedEntityIdentifier<TimestampSavePoint<LocalDateTime>>> getIdentifiers(
      LocalDateTime afterTimestamp) {
    return currentSession().createQuery(GET_IDENTIFIERS_BASE + ORDER_BY_CLAUSE)
        .setParameter(DATE_AFTER, afterTimestamp)
        .setReadOnly(true).list();
  }

  public NsAuditEvent find(String eventId) {
    return currentSession().find(NsAuditEvent.class, eventId);
  }

  public Optional<LocalDateTime> getFirstUnprocessedChangedTimestampAfterSavepoint(
      LocalDateTime timestamp) {
    return currentSession()
        .createQuery(GET_NEXT_SAVEPOINT_QUERY_BASE + NOT_PROCESSED_ONLY + ORDER_BY_CLAUSE,
            LocalDateTime.class)
        .setParameter(DATE_AFTER, timestamp)
        .setMaxResults(1)
        .setFirstResult(0)
        .setReadOnly(true).uniqueResultOptional();
  }

  public List<ChangedEntityIdentifier<TimestampSavePoint<LocalDateTime>>> getUnprocessedIdentifiers(
      LocalDateTime afterTimestamp, LocalDateTime beforeTimestamp) {
    return currentSession()
        .createQuery(GET_IDENTIFIERS_BASE + BEFORE_CLAUSE + NOT_PROCESSED_ONLY + ORDER_BY_CLAUSE)
        .setParameter(DATE_AFTER, afterTimestamp)
        .setParameter(DATE_BEFORE, beforeTimestamp)
        .setReadOnly(true).list();
  }

  public List<ChangedEntityIdentifier<TimestampSavePoint<LocalDateTime>>> getUnprocessedIdentifiersAfter(
      LocalDateTime afterTimestamp) {
    return currentSession().createQuery(GET_IDENTIFIERS_BASE + NOT_PROCESSED_ONLY + ORDER_BY_CLAUSE)
        .setParameter(DATE_AFTER, afterTimestamp)
        .setReadOnly(true).list();
  }
}
