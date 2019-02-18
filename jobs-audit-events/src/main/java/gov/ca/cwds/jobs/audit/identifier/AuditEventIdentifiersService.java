package gov.ca.cwds.jobs.audit.identifier;

import static gov.ca.cwds.jobs.audit.inject.NsDataAccessModule.NS;

import com.google.inject.Inject;
import gov.ca.cwds.jobs.audit.NsAuditEventDao;
import gov.ca.cwds.jobs.common.batch.JobBatchSize;
import gov.ca.cwds.jobs.common.identifier.ChangedEntitiesIdentifiersService;
import gov.ca.cwds.jobs.common.identifier.ChangedEntityIdentifier;
import gov.ca.cwds.jobs.common.savepoint.LocalDateTimeSavePoint;
import gov.ca.cwds.jobs.common.savepoint.TimestampSavePoint;
import io.dropwizard.hibernate.UnitOfWork;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * Created by Alexander Serbin on 3/6/2018.
 */
public class AuditEventIdentifiersService
    implements ChangedEntitiesIdentifiersService<LocalDateTime> {

  @Inject
  private NsAuditEventDao dao;

  @Inject
  @JobBatchSize
  private int batchSize;

  @Override
  @UnitOfWork(NS)
  public Optional<TimestampSavePoint<LocalDateTime>> getNextSavePoint(
      TimestampSavePoint<LocalDateTime> previousSavePoint) {
    return dao.getNextSavePoint(previousSavePoint.getTimestamp(), batchSize).map(
        LocalDateTimeSavePoint::new);
  }


  @Override
  @UnitOfWork(NS)
  public Optional<TimestampSavePoint<LocalDateTime>> getFirstChangedTimestampAfterSavepoint(
      TimestampSavePoint<LocalDateTime> savePoint) {
    return dao.getFirstChangedTimestampAfterSavepoint(savePoint.getTimestamp())
        .map(LocalDateTimeSavePoint::new);
  }

  @Override
  @UnitOfWork(NS)
  public List<ChangedEntityIdentifier<TimestampSavePoint<LocalDateTime>>> getIdentifiers(
      Optional<TimestampSavePoint<LocalDateTime>> previousTimestamp,
      Optional<TimestampSavePoint<LocalDateTime>> nextTimestamp) {
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