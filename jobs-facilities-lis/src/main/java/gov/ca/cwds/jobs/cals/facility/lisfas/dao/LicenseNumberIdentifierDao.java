package gov.ca.cwds.jobs.cals.facility.lisfas.dao;

import java.util.List;

import org.hibernate.SessionFactory;

import com.google.inject.Inject;

import gov.ca.cwds.cals.inject.LisSessionFactory;
import gov.ca.cwds.data.BaseDaoImpl;
import gov.ca.cwds.jobs.cals.facility.lisfas.identifier.LicenseNumberIdentifier;
import gov.ca.cwds.jobs.cals.facility.lisfas.savepoint.LicenseNumberSavePoint;
import gov.ca.cwds.jobs.common.batch.JobBatchSize;
import gov.ca.cwds.jobs.common.identifier.ChangedEntityIdentifier;

/**
 * @author CWDS CALS API Team
 */
public class LicenseNumberIdentifierDao extends BaseDaoImpl<LicenseNumberIdentifier> {

  @Inject
  @JobBatchSize
  private int batchSize;

  @Inject
  public LicenseNumberIdentifierDao(@LisSessionFactory SessionFactory sessionFactory) {
    super(sessionFactory);
  }

  @SuppressWarnings("unchecked")
  public List<ChangedEntityIdentifier<LicenseNumberSavePoint>> getInitialLoadStream(
      int licenseNumber) {
    return currentSession().createNamedQuery(LicenseNumberIdentifier.LIS_INITIAL_LOAD_QUERY_NAME)
        .setMaxResults(batchSize).setReadOnly(true).setParameter("facNbr", licenseNumber).list();
  }

}
