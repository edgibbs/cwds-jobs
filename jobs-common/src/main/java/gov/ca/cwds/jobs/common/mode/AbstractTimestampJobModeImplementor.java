package gov.ca.cwds.jobs.common.mode;

import com.google.inject.Inject;
import gov.ca.cwds.jobs.common.batch.JobBatch;
import gov.ca.cwds.jobs.common.batch.JobBatchSize;
import gov.ca.cwds.jobs.common.batch.PageRequest;
import gov.ca.cwds.jobs.common.identifier.ChangedEntitiesIdentifiersService;
import gov.ca.cwds.jobs.common.identifier.ChangedEntityIdentifier;
import gov.ca.cwds.jobs.common.savepoint.SavePointContainer;
import gov.ca.cwds.jobs.common.savepoint.SavePointService;
import gov.ca.cwds.jobs.common.savepoint.TimestampSavePoint;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by Alexander Serbin on 6/19/2018.
 */
public abstract class AbstractTimestampJobModeImplementor<E, T, J extends JobMode> extends
    AbstractJobModeImplementor<E, TimestampSavePoint<T>, J> {

  private static final Logger LOGGER = LoggerFactory
      .getLogger(AbstractTimestampJobModeImplementor.class);

  private int offset = 0;

  @Inject
  @JobBatchSize
  private int batchSize;

  @Inject
  private SavePointService<TimestampSavePoint<T>, J> savePointService;

  @Inject
  private ChangedEntitiesIdentifiersService<TimestampSavePoint<T>>
      changedEntitiesIdentifiersService;

  @Override
  public TimestampSavePoint<T> loadSavePoint(
      Class<? extends SavePointContainer<? extends TimestampSavePoint<T>, J>> savePointContainerClass) {
    return savePointService.loadSavePoint(savePointContainerClass);
  }

  @Override
  public TimestampSavePoint<T> defineSavepoint(JobBatch<TimestampSavePoint<T>> jobBatch) {
    return savePointService.defineSavepoint(jobBatch);
  }

  @Override
  public void saveSavePoint(TimestampSavePoint<T> savePoint) {
    savePointService.saveSavePoint(savePoint);
  }

  @Override
  public List<JobBatch<TimestampSavePoint<T>>> getNextPortion() {
    LOGGER.info("Getting next portion");
    List<ChangedEntityIdentifier<TimestampSavePoint<T>>> identifiers = getNextPage();
    if (identifiers.isEmpty()) {
      return Collections.emptyList();
    }
    if (timeStampsAreEmpty(identifiers)) {
      LOGGER.info("Found page with all empty timestamps");
      offset += batchSize;
      return Collections.singletonList(new JobBatch<>(identifiers));
    } else {
      return calculateNextPortion(identifiers);
    }
  }

  private List<ChangedEntityIdentifier<TimestampSavePoint<T>>> getNextPage() {
    return getNextPage(new PageRequest(offset, batchSize));
  }

  protected abstract List<ChangedEntityIdentifier<TimestampSavePoint<T>>> getNextPage(
      PageRequest pageRequest);

  private List<JobBatch<TimestampSavePoint<T>>> calculateNextPortion(
      List<ChangedEntityIdentifier<TimestampSavePoint<T>>> identifiers) {

    TimestampSavePoint<T> lastTimestamp = getLastTimestamp(identifiers);
    TimestampSavePoint<T> firstChangedTimestamp =
        changedEntitiesIdentifiersService.getFirstChangedTimestamp(lastTimestamp);
    List<JobBatch<TimestampSavePoint<T>>> batches = new ArrayList<>();
    batches.add(new JobBatch<>(identifiers));
    offset += identifiers.size();

    if (firstChangedTimestamp != null) {
      List<ChangedEntityIdentifier<TimestampSavePoint<T>>> identifiersBeforeChangedTimestamp =
          changedEntitiesIdentifiersService.getIdentifiersBeforeChangedTimestamp(
              firstChangedTimestamp, offset);
      if (!identifiersBeforeChangedTimestamp.isEmpty()) {
        batches.add(new JobBatch<>(identifiersBeforeChangedTimestamp));
        offset += identifiersBeforeChangedTimestamp.size();
      }
    }
    return batches;
  }

  private static <T> TimestampSavePoint<T> getLastTimestamp(
      List<ChangedEntityIdentifier<TimestampSavePoint<T>>> identifiers) {
    return identifiers.get(identifiers.size() - 1).getSavePoint();
  }

  private boolean timeStampsAreEmpty(
      List<ChangedEntityIdentifier<TimestampSavePoint<T>>> identifiers) {
    return getLastTimestamp(identifiers) == null;
  }

  public void setBatchSize(int batchSize) {
    this.batchSize = batchSize;
  }

}
