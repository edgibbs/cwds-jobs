package gov.ca.cwds.jobs.cals.facility.cws.dao;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
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
import gov.ca.cwds.jobs.common.RecordChangeOperation;
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
    LOG.debug("getNextSavePoint: timestamp: {}", timestamp);
    LOG.debug("getNextSavePoint: \n{}", getNextSavePointQuery);

    try {
      final Object obj = currentSession().createNativeQuery(getNextSavePointQuery)
          .setParameter(QueryConstants.DATE_AFTER, Timestamp.valueOf(timestamp)).uniqueResult();
      ret = Optional.<LocalDateTime>of(((Timestamp) obj).toLocalDateTime());
    } catch (Exception e) {
      LOG.error("getNextSavePoint: FAILED TO FIND NEXT SAVE POINT!", e);
      throw e;
    }

    LOG.debug("getNextSavePoint: ret: {}", ret);
    return ret;
  }

  @SuppressWarnings("unchecked")
  public Optional<LocalDateTime> getFirstChangedTimestampAfterSavepoint(LocalDateTime timestamp) {
    Optional<LocalDateTime> ret = Optional.<LocalDateTime>empty();
    final String sql =
        getFirstTimestampAfterSavePointQuery.replace("BATCH_SIZE", Integer.toString(batchSize));
    LOG.debug("getFirstChangedTimestampAfterSavepoint: SQL: \n{}", sql);
    LOG.debug("timestamp: {}", timestamp);

    try {
      final Object obj = currentSession().createNativeQuery(sql)
          .setParameter(QueryConstants.DATE_AFTER, Timestamp.valueOf(timestamp)).uniqueResult();
      ret = obj != null ? Optional.<LocalDateTime>of(((Timestamp) obj).toLocalDateTime())
          : Optional.<LocalDateTime>empty();
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
    LOG.info("cwsGetIdentifiersBetweenTimestampsQuery: \n{}",
        cwsGetIdentifiersBetweenTimestampsQuery);
    List<ChangedEntityIdentifier<TimestampSavePoint<LocalDateTime>>> ret = new ArrayList<>(0);
    final String sql =
        cwsGetIdentifiersBetweenTimestampsQuery.replace("BATCH_SIZE", Integer.toString(batchSize));
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

    LOG.debug("getIdentifiers(ts): ret: {}", ret);
    return ret;
  }

  @SuppressWarnings("unchecked")
  public List<ChangedEntityIdentifier<TimestampSavePoint<LocalDateTime>>> getIdentifiers(
      LocalDateTime afterTimestamp) {
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

    LOG.debug("getIdentifiers(ts): ret: {}", ret);
    return ret;
  }

}
