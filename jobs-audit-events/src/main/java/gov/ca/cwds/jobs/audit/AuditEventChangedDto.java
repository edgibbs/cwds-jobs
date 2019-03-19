package gov.ca.cwds.jobs.audit;

import gov.ca.cwds.idm.persistence.ns.entity.NsAuditEvent;
import gov.ca.cwds.jobs.common.ChangedDTO;
import gov.ca.cwds.jobs.common.RecordChangeOperation;


public class AuditEventChangedDto implements ChangedDTO<String> {

  private final String id;
  private String event;

  public AuditEventChangedDto(NsAuditEvent event) {
    this.id = event.getId();
    this.event = event.getAuditEvent();
  }

  @Override
  public RecordChangeOperation getRecordChangeOperation() {
    return RecordChangeOperation.I;
  }

  @Override
  public String getDTO() {
    return event;
  }

  @Override
  public String getId() {
    return id;
  }
}
