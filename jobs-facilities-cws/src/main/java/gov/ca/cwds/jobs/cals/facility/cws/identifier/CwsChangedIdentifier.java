package gov.ca.cwds.jobs.cals.facility.cws.identifier;

import java.io.Serializable;
import java.time.LocalDateTime;

import javax.persistence.Entity;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;

import gov.ca.cwds.jobs.cals.facility.cws.QueryConstants;
import gov.ca.cwds.jobs.common.RecordChangeOperation;
import gov.ca.cwds.jobs.common.identifier.ChangedEntityIdentifier;
import gov.ca.cwds.jobs.common.savepoint.LocalDateTimeSavePoint;
import gov.ca.cwds.jobs.common.savepoint.TimestampSavePoint;

/**
 * Created by Alexander Serbin on 7/6/2018.
 */
@NamedQueries({@NamedQuery(name = CwsChangedIdentifier.CWSCMS_GET_MAX_TIMESTAMP_QUERY_NAME,
    query = QueryConstants.CWS_CMS_GET_MAX_TIMESTAMP_QUERY)})
@Entity
public class CwsChangedIdentifier
    extends ChangedEntityIdentifier<TimestampSavePoint<LocalDateTime>> {

  private static final long serialVersionUID = -2320143335869667227L;

  public static final String CWSCMS_GET_MAX_TIMESTAMP_QUERY_NAME =
      "RecordChange.cwscmsMaxTimestampQuery";

  protected CwsChangedIdentifier() {}

  public CwsChangedIdentifier(String id, LocalDateTime timestamp) {
    super(id, RecordChangeOperation.I, new LocalDateTimeSavePoint(timestamp));
  }

  public CwsChangedIdentifier(String id, RecordChangeOperation recordChangeOperation,
      LocalDateTime timestamp) {
    super(id, recordChangeOperation, new LocalDateTimeSavePoint(timestamp));
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
