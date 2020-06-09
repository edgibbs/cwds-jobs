package gov.ca.cwds.jobs.common.identifier;

import java.io.Serializable;

import gov.ca.cwds.jobs.common.RecordChangeOperation;
import gov.ca.cwds.jobs.common.savepoint.TimestampSavePoint;

/**
 * Created by Alexander Serbin on 6/27/2018.
 */
public class TimestampIdentifier<T> extends ChangedEntityIdentifier<TimestampSavePoint<T>> {

  private static final long serialVersionUID = 1L;

  public TimestampIdentifier(String id, TimestampSavePoint<T> savePoint) {
    super(id, savePoint);
  }

  public TimestampIdentifier(String id, RecordChangeOperation recordChangeOperation,
      TimestampSavePoint<T> savePoint) {
    super(id, recordChangeOperation, savePoint);
  }

  @Override
  public int compareTo(ChangedEntityIdentifier<TimestampSavePoint<T>> o) {
    return getSavePoint().compareTo(o.getSavePoint());
  }

  @Override
  public Serializable getPrimaryKey() {
    return getId();
  }

}
