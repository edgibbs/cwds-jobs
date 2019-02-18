package gov.ca.cwds.jobs.audit.identifier;

import gov.ca.cwds.jobs.common.RecordChangeOperation;
import gov.ca.cwds.jobs.common.identifier.ChangedEntityIdentifier;
import gov.ca.cwds.jobs.common.savepoint.LocalDateTimeSavePoint;
import gov.ca.cwds.jobs.common.savepoint.TimestampSavePoint;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Created by Alexander Serbin on 10/15/2018
 */
public class AuditEventIdentifier extends
    ChangedEntityIdentifier<TimestampSavePoint<LocalDateTime>> {

  public AuditEventIdentifier(String id,
      LocalDateTime timestamp) {
    super(id, RecordChangeOperation.I, new LocalDateTimeSavePoint(timestamp));
  }

  @Override
  public int compareTo(ChangedEntityIdentifier<TimestampSavePoint<LocalDateTime>> o) {
    return getSavePoint().compareTo(o.getSavePoint());
  }

  @Override
  public Serializable getPrimaryKey() {
    return getId();
  }

}
