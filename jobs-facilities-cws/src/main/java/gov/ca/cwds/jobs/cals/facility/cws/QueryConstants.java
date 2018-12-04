package gov.ca.cwds.jobs.cals.facility.cws;

import gov.ca.cwds.jobs.common.RecordChangeOperation;

/**
 * Created by Alexander Serbin on 12/3/2018
 */
public final class QueryConstants {

  QueryConstants() {
  }

  public static final String DATE_AFTER = "dateAfter";

  public static final String DATE_BEFORE = "dateBefore";

  private static final String SHARED_PART =
      " from ReplicationPlacementHome as home"
          + " where home.licensrCd <> 'CL' "
          + " and home.facilityType <> 1420 ";  //medical facility

  public static final String CWS_CMS_GET_MAX_TIMESTAMP_QUERY =
      "select max(home.replicationLastUpdated)"
          + SHARED_PART;

  public static class InitialMode {

    private static String TIMESTAMP_FIELD_NAME = "home.lastUpdatedTime";

    public static final String GET_IDENTIFIERS_AFTER_TIMESTAMP_QUERY =
        "select new CwsChangedIdentifier(home.identifier, "
            + InitialMode.TIMESTAMP_FIELD_NAME + ") " +
            SHARED_PART +
            " and " + InitialMode.TIMESTAMP_FIELD_NAME + " > :" + DATE_AFTER +
            " and home.recordChangeOperation != '" + RecordChangeOperation.D.name() + "'" +
            " order by " + InitialMode.TIMESTAMP_FIELD_NAME + ", home.identifier";

    public static final String GET_IDENTIFIERS_BETWEEN_TIMESTAMPS_QUERY =
        "select new CwsChangedIdentifier(home.identifier, "
            + InitialMode.TIMESTAMP_FIELD_NAME + ") " +
            SHARED_PART +
            " and " + InitialMode.TIMESTAMP_FIELD_NAME + " > :" + DATE_AFTER +
            " and " + InitialMode.TIMESTAMP_FIELD_NAME + " < :" + DATE_BEFORE +
            " and home.recordChangeOperation != '" + RecordChangeOperation.D.name() + "'" +
            " order by " + InitialMode.TIMESTAMP_FIELD_NAME + ", home.identifier";

    public static final String GET_NEXT_SAVEPOINT_QUERY =
        "select " + InitialMode.TIMESTAMP_FIELD_NAME + SHARED_PART
            + " and " + InitialMode.TIMESTAMP_FIELD_NAME + " > :" + DATE_AFTER +
            " order by " + InitialMode.TIMESTAMP_FIELD_NAME + ", home.identifier";
  }

  public static class IncrementalMode {

    private static String TIMESTAMP_FIELD_NAME = "home.replicationLastUpdated";

    public static final String GET_IDENTIFIERS_AFTER_TIMESTAMP_QUERY =
        "select new CwsChangedIdentifier(home.identifier, home.recordChangeOperation,"
            + IncrementalMode.TIMESTAMP_FIELD_NAME + ")" + SHARED_PART +
            " and " + IncrementalMode.TIMESTAMP_FIELD_NAME + " > :" + DATE_AFTER +
            " order by "
            + IncrementalMode.TIMESTAMP_FIELD_NAME + ", home.identifier";

    public static final String GET_IDENTIFIERS_BETWEEN_TIMESTAMPS_QUERY =
        "select new CwsChangedIdentifier(home.identifier, home.recordChangeOperation,"
            + IncrementalMode.TIMESTAMP_FIELD_NAME + ")" + SHARED_PART +
            " and " + IncrementalMode.TIMESTAMP_FIELD_NAME + " > :" + DATE_AFTER +
            " and " + IncrementalMode.TIMESTAMP_FIELD_NAME + " < :" + DATE_BEFORE +
            " order by " + IncrementalMode.TIMESTAMP_FIELD_NAME + ", home.identifier";

    public static final String GET_NEXT_SAVEPOINT_QUERY =
        "select " + IncrementalMode.TIMESTAMP_FIELD_NAME + SHARED_PART
            + " and " + IncrementalMode.TIMESTAMP_FIELD_NAME + " > :" + DATE_AFTER +
            " order by " + IncrementalMode.TIMESTAMP_FIELD_NAME + ", home.identifier";

  }

}
