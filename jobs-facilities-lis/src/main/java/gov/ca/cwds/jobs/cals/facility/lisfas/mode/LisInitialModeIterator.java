package gov.ca.cwds.jobs.cals.facility.lisfas.mode;

import com.google.inject.Inject;
import gov.ca.cwds.jobs.cals.facility.lisfas.identifier.LisChangedEntitiesIdentifiersService;
import gov.ca.cwds.jobs.cals.facility.lisfas.savepoint.LicenseNumberSavePoint;
import gov.ca.cwds.jobs.common.batch.JobBatch;
import gov.ca.cwds.jobs.common.batch.JobBatchSize;
import gov.ca.cwds.jobs.common.identifier.ChangedEntityIdentifier;
import gov.ca.cwds.jobs.common.iterator.JobBatchIterator;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by Alexander Serbin on 10/16/2018
 */
public class LisInitialModeIterator implements JobBatchIterator<LicenseNumberSavePoint> {

  private static final Logger LOGGER = LoggerFactory
      .getLogger(LisInitialModeIterator.class);

  protected int lastId;

  @Inject
  @JobBatchSize
  private int batchSize;

  @Inject
  private LisChangedEntitiesIdentifiersService changedEntitiesIdentifiersService;

  private List<ChangedEntityIdentifier<LicenseNumberSavePoint>> getNextPage() {
    return changedEntitiesIdentifiersService
        .getIdentifiersForInitialLoad(lastId);
  }

  @Override
  public JobBatch<LicenseNumberSavePoint> getNextPortion() {
    List<ChangedEntityIdentifier<LicenseNumberSavePoint>> identifiers = getNextPage();
    if (identifiers.isEmpty()) {
      return new JobBatch<>(Collections.emptyList());
    }
    lastId = getLastId(identifiers);
    LOGGER.info("Next page prepared. List size: {}. Last Id: {}", identifiers.size(), lastId);
    if (identifiers.size() > batchSize) {
      identifiers = identifiers.subList(0, batchSize);
      lastId = getLastId(identifiers);
      LOGGER.info("Next page cut to the batch size. Adjusted list size: {}. Last Id: {}",
          identifiers.size(), lastId);
    }
    return new JobBatch<>(identifiers);
  }

  protected static int getLastId(
      List<ChangedEntityIdentifier<LicenseNumberSavePoint>> identifiers) {
    identifiers.sort(Comparator.comparing(ChangedEntityIdentifier::getIntId));
    return identifiers.get(identifiers.size() - 1).getIntId();
  }

}
