package gov.ca.cwds.jobs.cals.facility.lisfas.dao;

import java.math.BigInteger;

import org.hibernate.SessionFactory;

import com.google.inject.Inject;

import gov.ca.cwds.cals.inject.LisSessionFactory;
import gov.ca.cwds.cals.service.dao.CustomDao;
import gov.ca.cwds.jobs.cals.facility.lisfas.identifier.LicenseNumberIdentifier;

/**
 * Created by Alexander Serbin on 6/29/2018.
 */
public class FirstIncrementalSavePointDao extends CustomDao {

  @Inject
  public FirstIncrementalSavePointDao(@LisSessionFactory SessionFactory sessionFactory) {
    super(sessionFactory);
  }

  public BigInteger findMaxTimestamp() {
    return currentSession().createNamedQuery(
        LicenseNumberIdentifier.LIS_GET_MAX_TIMESTAMP_QUERY_NAME, BigInteger.class).uniqueResult();
  }

}
