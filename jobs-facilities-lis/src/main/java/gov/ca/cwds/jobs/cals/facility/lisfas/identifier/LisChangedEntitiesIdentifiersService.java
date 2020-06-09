package gov.ca.cwds.jobs.cals.facility.lisfas.identifier;

import static gov.ca.cwds.cals.Constants.UnitOfWork.LIS;

import java.math.BigInteger;
import java.util.List;

import com.google.inject.Inject;

import gov.ca.cwds.jobs.cals.facility.lisfas.dao.FirstIncrementalSavePointDao;
import gov.ca.cwds.jobs.cals.facility.lisfas.dao.LicenseNumberIdentifierDao;
import gov.ca.cwds.jobs.cals.facility.lisfas.dao.LisTimestampIdentifierDao;
import gov.ca.cwds.jobs.cals.facility.lisfas.savepoint.LicenseNumberSavePoint;
import gov.ca.cwds.jobs.common.identifier.ChangedEntityIdentifier;
import gov.ca.cwds.jobs.common.savepoint.TimestampSavePoint;
import io.dropwizard.hibernate.UnitOfWork;

/**
 * Created by Alexander Serbin on 3/6/2018.
 */
public class LisChangedEntitiesIdentifiersService {

  @Inject
  private LicenseNumberIdentifierDao licenseNumberIdentifierDao;

  @Inject
  private LisTimestampIdentifierDao lisTimestampIdentifierDao;


  @Inject
  private FirstIncrementalSavePointDao firstIncrementalSavePointDao;

  @UnitOfWork(LIS)
  public List<ChangedEntityIdentifier<LicenseNumberSavePoint>> getIdentifiersForInitialLoad(
      int lastId) {
    return licenseNumberIdentifierDao.getInitialLoadStream(lastId);
  }

  @UnitOfWork(LIS)
  public List<ChangedEntityIdentifier<TimestampSavePoint<BigInteger>>> getIdentifiersForIncrementalLoad(
      BigInteger savePoint) {
    return lisTimestampIdentifierDao.getIncrementalLoadStream(savePoint);
  }

  @UnitOfWork(LIS)
  public BigInteger findMaxTimestamp() {
    return firstIncrementalSavePointDao.findMaxTimestamp();
  }

}
