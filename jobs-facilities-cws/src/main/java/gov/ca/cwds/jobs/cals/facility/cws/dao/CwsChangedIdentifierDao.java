package gov.ca.cwds.jobs.cals.facility.cws.dao;

import static gov.ca.cwds.jobs.cals.facility.cws.identifier.CwsChangedIdentifier.SHARED_PART;

import com.google.inject.Inject;
import gov.ca.cwds.data.BaseDaoImpl;
import gov.ca.cwds.inject.CmsSessionFactory;
import gov.ca.cwds.jobs.cals.facility.cws.identifier.CwsChangedIdentifier;
import gov.ca.cwds.jobs.cals.facility.cws.inject.CwsIdentifierCreator;
import gov.ca.cwds.jobs.cals.facility.cws.inject.TimestampField;
import gov.ca.cwds.jobs.common.batch.JobBatchSize;
import gov.ca.cwds.jobs.common.identifier.ChangedEntityIdentifier;
import gov.ca.cwds.jobs.common.savepoint.TimestampSavePoint;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.apache.commons.text.StringSubstitutor;
import org.hibernate.SessionFactory;

/**
 * @author CWDS TPT-2
 */
public class CwsChangedIdentifierDao extends BaseDaoImpl<CwsChangedIdentifier> {

  private static final String ORDER_BY_CLAUSE = " order by home.${timestampField}, home.identifier";
  public static final String DATE_AFTER = "dateAfter";

  @Inject
  @JobBatchSize
  private int batchSize;

  @Inject
  @TimestampField
  private String timestampField;

  @Inject
  @CwsIdentifierCreator
  private String cwsIdentifierCreator;

  private static final String GET_NEXT_SAVEPOINT_QUERY =
      "select home.${timestampField} " + SHARED_PART
          + " and home.${timestampField} > :dateAfter"
          + ORDER_BY_CLAUSE;

  private static final String GET_IDENTIFIERS_BASE =
      "select ${cwsIdentifierCreator} "
          + SHARED_PART
          + " and home.${timestampField} > :dateAfter ";

  private static final String GET_IDENTIFIERS_AFTER_TIMESTAMP = GET_IDENTIFIERS_BASE + ORDER_BY_CLAUSE;

  private static final String GET_IDENTIFIERS =
      GET_IDENTIFIERS_BASE + " and home.${timestampField} < :dateBefore"
          + ORDER_BY_CLAUSE;

  @Inject
  public CwsChangedIdentifierDao(@CmsSessionFactory SessionFactory sessionFactory) {
    super(sessionFactory);
  }

  public Optional<LocalDateTime> getNextSavePoint(LocalDateTime timestamp) {
    return currentSession().createQuery(substituteVariables(GET_NEXT_SAVEPOINT_QUERY), LocalDateTime.class)
        .setParameter(DATE_AFTER, timestamp)
        .setMaxResults(1)
        .setFirstResult(batchSize - 1)
        .setReadOnly(true).uniqueResultOptional();
  }

  public Optional<LocalDateTime> getFirstChangedTimestampAfterSavepoint(
      LocalDateTime timestamp) {
    return currentSession().createQuery(substituteVariables(GET_NEXT_SAVEPOINT_QUERY), LocalDateTime.class)
        .setParameter(DATE_AFTER, timestamp)
        .setMaxResults(1)
        .setFirstResult(0)
        .setReadOnly(true).uniqueResultOptional();
  }

  public List<ChangedEntityIdentifier<TimestampSavePoint<LocalDateTime>>> getIdentifiers(
      LocalDateTime afterTimestamp, LocalDateTime beforeTimestamp) {
    return currentSession().createQuery(substituteVariables(GET_IDENTIFIERS))
        .setParameter(DATE_AFTER, afterTimestamp)
        .setParameter("dateBefore", beforeTimestamp)
        .setReadOnly(true).list();
  }

  public List<ChangedEntityIdentifier<TimestampSavePoint<LocalDateTime>>> getIdentifiers(
      LocalDateTime afterTimestamp) {
    return currentSession().createQuery(substituteVariables(GET_IDENTIFIERS_AFTER_TIMESTAMP))
        .setParameter(DATE_AFTER, afterTimestamp)
        .setReadOnly(true).list();
  }

  private String substituteVariables(String query) {
    Map<String, String> valuesMap = new HashMap<>();
    valuesMap.put("timestampField", timestampField);
    valuesMap.put("cwsIdentifierCreator", cwsIdentifierCreator);
    StringSubstitutor substitutor = new StringSubstitutor(valuesMap);
    return substitutor.replace(query);
  }

}
