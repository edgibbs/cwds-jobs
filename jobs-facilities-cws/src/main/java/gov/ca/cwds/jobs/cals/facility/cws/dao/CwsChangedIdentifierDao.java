package gov.ca.cwds.jobs.cals.facility.cws.dao;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;

import gov.ca.cwds.data.BaseDaoImpl;
import gov.ca.cwds.inject.CmsSessionFactory;
import gov.ca.cwds.jobs.cals.facility.cws.QueryConstants;
import gov.ca.cwds.jobs.cals.facility.cws.identifier.CwsChangedIdentifier;
import gov.ca.cwds.jobs.cals.facility.cws.inject.CwsGetFirstTimestampAfterSavePointQuery;
import gov.ca.cwds.jobs.cals.facility.cws.inject.CwsGetIdentifiersAfterTimestampQuery;
import gov.ca.cwds.jobs.cals.facility.cws.inject.CwsGetIdentifiersBetweenTimestampsQuery;
import gov.ca.cwds.jobs.cals.facility.cws.inject.CwsGetNextSavePointQuery;
import gov.ca.cwds.jobs.common.batch.JobBatchSize;
import gov.ca.cwds.jobs.common.identifier.ChangedEntityIdentifier;
import gov.ca.cwds.jobs.common.savepoint.TimestampSavePoint;

/**
 * @author CWDS TPT-2
 */
public class CwsChangedIdentifierDao extends BaseDaoImpl<CwsChangedIdentifier> {

  private static final Logger LOG = LoggerFactory.getLogger(CwsChangedIdentifierDao.class);

  @Inject
  @JobBatchSize
  private int batchSize;

  @Inject
  @CwsGetNextSavePointQuery
  private String getNextSavePointQuery;

  @Inject
  @CwsGetFirstTimestampAfterSavePointQuery
  private String getFirstTimestampAfterSavePointQuery;

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
    Optional<LocalDateTime> ret = Optional.<LocalDateTime>empty();
    LOG.debug("getNextSavePointQuery: timestamp: {}", timestamp);
    LOG.debug("getNextSavePointQuery: \n{}", getNextSavePointQuery);

    try {
      final Object obj = currentSession().createNativeQuery(getNextSavePointQuery)
          .setParameter(QueryConstants.DATE_AFTER, Timestamp.valueOf(timestamp)).uniqueResult();
      ret = Optional.<LocalDateTime>of(((Timestamp) obj).toLocalDateTime());
    } catch (Exception e) {
      LOG.error("FAILED TO FIND NEXT SAVE POINT!", e);
      throw e;
    }

    LOG.debug("getNextSavePoint: {}", ret);
    return ret;
  }

  public Optional<LocalDateTime> getFirstChangedTimestampAfterSavepoint(LocalDateTime timestamp) {
    Optional<LocalDateTime> ret = Optional.<LocalDateTime>empty();
    LOG.debug("getFirstChangedTimestampAfterSavepoint: \n{}", getFirstTimestampAfterSavePointQuery);
    LOG.debug("batchSize: {}", batchSize);
    try {
      final Object obj = currentSession().createNativeQuery(getFirstTimestampAfterSavePointQuery)
          .setParameter(QueryConstants.DATE_AFTER, Timestamp.valueOf(timestamp)).uniqueResult();
      ret = Optional.<LocalDateTime>of(((Timestamp) obj).toLocalDateTime());
    } catch (Exception e) {
      LOG.error("FAILED TO FIND FIRST TIMESTAMP AFTER SAVE POINT!", e);
      throw e;
    }

    LOG.debug("getFirstChangedTimestampAfterSavepoint: {}", ret);
    return ret;

    // DRS: code works okay-ish, but the original approach is incomplete.
    // It only scans for changes in the placement home table proper, not supporting tables.

    // return currentSession().createNativeQuery(getNextSavePointQuery, Timestamp.class)
    // .setParameter(QueryConstants.DATE_AFTER, timestamp).setMaxResults(1)
    // .setFirstResult(batchSize - 1).setReadOnly(true).uniqueResultOptional()
    // .map(Timestamp::toLocalDateTime);
  }

  public List<ChangedEntityIdentifier<TimestampSavePoint<LocalDateTime>>> getIdentifiers(
      LocalDateTime afterTimestamp, LocalDateTime beforeTimestamp) {
    LOG.info("cwsGetIdentifiersBetweenTimestampsQuery: \n{}",
        cwsGetIdentifiersBetweenTimestampsQuery);
    return currentSession().createQuery(cwsGetIdentifiersBetweenTimestampsQuery)
        .setParameter(QueryConstants.DATE_AFTER, afterTimestamp)
        .setParameter(QueryConstants.DATE_BEFORE, beforeTimestamp).setReadOnly(true).list();
  }

  public List<ChangedEntityIdentifier<TimestampSavePoint<LocalDateTime>>> getIdentifiers(
      LocalDateTime afterTimestamp) {
    LOG.info("get identifiers ...");
    return currentSession().createQuery(cwsGetIdentifierAfterTimestampQuery)
        .setParameter(QueryConstants.DATE_AFTER, afterTimestamp).setReadOnly(true).list();
  }

}
