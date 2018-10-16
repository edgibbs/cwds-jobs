package gov.ca.cwds.jobs.cals.facility.lisfas.mode;

import com.google.inject.Inject;
import gov.ca.cwds.jobs.cals.facility.ChangedFacilityDto;
import gov.ca.cwds.jobs.cals.facility.lisfas.identifier.LisChangedEntitiesIdentifiersService;
import gov.ca.cwds.jobs.cals.facility.lisfas.savepoint.LicenseNumberSavePoint;
import gov.ca.cwds.jobs.cals.facility.lisfas.savepoint.LicenseNumberSavePointService;
import gov.ca.cwds.jobs.cals.facility.lisfas.savepoint.LisTimestampSavePointContainerService;
import gov.ca.cwds.jobs.common.batch.JobBatch;
import gov.ca.cwds.jobs.common.batch.JobBatchSize;
import gov.ca.cwds.jobs.common.identifier.ChangedEntityIdentifier;
import gov.ca.cwds.jobs.common.mode.AbstractJobModeImplementor;
import gov.ca.cwds.jobs.common.mode.DefaultJobMode;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractLisInitialModeImplementor extends
    AbstractJobModeImplementor<ChangedFacilityDto, LicenseNumberSavePoint, DefaultJobMode> {

  private static final Logger LOGGER = LoggerFactory
      .getLogger(AbstractLisInitialModeImplementor.class);

  @Inject
  @JobBatchSize
  private int batchSize;

  protected int lastId;

  @Inject
  private LicenseNumberSavePointService savePointService;

  @Inject
  private LisChangedEntitiesIdentifiersService changedEntitiesIdentifiersService;

  @Inject
  private LisTimestampSavePointContainerService savePointContainerService;

  @Inject
  private LisJobModeFinalizer jobModeFinalizer;

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

  private List<ChangedEntityIdentifier<LicenseNumberSavePoint>> getNextPage() {
    return changedEntitiesIdentifiersService
        .getIdentifiersForInitialLoad(lastId);
  }

  protected static int getLastId(
      List<ChangedEntityIdentifier<LicenseNumberSavePoint>> identifiers) {
    identifiers.sort(Comparator.comparing(ChangedEntityIdentifier::getIntId));
    return identifiers.get(identifiers.size() - 1).getIntId();
  }

  @Override
  public LicenseNumberSavePoint defineSavepoint(JobBatch<LicenseNumberSavePoint> jobBatch) {
    return savePointService.defineSavepoint(jobBatch);
  }

  @Override
  public void doFinalizeJob() {
    jobModeFinalizer.doFinalizeJob();
  }


}
