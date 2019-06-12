package gov.ca.cwds.jobs.cals.facility.cws.dao;

import com.google.inject.Inject;
import gov.ca.cwds.data.BaseDaoImpl;
import gov.ca.cwds.inject.CmsSessionFactory;
import gov.ca.cwds.jobs.cals.facility.cws.QueryConstants;
import gov.ca.cwds.jobs.cals.facility.cws.identifier.CwsChangedIdentifier;
import gov.ca.cwds.jobs.cals.facility.cws.inject.CwsGetIdentifiersAfterTimestampQuery;
import gov.ca.cwds.jobs.cals.facility.cws.inject.CwsGetIdentifiersBetweenTimestampsQuery;
import gov.ca.cwds.jobs.cals.facility.cws.inject.CwsGetNextSavePointQuery;
import gov.ca.cwds.jobs.common.batch.JobBatchSize;
import gov.ca.cwds.jobs.common.identifier.ChangedEntityIdentifier;
import gov.ca.cwds.jobs.common.savepoint.TimestampSavePoint;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.hibernate.SessionFactory;

/**
 * @author CWDS TPT-2
 */
public class CwsChangedIdentifierDao extends BaseDaoImpl<CwsChangedIdentifier> {

  @Inject
  @JobBatchSize
  private int batchSize;

  @Inject
  @CwsGetNextSavePointQuery
  private String getNextSavePointQuery;

  @Inject
  @CwsGetIdentifiersBetweenTimestampsQuery
  private String cwsGetIdentifiersBetweenTimestampsQuery;

  @Inject
  @CwsGetIdentifiersAfterTimestampQuery
  private String cwsGetIdentifierAfterTimestampQuery;

  @Inject
  public CwsChangedIdentifierDao(@CmsSessionFactory SessionFactory sessionFactory) {
    super(sessionFactory);
  }

  public Optional<LocalDateTime> getNextSavePoint(LocalDateTime timestamp) {
    return currentSession().createQuery(getNextSavePointQuery, LocalDateTime.class)
        .setParameter(QueryConstants.DATE_AFTER, timestamp)
        .setMaxResults(1)
        .setFirstResult(batchSize - 1)
        .setReadOnly(true).uniqueResultOptional();
  }

  public Optional<LocalDateTime> getFirstChangedTimestampAfterSavepoint(
      LocalDateTime timestamp) {
    return currentSession().createQuery(getNextSavePointQuery, LocalDateTime.class)
        .setParameter(QueryConstants.DATE_AFTER, timestamp)
        .setMaxResults(1)
        .setFirstResult(0)
        .setReadOnly(true).uniqueResultOptional();
  }

  public List<ChangedEntityIdentifier<TimestampSavePoint<LocalDateTime>>> getIdentifiers(
      LocalDateTime afterTimestamp, LocalDateTime beforeTimestamp) {
    return currentSession().createQuery(cwsGetIdentifiersBetweenTimestampsQuery)
        .setParameter(QueryConstants.DATE_AFTER, afterTimestamp)
        .setParameter(QueryConstants.DATE_BEFORE, beforeTimestamp)
        .setReadOnly(true).list();
  }

  public List<ChangedEntityIdentifier<TimestampSavePoint<LocalDateTime>>> getIdentifiers(
      LocalDateTime afterTimestamp) {
    return currentSession().createQuery(cwsGetIdentifierAfterTimestampQuery)
        .setParameter(QueryConstants.DATE_AFTER, afterTimestamp)
        .setReadOnly(true).list();
  }

}
