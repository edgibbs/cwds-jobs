package gov.ca.cwds.jobs.common.iterator;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;

import gov.ca.cwds.jobs.common.batch.JobBatch;
import gov.ca.cwds.jobs.common.identifier.ChangedEntitiesIdentifiersService;
import gov.ca.cwds.jobs.common.savepoint.SavePointService;
import gov.ca.cwds.jobs.common.savepoint.TimestampSavePoint;

/**
 * Created by Alexander Serbin on 10/12/2018
 */
public class TimestampJobBatchIterator<T> implements JobBatchIterator<TimestampSavePoint<T>> {

  private static final Logger LOGGER = LoggerFactory.getLogger(TimestampJobBatchIterator.class);

  @Inject
  private SavePointService<TimestampSavePoint<T>> savePointService;

  @Inject
  private ChangedEntitiesIdentifiersService<T> identifiersService;

  @Override
  public JobBatch<TimestampSavePoint<T>> getNextPortion() {
    LOGGER.info("Getting next portion");
    final TimestampSavePoint<T> previousSavePoint = savePointService.loadSavePoint();
    LOGGER.debug("getNextPortion: previousSavePoint: {}", previousSavePoint);

    final Optional<TimestampSavePoint<T>> nextSavePoint =
        identifiersService.getNextSavePoint(previousSavePoint);
    LOGGER.debug("getNextPortion: nextSavePoint: {}", nextSavePoint);

    final Optional<TimestampSavePoint<T>> firstChangedTimestamp =
        nextSavePoint.flatMap(identifiersService::getFirstChangedTimestampAfterSavepoint);
    return new JobBatch<>(
        identifiersService.getIdentifiers(Optional.of(previousSavePoint), firstChangedTimestamp));
  }

}
