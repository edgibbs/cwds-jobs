package gov.ca.cwds.jobs.audit.identifier;

import static gov.ca.cwds.jobs.audit.inject.NsDataAccessModule.NS;

import com.google.inject.Inject;
import gov.ca.cwds.jobs.audit.NsAuditEventDao;
import gov.ca.cwds.jobs.common.identifier.ChangedEntityIdentifier;
import gov.ca.cwds.jobs.common.savepoint.TimestampSavePoint;
import io.dropwizard.hibernate.UnitOfWork;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public class IncrementalModeAuditEventIdentifiersService extends AuditEventIdentifiersService {


  @Inject
  private NsAuditEventDao dao;

  @UnitOfWork(NS)
  @Override
  protected Optional<LocalDateTime> findNextSavePoint(LocalDateTime timestamp, int batchSize) {
    return dao.getNextSavePoint(timestamp, batchSize);
  }

  @UnitOfWork(NS)
  @Override
  Optional<LocalDateTime> findFirstChangedTimestampAfterSavepoint(
      TimestampSavePoint<LocalDateTime> savePoint) {
    return dao.getFirstUnprocessedChangedTimestampAfterSavepoint(savePoint.getTimestamp());
  }

  @UnitOfWork(NS)
  @Override
  List<ChangedEntityIdentifier<TimestampSavePoint<LocalDateTime>>> getAuditEventIdentifiers(
      LocalDateTime previousTimestamp, Optional<TimestampSavePoint<LocalDateTime>> nextTimestamp) {
    if (nextTimestamp.isPresent()) {
      return dao.getUnprocessedIdentifiers(previousTimestamp, nextTimestamp.get().getTimestamp());
    } else {
      return dao.getUnprocessedIdentifiersAfter(previousTimestamp);
    }
  }
}