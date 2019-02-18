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

/**
 * Created by Alexander Serbin on 10/15/2018
 */
public class NsAuditEventDao extends CustomDao {

  private static final String DATE_AFTER = "dateAfter";
  private static final String DATE_BEFORE = "dateBefore";

  private static final String ORDER_BY_CLAUSE = " order by entity.eventTimestamp, entity.id";

  private static final String GET_NEXT_SAVEPOINT_QUERY =
      "select entity.eventTimestamp from NsAuditEvent entity "
          + " where entity.eventTimestamp > :" + DATE_AFTER + ORDER_BY_CLAUSE;

  private static final String GET_IDENTIFIERS_BASE =
      "select new gov.ca.cwds.jobs.audit.identifier.AuditEventIdentifier(entity.id, entity.eventTimestamp) "
          + " from NsAuditEvent entity"
          + " where entity.eventTimestamp > :" + DATE_AFTER;

  private static final String GET_IDENTIFIERS_AFTER_TIMESTAMP =
      GET_IDENTIFIERS_BASE + ORDER_BY_CLAUSE;

  private static final String GET_IDENTIFIERS =
      GET_IDENTIFIERS_BASE + " and entity.eventTimestamp < :" + DATE_BEFORE + ORDER_BY_CLAUSE;

  @Inject
  public NsAuditEventDao(@NsSessionFactory SessionFactory sessionFactory) {
    super(sessionFactory);
  }

  public Optional<LocalDateTime> getNextSavePoint(LocalDateTime timestamp,
      int batchSize) {
    return currentSession().createQuery(GET_NEXT_SAVEPOINT_QUERY, LocalDateTime.class)
        .setParameter(DATE_AFTER, timestamp)
        .setMaxResults(1)
        .setFirstResult(batchSize - 1)
        .setReadOnly(true).uniqueResultOptional();
  }

  public Optional<LocalDateTime> getFirstChangedTimestampAfterSavepoint(
      LocalDateTime timestamp) {
    return currentSession().createQuery(GET_NEXT_SAVEPOINT_QUERY, LocalDateTime.class)
        .setParameter(DATE_AFTER, timestamp)
        .setMaxResults(1)
        .setFirstResult(0)
        .setReadOnly(true).uniqueResultOptional();
  }

  public List<ChangedEntityIdentifier<TimestampSavePoint<LocalDateTime>>> getIdentifiers(
      LocalDateTime afterTimestamp, LocalDateTime beforeTimestamp) {
    return currentSession().createQuery(GET_IDENTIFIERS)
        .setParameter(DATE_AFTER, afterTimestamp)
        .setParameter(DATE_BEFORE, beforeTimestamp)
        .setReadOnly(true).list();
  }

  public List<ChangedEntityIdentifier<TimestampSavePoint<LocalDateTime>>> getIdentifiers(
      LocalDateTime afterTimestamp) {
    return currentSession().createQuery(GET_IDENTIFIERS_AFTER_TIMESTAMP)
        .setParameter(DATE_AFTER, afterTimestamp)
        .setReadOnly(true).list();
  }

  public NsAuditEvent find(String eventId) {
    return currentSession().find(NsAuditEvent.class, eventId);
  }

}
