package gov.ca.cwds.jobs.audit.identifier;

import com.google.inject.Inject;
import gov.ca.cwds.jobs.common.batch.JobBatchSize;
import gov.ca.cwds.jobs.common.identifier.ChangedEntitiesIdentifiersService;
import gov.ca.cwds.jobs.common.identifier.ChangedEntityIdentifier;
import gov.ca.cwds.jobs.common.savepoint.LocalDateTimeSavePoint;
import gov.ca.cwds.jobs.common.savepoint.TimestampSavePoint;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * Created by Alexander Serbin on 2/17/2019.
 */
public abstract class AuditEventIdentifiersService
    implements ChangedEntitiesIdentifiersService<LocalDateTime> {


  @Inject
  @JobBatchSize
  private int batchSize;

  @Override
  public final Optional<TimestampSavePoint<LocalDateTime>> getNextSavePoint(
      TimestampSavePoint<LocalDateTime> previousSavePoint) {
    return findNextSavePoint(previousSavePoint.getTimestamp(), batchSize).map(
        LocalDateTimeSavePoint::new);
  }


  @Override
  public final Optional<TimestampSavePoint<LocalDateTime>> getFirstChangedTimestampAfterSavepoint(
      TimestampSavePoint<LocalDateTime> savePoint) {
    return findFirstChangedTimestampAfterSavepoint(savePoint).map(LocalDateTimeSavePoint::new);
  }

  @Override
  public final List<ChangedEntityIdentifier<TimestampSavePoint<LocalDateTime>>> getIdentifiers(
      Optional<TimestampSavePoint<LocalDateTime>> previousTimestamp,
      Optional<TimestampSavePoint<LocalDateTime>> nextTimestamp) {
    if (previousTimestamp.isPresent()) {
      return getAuditEventIdentifiers(previousTimestamp.get().getTimestamp(), nextTimestamp);
    } else {
      return Collections.emptyList();
    }
  }

  abstract Optional<LocalDateTime> findFirstChangedTimestampAfterSavepoint(
      TimestampSavePoint<LocalDateTime> savePoint);

  abstract List<ChangedEntityIdentifier<TimestampSavePoint<LocalDateTime>>> getAuditEventIdentifiers(
      LocalDateTime previousTimestamp, Optional<TimestampSavePoint<LocalDateTime>> nextTimestamp);

  abstract Optional<LocalDateTime> findNextSavePoint(LocalDateTime timestamp, int batchSize);


}