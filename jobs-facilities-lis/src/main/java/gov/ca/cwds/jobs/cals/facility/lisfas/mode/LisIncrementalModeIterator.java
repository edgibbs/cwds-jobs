package gov.ca.cwds.jobs.cals.facility.lisfas.mode;

import java.math.BigInteger;

import com.google.inject.Inject;

import gov.ca.cwds.jobs.cals.facility.lisfas.identifier.LisChangedEntitiesIdentifiersService;
import gov.ca.cwds.jobs.cals.facility.lisfas.savepoint.LisTimestampSavePoint;
import gov.ca.cwds.jobs.cals.facility.lisfas.savepoint.LisTimestampSavePointService;
import gov.ca.cwds.jobs.common.batch.JobBatch;
import gov.ca.cwds.jobs.common.iterator.JobBatchIterator;
import gov.ca.cwds.jobs.common.savepoint.TimestampSavePoint;

/**
 * Created by Alexander Serbin on 10/16/2018
 */
public class LisIncrementalModeIterator
    implements JobBatchIterator<TimestampSavePoint<BigInteger>> {

  @Inject
  private LisTimestampSavePointService lisTimestampSavePointService;

  @Inject
  private LisChangedEntitiesIdentifiersService changedEntitiesIdentifiersService;

  @Override
  public JobBatch<TimestampSavePoint<BigInteger>> getNextPortion() {
    LisTimestampSavePoint savePoint =
        (LisTimestampSavePoint) lisTimestampSavePointService.loadSavePoint();
    return new JobBatch<>(changedEntitiesIdentifiersService
        .getIdentifiersForIncrementalLoad(savePoint.getTimestamp()));
  }

}
