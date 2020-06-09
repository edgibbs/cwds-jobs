package gov.ca.cwds.jobs.cals.facility.lisfas.dao;

import java.math.BigInteger;
import java.util.List;

import org.hibernate.SessionFactory;

import com.google.inject.Inject;

import gov.ca.cwds.cals.inject.LisSessionFactory;
import gov.ca.cwds.data.BaseDaoImpl;
import gov.ca.cwds.jobs.cals.facility.lisfas.identifier.LisTimestampIdentifier;
import gov.ca.cwds.jobs.common.identifier.ChangedEntityIdentifier;
import gov.ca.cwds.jobs.common.savepoint.TimestampSavePoint;

/**
 * @author CWDS CALS API Team
 */
public class LisTimestampIdentifierDao extends BaseDaoImpl<LisTimestampIdentifier> {

  @Inject
  public LisTimestampIdentifierDao(@LisSessionFactory SessionFactory sessionFactory) {
    super(sessionFactory);
  }

  @SuppressWarnings("unchecked")
  public List<ChangedEntityIdentifier<TimestampSavePoint<BigInteger>>> getIncrementalLoadStream(
      final BigInteger dateAfter) {
    return currentSession().createNamedQuery(LisTimestampIdentifier.LIS_INCREMENTAL_LOAD_QUERY_NAME)
        .setReadOnly(true).setParameter("dateAfter", dateAfter).list();
  }

}
