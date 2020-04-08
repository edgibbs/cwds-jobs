package gov.ca.cwds.jobs.cals.facility.cws.dao;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
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
import gov.ca.cwds.jobs.common.RecordChangeOperation;
import gov.ca.cwds.jobs.common.batch.JobBatchSize;
import gov.ca.cwds.jobs.common.identifier.ChangedEntityIdentifier;
import gov.ca.cwds.jobs.common.mode.JobBatchMode;
import gov.ca.cwds.jobs.common.mode.JobMode;
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
  @JobBatchMode
  private JobMode jobMode;

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

  protected Optional<LocalDateTime> getNextSavePointInitial(LocalDateTime timestamp) {
    return currentSession().createQuery(getNextSavePointQuery, LocalDateTime.class)
        .setParameter(QueryConstants.DATE_AFTER, timestamp).setMaxResults(1)
        .setFirstResult(batchSize - 1).setReadOnly(true).uniqueResultOptional();
  }

  protected Optional<LocalDateTime> getFirstChangedTimestampAfterSavepointInitial(
      LocalDateTime timestamp) {
    return currentSession().createQuery(getNextSavePointQuery, LocalDateTime.class)
        .setParameter(QueryConstants.DATE_AFTER, timestamp).setMaxResults(1).setFirstResult(0)
        .setReadOnly(true).uniqueResultOptional();
  }

  protected List<ChangedEntityIdentifier<TimestampSavePoint<LocalDateTime>>> getIdentifiersInitial(
      LocalDateTime afterTimestamp, LocalDateTime beforeTimestamp) {
    return currentSession().createQuery(cwsGetIdentifiersBetweenTimestampsQuery)
        .setParameter(QueryConstants.DATE_AFTER, afterTimestamp)
        .setParameter(QueryConstants.DATE_BEFORE, beforeTimestamp).setReadOnly(true).list();
  }

  protected List<ChangedEntityIdentifier<TimestampSavePoint<LocalDateTime>>> getIdentifiersInitial(
      LocalDateTime afterTimestamp) {
    return currentSession().createQuery(cwsGetIdentifierAfterTimestampQuery)
        .setParameter(QueryConstants.DATE_AFTER, afterTimestamp).setReadOnly(true).list();
  }

  protected boolean isInitialLoad() {
    return jobMode.ordinal() == JobMode.INITIAL_LOAD.ordinal()
        || jobMode.ordinal() == JobMode.INITIAL_RESUME.ordinal();
  }

  public Optional<LocalDateTime> getNextSavePoint(LocalDateTime timestamp) {
    if (isInitialLoad()) {
      return getNextSavePointInitial(timestamp);
    }

    Optional<LocalDateTime> ret = Optional.<LocalDateTime>empty();
    LOG.debug("getNextSavePoint: timestamp: {}", timestamp);
    final String sql = getNextSavePointQuery.replace("BATCH_SIZE", Integer.toString(batchSize));
    LOG.debug("getNextSavePoint: SQL: \n{}", sql);

    try {
      final Object obj = currentSession().createNativeQuery(sql)
          .setParameter(QueryConstants.DATE_AFTER, Timestamp.valueOf(timestamp)).uniqueResult();
      ret = obj != null ? Optional.<LocalDateTime>of(((Timestamp) obj).toLocalDateTime())
          : Optional.<LocalDateTime>of(
              LocalDateTime.now(ZoneId.of("America/Los_Angeles")).minus(1, ChronoUnit.MINUTES));
    } catch (Exception e) {
      LOG.error("getNextSavePoint: FAILED TO FIND NEXT SAVE POINT!", e);
      throw e;
    }

    LOG.debug("getNextSavePoint: ret: {}", ret);
    return ret;
  }

  @SuppressWarnings("unchecked")
  public Optional<LocalDateTime> getFirstChangedTimestampAfterSavepoint(LocalDateTime timestamp) {
    if (isInitialLoad()) {
      return getFirstChangedTimestampAfterSavepointInitial(timestamp);
    }

    Optional<LocalDateTime> ret = Optional.<LocalDateTime>empty();
    final String sql =
        getFirstTimestampAfterSavePointQuery.replace("BATCH_SIZE", Integer.toString(batchSize));
    LOG.debug("getFirstChangedTimestampAfterSavepoint: SQL: \n{}", sql);
    LOG.debug("getFirstChangedTimestampAfterSavepoint: timestamp: {}", timestamp);

    try {
      final Object obj = currentSession().createNativeQuery(sql)
          .setParameter(QueryConstants.DATE_AFTER, Timestamp.valueOf(timestamp)).uniqueResult();
      ret = obj != null ? Optional.<LocalDateTime>of(((Timestamp) obj).toLocalDateTime())
          : Optional.<LocalDateTime>of(
              LocalDateTime.now(ZoneId.of("America/Los_Angeles")).minus(1, ChronoUnit.MINUTES));
    } catch (Exception e) {
      LOG.error("FAILED TO FIND FIRST TIMESTAMP AFTER SAVE POINT!", e);
      throw e;
    }

    LOG.debug("getFirstChangedTimestampAfterSavepoint: ret: {}", ret);
    return ret;
  }

  @SuppressWarnings("unchecked")
  public List<ChangedEntityIdentifier<TimestampSavePoint<LocalDateTime>>> getIdentifiers(
      LocalDateTime afterTimestamp, LocalDateTime beforeTimestamp) {
    if (isInitialLoad()) {
      return getIdentifiersInitial(afterTimestamp, beforeTimestamp);
    }

    List<ChangedEntityIdentifier<TimestampSavePoint<LocalDateTime>>> ret = new ArrayList<>(0);
    final String sql =
        cwsGetIdentifiersBetweenTimestampsQuery.replace("BATCH_SIZE", Integer.toString(batchSize));
    LOG.debug("getIdentifiers(ts,ts): SQL: \n{}", sql);
    LOG.debug("getIdentifiers(ts,ts): \nbefore: {}, \n after: {}", beforeTimestamp, afterTimestamp);

    try {
      Timestamp paramAfter = Timestamp.valueOf(afterTimestamp.minus(1, ChronoUnit.MINUTES));
      Timestamp paramBefore = Timestamp.valueOf(beforeTimestamp.plus(1, ChronoUnit.MINUTES));

      final List<Object[]> arr = currentSession().createNativeQuery(sql)
          .setParameter(QueryConstants.DATE_AFTER, paramAfter)
          .setParameter(QueryConstants.DATE_BEFORE, paramBefore).list();
      if (arr != null && !arr.isEmpty()) {
        ret = new ArrayList<>(arr.size());

        for (Object[] row : arr) {
          final String id = (String) row[0];
          final String strOp = "" + row[1];
          final RecordChangeOperation op =
              StringUtils.isNotBlank(strOp) ? RecordChangeOperation.valueOf(String.valueOf(strOp))
                  : RecordChangeOperation.I;
          final LocalDateTime ts = ((Timestamp) row[2]).toLocalDateTime();
          ret.add(new CwsChangedIdentifier(id, op, ts));
        }
      }
    } catch (Exception e) {
      LOG.error("getIdentifiers(ts,ts): FAILED TO PULL IDENTIFIERS!", e);
      throw e;
    }

    LOG.debug("getIdentifiers(ts,ts): \nret: {}", ret);
    return ret;
  }

  @SuppressWarnings("unchecked")
  public List<ChangedEntityIdentifier<TimestampSavePoint<LocalDateTime>>> getIdentifiers(
      LocalDateTime afterTimestamp) {
    if (isInitialLoad()) {
      return getIdentifiersInitial(afterTimestamp);
    }

    List<ChangedEntityIdentifier<TimestampSavePoint<LocalDateTime>>> ret = new ArrayList<>(0);
    final String sql =
        cwsGetIdentifierAfterTimestampQuery.replace("BATCH_SIZE", Integer.toString(batchSize));
    LOG.debug("getIdentifiers(ts): SQL: \n{}", sql);
    LOG.debug("getIdentifiers(ts): timestamp: {}", afterTimestamp);

    try {
      final List<Object[]> arr = currentSession().createNativeQuery(sql)
          .setParameter(QueryConstants.DATE_AFTER, Timestamp.valueOf(afterTimestamp)).list();
      if (arr != null && !arr.isEmpty()) {
        ret = new ArrayList<>(arr.size());

        for (Object o : arr) {
          final Object[] row = (Object[]) o;
          final RecordChangeOperation op = RecordChangeOperation.valueOf(String.valueOf(row[1]));
          final LocalDateTime ts = ((Timestamp) row[2]).toLocalDateTime();
          ret.add(new CwsChangedIdentifier((String) row[0], op, ts));
        }
      }
    } catch (Exception e) {
      LOG.error("getIdentifiers(ts): FAILED TO PULL IDENTIFIERS!", e);
      throw e;
    }

    LOG.debug("getIdentifiers(ts): DONE");
    return ret;
  }

}
