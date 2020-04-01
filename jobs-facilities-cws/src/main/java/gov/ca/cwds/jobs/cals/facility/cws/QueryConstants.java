package gov.ca.cwds.jobs.cals.facility.cws;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gov.ca.cwds.jobs.common.RecordChangeOperation;

/**
 * Created by Alexander Serbin on 12/3/2018
 */
public final class QueryConstants {

  private static final Logger LOGGER = LoggerFactory.getLogger(QueryConstants.class);

  public static final String DATE_AFTER = "dateAfter";
  public static final String DATE_BEFORE = "dateBefore";

  private static final String HOME_IDENTIFIER_FIELD_NAME = "home.identifier";
  private static final String AND = " and ";
  private static final String ORDER_BY = " order by ";

  // CMO-475: discrepancy between CWS/CMS and CARES Facility Search.
  // This query ignores other affected tables other than PLC_HM_T.
  private static final String SHARED_PART = " from ReplicationPlacementHome as home"
      + " where home.licensrCd <> 'CL' and home.facilityType <> 1420"; // medical facility

  public static final String CWS_CMS_GET_MAX_TIMESTAMP_QUERY =
      "select max(home.replicationLastUpdated)" + SHARED_PART;

  private QueryConstants() {
    // utility class
  }

  public static class InitialMode {

    @SuppressWarnings("squid:S3008") // the name TIMESTAMP_FIELD_NAME matches the regular expression
                                     // '^[A-Z][A-Z0-9]*(_[A-Z0-9]+)*$'
    private static final String TIMESTAMP_FIELD_NAME = "home.lastUpdatedTime";

    public static final String GET_IDENTIFIERS_AFTER_TIMESTAMP_QUERY =
        "select new CwsChangedIdentifier(home.identifier, " + InitialMode.TIMESTAMP_FIELD_NAME
            + ") " + SHARED_PART + AND + InitialMode.TIMESTAMP_FIELD_NAME + " > :" + DATE_AFTER
            + " and home.recordChangeOperation != '" + RecordChangeOperation.D.name() + "'"
            + ORDER_BY + InitialMode.TIMESTAMP_FIELD_NAME + ", " + HOME_IDENTIFIER_FIELD_NAME;

    public static final String GET_IDENTIFIERS_BETWEEN_TIMESTAMPS_QUERY =
        "select new CwsChangedIdentifier(home.identifier, " + InitialMode.TIMESTAMP_FIELD_NAME
            + ") " + SHARED_PART + AND + InitialMode.TIMESTAMP_FIELD_NAME + " > :" + DATE_AFTER
            + AND + InitialMode.TIMESTAMP_FIELD_NAME + " < :" + DATE_BEFORE
            + " and home.recordChangeOperation != '" + RecordChangeOperation.D.name() + "'"
            + ORDER_BY + InitialMode.TIMESTAMP_FIELD_NAME + ", " + HOME_IDENTIFIER_FIELD_NAME;

    public static final String GET_NEXT_SAVEPOINT_QUERY =
        "select " + InitialMode.TIMESTAMP_FIELD_NAME + SHARED_PART + AND
            + InitialMode.TIMESTAMP_FIELD_NAME + " > :" + DATE_AFTER + ORDER_BY
            + InitialMode.TIMESTAMP_FIELD_NAME + ", " + HOME_IDENTIFIER_FIELD_NAME;

    static {
      LOGGER.info("INITIAL MODE: GET_NEXT_SAVEPOINT_QUERY: {}",
          InitialMode.GET_NEXT_SAVEPOINT_QUERY);
    }

    private InitialMode() {
      // utility class
    }
  }

  public static class IncrementalMode {

    @SuppressWarnings("squid:S3008") // the name TIMESTAMP_FIELD_NAME matches the regular expression
                                     // '^[A-Z][A-Z0-9]*(_[A-Z0-9]+)*$'
    private static final String TIMESTAMP_FIELD_NAME = "home.replicationLastUpdated";

    public static final String GET_IDENTIFIERS_AFTER_TIMESTAMP_QUERY =
        "select new CwsChangedIdentifier(home.identifier, home.recordChangeOperation,"
            + IncrementalMode.TIMESTAMP_FIELD_NAME + ")" + SHARED_PART + AND
            + IncrementalMode.TIMESTAMP_FIELD_NAME + " > :" + DATE_AFTER + ORDER_BY
            + IncrementalMode.TIMESTAMP_FIELD_NAME + ", " + HOME_IDENTIFIER_FIELD_NAME;

    public static final String GET_IDENTIFIERS_BETWEEN_TIMESTAMPS_QUERY =
        "select new CwsChangedIdentifier(home.identifier, home.recordChangeOperation,"
            + IncrementalMode.TIMESTAMP_FIELD_NAME + ")" + SHARED_PART + AND
            + IncrementalMode.TIMESTAMP_FIELD_NAME + " > :" + DATE_AFTER + AND
            + IncrementalMode.TIMESTAMP_FIELD_NAME + " < :" + DATE_BEFORE + ORDER_BY
            + IncrementalMode.TIMESTAMP_FIELD_NAME + ", " + HOME_IDENTIFIER_FIELD_NAME;

    public static final String GET_NEXT_SAVEPOINT_QUERY =
        "select " + IncrementalMode.TIMESTAMP_FIELD_NAME + SHARED_PART + AND
            + IncrementalMode.TIMESTAMP_FIELD_NAME + " > :" + DATE_AFTER + ORDER_BY
            + IncrementalMode.TIMESTAMP_FIELD_NAME + ", " + HOME_IDENTIFIER_FIELD_NAME;

    static {
      LOGGER.info("INCREMENTAL MODE: GET_NEXT_SAVEPOINT_QUERY: {}",
          IncrementalMode.GET_NEXT_SAVEPOINT_QUERY);
    }

    private IncrementalMode() {
      // utility class
    }
  }

}
