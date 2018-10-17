package gov.ca.cwds.jobs.common.identifier;

import com.google.inject.Inject;
import gov.ca.cwds.jobs.common.TestEntityDao;
import gov.ca.cwds.jobs.common.batch.JobBatchSize;
import gov.ca.cwds.jobs.common.savepoint.LocalDateTimeSavePoint;
import gov.ca.cwds.jobs.common.savepoint.TimestampSavePoint;
import io.dropwizard.hibernate.UnitOfWork;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * Created by Alexander Serbin on 10/13/2018
 */
public class TestIdentifiersService implements
    ChangedEntitiesIdentifiersService<LocalDateTime> {

  @Inject
  @JobBatchSize
  private int batchSize;

  @Inject
  private TestEntityDao dao;

  @Override
  @UnitOfWork("test")
  public Optional<TimestampSavePoint<LocalDateTime>> getNextSavePoint(
      TimestampSavePoint<LocalDateTime> previousSavePoint) {
    return dao.getNextSavePoint(previousSavePoint.getTimestamp(), batchSize).map(
        LocalDateTimeSavePoint::new);
  }

  @Override
  @UnitOfWork("test")
  public Optional<TimestampSavePoint<LocalDateTime>> getFirstChangedTimestampAfterSavepoint(
      TimestampSavePoint<LocalDateTime> savePoint) {
    return dao.getFirstChangedTimestampAfterSavepoint(savePoint.getTimestamp())
        .map(LocalDateTimeSavePoint::new);
  }

  @Override
  @UnitOfWork("test")
  public List<ChangedEntityIdentifier<TimestampSavePoint<LocalDateTime>>> getIdentifiers(
      Optional<TimestampSavePoint<LocalDateTime>> previousTimestamp,
      Optional<TimestampSavePoint<LocalDateTime>> nextTimestamp) {
    List<ChangedEntityIdentifier<TimestampSavePoint<LocalDateTime>>> identifiers;
    if (previousTimestamp.isPresent()) {
      if (nextTimestamp.isPresent()) {
        return dao.getIdentifiers(previousTimestamp.get().getTimestamp(),
            nextTimestamp.get().getTimestamp());
      } else {
        return dao.getIdentifiers(previousTimestamp.get().getTimestamp());
      }
    } else {
      return Collections.emptyList();
    }
  }

}
