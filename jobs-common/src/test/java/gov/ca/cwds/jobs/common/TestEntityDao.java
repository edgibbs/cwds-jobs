package gov.ca.cwds.jobs.common;

import com.google.inject.Inject;
import gov.ca.cwds.data.BaseDaoImpl;
import gov.ca.cwds.jobs.common.entity.TestEntity;
import gov.ca.cwds.jobs.common.identifier.ChangedEntityIdentifier;
import gov.ca.cwds.jobs.common.inject.TestSessionFactory;
import gov.ca.cwds.jobs.common.savepoint.TimestampSavePoint;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.hibernate.SessionFactory;

/**
 * Created by Alexander Serbin on 10/15/2018
 */
public class TestEntityDao extends BaseDaoImpl<TestEntity> {

  private static final String ORDER_BY_CLAUSE = " order by entity.timestamp, entity.id";

  private static final String GET_NEXT_SAVEPOINT_QUERY =
  "select entity.timestamp from TestEntity entity "
      + " where entity.timestamp > :dateAfter" + ORDER_BY_CLAUSE;

  private static final String GET_IDENTIFIERS_BASE =
      "select new gov.ca.cwds.jobs.common.identifier.TestJobIdentifier(entity.id, entity.timestamp) "
          + " from TestEntity entity"
          + " where entity.timestamp > :dateAfter ";

  private static final String GET_IDENTIFIERS_AFTER_TIMESTAMP = GET_IDENTIFIERS_BASE + ORDER_BY_CLAUSE;

  private static final String GET_IDENTIFIERS =
      GET_IDENTIFIERS_BASE + " and entity.timestamp < :dateBefore"
      + ORDER_BY_CLAUSE;

  @Inject
  public TestEntityDao(@TestSessionFactory SessionFactory sessionFactory) {
    super(sessionFactory);
  }

  public Optional<LocalDateTime> getNextSavePoint(LocalDateTime timestamp,
      int batchSize) {
    return currentSession().createQuery(GET_NEXT_SAVEPOINT_QUERY, LocalDateTime.class)
        .setParameter("dateAfter", timestamp)
        .setMaxResults(1)
        .setFirstResult(batchSize - 1)
        .setReadOnly(true).uniqueResultOptional();
  }

  public Optional<LocalDateTime> getFirstChangedTimestampAfterSavepoint(
      LocalDateTime timestamp) {
    return currentSession().createQuery(GET_NEXT_SAVEPOINT_QUERY, LocalDateTime.class)
        .setParameter("dateAfter", timestamp)
        .setMaxResults(1)
        .setFirstResult(0)
        .setReadOnly(true).uniqueResultOptional();
  }

  public List<ChangedEntityIdentifier<TimestampSavePoint<LocalDateTime>>> getIdentifiers(
      LocalDateTime afterTimestamp, LocalDateTime beforeTimestamp) {
      return currentSession().createQuery(GET_IDENTIFIERS)
          .setParameter("dateAfter", afterTimestamp)
          .setParameter("dateBefore", beforeTimestamp)
          .setReadOnly(true).list();
  }

  public List<ChangedEntityIdentifier<TimestampSavePoint<LocalDateTime>>> getIdentifiers(
      LocalDateTime afterTimestamp) {
    return currentSession().createQuery(GET_IDENTIFIERS_AFTER_TIMESTAMP)
        .setParameter("dateAfter", afterTimestamp)
        .setReadOnly(true).list();
  }

}
