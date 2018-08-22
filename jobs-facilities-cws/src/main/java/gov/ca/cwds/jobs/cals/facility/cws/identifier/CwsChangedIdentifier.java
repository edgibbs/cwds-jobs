package gov.ca.cwds.jobs.cals.facility.cws.identifier;

import gov.ca.cwds.jobs.common.RecordChangeOperation;
import gov.ca.cwds.jobs.common.identifier.ChangedEntityIdentifier;
import gov.ca.cwds.jobs.common.savepoint.LocalDateTimeSavePoint;
import gov.ca.cwds.jobs.common.savepoint.TimestampSavePoint;
import java.io.Serializable;
import java.time.LocalDateTime;
import javax.persistence.Entity;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;

/**
 * Created by Alexander Serbin on 7/6/2018.
 */

@NamedQueries({@NamedQuery(
    name = CwsChangedIdentifier.CWSCMS_INITIAL_LOAD_QUERY_NAME,
    query = CwsChangedIdentifier.CWS_CMS_INITIAL_LOAD_QUERY
), @NamedQuery(
    name = CwsChangedIdentifier.CWSCMS_INCREMENTAL_LOAD_QUERY_NAME,
    query = CwsChangedIdentifier.CWS_CMS_INCREMENTAL_LOAD_QUERY
), @NamedQuery(
    name = CwsChangedIdentifier.CWSCMS_GET_MAX_TIMESTAMP_QUERY_NAME,
    query = CwsChangedIdentifier.CWS_CMS_GET_MAX_TIMESTAMP_QUERY
)
})
@Entity
public class CwsChangedIdentifier extends
    ChangedEntityIdentifier<TimestampSavePoint<LocalDateTime>> {

  private static final String SHARED_PART =
      " from ReplicationPlacementHome as home"
          + " where home.licensrCd <> 'CL' "
          + " and home.facilityType <> 1420 ";  //medical facility

  static final String CWS_CMS_GET_MAX_TIMESTAMP_QUERY =
      "select max(home.replicationLastUpdated)"
          + SHARED_PART;

  static final String CWS_CMS_INITIAL_LOAD_QUERY =
      "select new CwsChangedIdentifier(home.identifier,"
          + "home.lastUpdatedTime) "
          + SHARED_PART
          + " and home.lastUpdatedTime > :dateAfter "
          + " order by home.lastUpdatedTime, home.identifier";

  static final String CWS_CMS_INCREMENTAL_LOAD_QUERY =
      "select new CwsChangedIdentifier(home.identifier,"
          + "home.recordChangeOperation, "
          + "home.replicationLastUpdated) "
          + SHARED_PART
          + " and home.replicationLastUpdated > :dateAfter"
          + " order by home.replicationLastUpdated, home.identifier";

  public static final String CWSCMS_INITIAL_LOAD_QUERY_NAME = "RecordChange.cwscmsInitialLoadQuery";
  public static final String CWSCMS_INCREMENTAL_LOAD_QUERY_NAME = "RecordChange.cwscmsIncrementalLoadQuery";
  public static final String CWSCMS_GET_MAX_TIMESTAMP_QUERY_NAME = "RecordChange.cwscmsMaxTimestampQuery";

  protected CwsChangedIdentifier() {
  }

  public CwsChangedIdentifier(String id,
      LocalDateTime timestamp) {
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
