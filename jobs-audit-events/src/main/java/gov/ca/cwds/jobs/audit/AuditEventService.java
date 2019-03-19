package gov.ca.cwds.jobs.audit;

import static gov.ca.cwds.jobs.audit.inject.NsDataAccessModule.NS;

import com.google.inject.Inject;
import gov.ca.cwds.jobs.common.entity.ChangedEntityService;
import gov.ca.cwds.jobs.common.identifier.ChangedEntityIdentifier;
import io.dropwizard.hibernate.UnitOfWork;
import java.time.LocalDateTime;

/**
 * @author CWDS TPT-2
 */

public class AuditEventService implements ChangedEntityService<AuditEventChangedDto> {

  @Inject
  private NsAuditEventDao dao;

  @Override
  @UnitOfWork(NS)
  public AuditEventChangedDto loadEntity(ChangedEntityIdentifier identifier) {
    return new AuditEventChangedDto(dao.find(identifier.getId()));
  }

  @Override
  @UnitOfWork(NS)
  public void markAllBeforeTimeStampAsProcessed(LocalDateTime timeStamp) {
    dao.markAllBeforeTimeStampAsProcessed(timeStamp);
  }

}
