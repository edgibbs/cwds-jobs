package gov.ca.cwds.jobs.cals.facility.cws;

import gov.ca.cwds.jobs.common.RecordChangeOperation;

/**
 * Created by Alexander Serbin on 12/3/2018
 */
public final class QueryConstants {

  public static final String DATE_AFTER = "dateAfter";

  public static final String DATE_BEFORE = "dateBefore";

  private static final String HOME_IDENTIFIER_FIELD_NAME = "home.identifier";

  private static final String AND = " and ";

  private static final String ORDER_BY = " order by ";

  private static final String SHARED_PART = " from ReplicationPlacementHome as home"
      + " where home.licensrCd <> 'CL' " + " and home.facilityType <> 1420 "; // medical facility

  public static final String CWS_CMS_GET_MAX_TIMESTAMP_QUERY =
      "select max(home.replicationLastUpdated)" + SHARED_PART;

  private QueryConstants() {
    // utility class
  }

  public static class InitialMode {

    @SuppressWarnings("squid:S3008") // the name TIMESTAMP_FIELD_NAME matches the regular expression
                                     // '^[A-Z][A-Z0-9]*(_[A-Z0-9]+)*$'
    private static String TIMESTAMP_FIELD_NAME = "home.lastUpdatedTime";

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

    private InitialMode() {
      // utility class
    }
  }

  public static class IncrementalMode {

    //@formatter:off
    public static final String GET_IDENTIFIERS_AFTER_TIMESTAMP_QUERY =
        "SELECT scp.FKPLC_HM_T AS IDENTIFIER, scp.IBMSNAP_OPERATION\n"
      + "  , MAX(NVL(sbp.IBMSNAP_LOGMARKER, scp.IBMSNAP_LOGMARKER)\n"
      + "      ,     scp.IBMSNAP_LOGMARKER)  AS IBMSNAP_LOGMARKER\n"
      + "FROM      {h-schema}HM_SCP_T scp\n"
      + "LEFT JOIN {h-schema}SB_PVDRT sbp ON sbp.IDENTIFIER = scp.FKSB_PVDRT\n"
      + "WHERE scp.IBMSNAP_LOGMARKER > :dateAfter\n"
      + "UNION\n"
      + "SELECT scp.FKPLC_HM_T AS IDENTIFIER, scp.IBMSNAP_OPERATION\n"
      + "  , MAX(NVL(sbp.IBMSNAP_LOGMARKER, scp.IBMSNAP_LOGMARKER)\n"
      + "      ,     scp.IBMSNAP_LOGMARKER)  AS IBMSNAP_LOGMARKER\n"
      + "FROM      {h-schema}HM_SCP_T scp\n"
      + "LEFT JOIN {h-schema}SB_PVDRT sbp ON sbp.IDENTIFIER = scp.FKSB_PVDRT\n"
      + "WHERE sbp.IBMSNAP_LOGMARKER > :dateAfter\n"
      + "UNION\n"
      + "SELECT plh.IDENTIFIER, plh.IBMSNAP_OPERATION\n"
      + "  , MAX(NVL(cst.IBMSNAP_LOGMARKER, plh.IBMSNAP_LOGMARKER)\n"
      + "      , NVL(stf.IBMSNAP_LOGMARKER, plh.IBMSNAP_LOGMARKER)\n"
      + "      ,     plh.IBMSNAP_LOGMARKER)  AS IBMSNAP_LOGMARKER\n"
      + "FROM      {h-schema}PLC_HM_T plh\n"
      + "LEFT JOIN {h-schema}CNTY_CST cst ON cst.IDENTIFIER = plh.FKCNTY_CST\n"
      + "LEFT JOIN {h-schema}STFPERST stf ON stf.IDENTIFIER = cst.FKSTFPERST\n"
      + "WHERE plh.IBMSNAP_LOGMARKER > :dateAfter\n"
      + "  AND plh.LICENSR_CD <> 'CL'\n"
      + "  AND plh.PLC_FCLC   <> 1420\n"
      + "UNION\n"
      + "SELECT plh.IDENTIFIER, plh.IBMSNAP_OPERATION\n"
      + "  , MAX(NVL(cst.IBMSNAP_LOGMARKER, plh.IBMSNAP_LOGMARKER)\n"
      + "      , NVL(stf.IBMSNAP_LOGMARKER, plh.IBMSNAP_LOGMARKER)\n"
      + "      ,     plh.IBMSNAP_LOGMARKER)  AS IBMSNAP_LOGMARKER\n"
      + "FROM      {h-schema}PLC_HM_T plh\n"
      + "LEFT JOIN {h-schema}CNTY_CST cst ON cst.IDENTIFIER = plh.FKCNTY_CST\n"
      + "LEFT JOIN {h-schema}STFPERST stf ON stf.IDENTIFIER = cst.FKSTFPERST\n"
      + "WHERE cst.IBMSNAP_LOGMARKER > :dateAfter\n"
      + "  AND plh.LICENSR_CD <> 'CL'\n"
      + "  AND plh.PLC_FCLC   <> 1420\n"
      + "UNION\n"
      + "SELECT plh.IDENTIFIER, plh.IBMSNAP_OPERATION\n"
      + "  , MAX(NVL(cst.IBMSNAP_LOGMARKER, plh.IBMSNAP_LOGMARKER)\n"
      + "      , NVL(stf.IBMSNAP_LOGMARKER, plh.IBMSNAP_LOGMARKER)\n"
      + "      ,     plh.IBMSNAP_LOGMARKER)  AS IBMSNAP_LOGMARKER\n"
      + "FROM      {h-schema}PLC_HM_T plh\n"
      + "LEFT JOIN {h-schema}CNTY_CST cst ON cst.IDENTIFIER = plh.FKCNTY_CST\n"
      + "LEFT JOIN {h-schema}STFPERST stf ON stf.IDENTIFIER = cst.FKSTFPERST\n"
      + "WHERE stf.IBMSNAP_LOGMARKER > :dateAfter\n"
      + "  AND plh.LICENSR_CD <> 'CL'\n"
      + "  AND plh.PLC_FCLC   <> 1420\n"
      + "ORDER BY 3, 1\n"
      + "FETCH FIRST BATCH_SIZE ROWS ONLY\n"
      + "FOR READ ONLY WITH UR";
    //@formatter:on

    //@formatter:off
    public static final String GET_IDENTIFIERS_BETWEEN_TIMESTAMPS_QUERY =
        "SELECT scp.FKPLC_HM_T AS IDENTIFIER, scp.IBMSNAP_OPERATION\n"
      + "  , MAX(NVL(sbp.IBMSNAP_LOGMARKER, scp.IBMSNAP_LOGMARKER)\n"
      + "      ,     scp.IBMSNAP_LOGMARKER)  AS IBMSNAP_LOGMARKER\n"
      + "FROM      {h-schema}HM_SCP_T scp\n"
      + "LEFT JOIN {h-schema}SB_PVDRT sbp ON sbp.IDENTIFIER = scp.FKSB_PVDRT\n"
      + "WHERE scp.IBMSNAP_LOGMARKER > :dateAfter AND scp.IBMSNAP_LOGMARKER < :dateBefore\n"
      + "UNION\n"
      + "SELECT scp.FKPLC_HM_T AS IDENTIFIER, scp.IBMSNAP_OPERATION\n"
      + "  , MAX(NVL(sbp.IBMSNAP_LOGMARKER, scp.IBMSNAP_LOGMARKER)\n"
      + "      ,     scp.IBMSNAP_LOGMARKER)  AS IBMSNAP_LOGMARKER\n"
      + "FROM      {h-schema}HM_SCP_T scp\n"
      + "LEFT JOIN {h-schema}SB_PVDRT sbp ON sbp.IDENTIFIER = scp.FKSB_PVDRT\n"
      + "WHERE sbp.IBMSNAP_LOGMARKER > :dateAfter AND sbp.IBMSNAP_LOGMARKER < :dateBefore\n"
      + "UNION\n"
      + "SELECT plh.IDENTIFIER, plh.IBMSNAP_OPERATION\n"
      + "  , MAX(NVL(cst.IBMSNAP_LOGMARKER, plh.IBMSNAP_LOGMARKER)\n"
      + "      , NVL(stf.IBMSNAP_LOGMARKER, plh.IBMSNAP_LOGMARKER)\n"
      + "      ,     plh.IBMSNAP_LOGMARKER)  AS IBMSNAP_LOGMARKER\n"
      + "FROM      {h-schema}PLC_HM_T plh\n"
      + "LEFT JOIN {h-schema}CNTY_CST cst ON cst.IDENTIFIER = plh.FKCNTY_CST\n"
      + "LEFT JOIN {h-schema}STFPERST stf ON stf.IDENTIFIER = cst.FKSTFPERST\n"
      + "WHERE plh.IBMSNAP_LOGMARKER > :dateAfter AND plh.IBMSNAP_LOGMARKER < :dateBefore\n"
      + "  AND plh.LICENSR_CD <> 'CL'\n"
      + "  AND plh.PLC_FCLC   <> 1420\n"
      + "UNION\n"
      + "SELECT plh.IDENTIFIER, plh.IBMSNAP_OPERATION\n"
      + "  , MAX(NVL(cst.IBMSNAP_LOGMARKER, plh.IBMSNAP_LOGMARKER)\n"
      + "      , NVL(stf.IBMSNAP_LOGMARKER, plh.IBMSNAP_LOGMARKER)\n"
      + "      ,     plh.IBMSNAP_LOGMARKER)  AS IBMSNAP_LOGMARKER\n"
      + "FROM      {h-schema}PLC_HM_T plh\n"
      + "LEFT JOIN {h-schema}CNTY_CST cst ON cst.IDENTIFIER = plh.FKCNTY_CST\n"
      + "LEFT JOIN {h-schema}STFPERST stf ON stf.IDENTIFIER = cst.FKSTFPERST\n"
      + "WHERE cst.IBMSNAP_LOGMARKER > :dateAfter AND cst.IBMSNAP_LOGMARKER < :dateBefore\n"
      + "  AND plh.LICENSR_CD <> 'CL'\n"
      + "  AND plh.PLC_FCLC   <> 1420\n"
      + "UNION\n"
      + "SELECT plh.IDENTIFIER, plh.IBMSNAP_OPERATION\n"
      + "  , MAX(NVL(cst.IBMSNAP_LOGMARKER, plh.IBMSNAP_LOGMARKER)\n"
      + "      , NVL(stf.IBMSNAP_LOGMARKER, plh.IBMSNAP_LOGMARKER)\n"
      + "      ,     plh.IBMSNAP_LOGMARKER)  AS IBMSNAP_LOGMARKER\n"
      + "FROM      {h-schema}PLC_HM_T plh\n"
      + "LEFT JOIN {h-schema}CNTY_CST cst ON cst.IDENTIFIER = plh.FKCNTY_CST\n"
      + "LEFT JOIN {h-schema}STFPERST stf ON stf.IDENTIFIER = cst.FKSTFPERST\n"
      + "WHERE stf.IBMSNAP_LOGMARKER > :dateAfter AND stf.IBMSNAP_LOGMARKER < :dateBefore\n"
      + "  AND plh.LICENSR_CD <> 'CL'\n"
      + "  AND plh.PLC_FCLC   <> 1420\n"
      + "ORDER BY 3, 1\n"
      + "FETCH FIRST BATCH_SIZE ROWS ONLY\n"
      + "FOR READ ONLY WITH UR";
    //@formatter:on

    //@formatter:off
    public static final String GET_FIRST_TS_AFTER_SAVEPOINT_QUERY =
        "WITH STEP1 AS (\n"
      + " SELECT scp.FKPLC_HM_T AS IDENTIFIER\n"
      + "   , MAX(NVL(sbp.IBMSNAP_LOGMARKER, scp.IBMSNAP_LOGMARKER)\n"
      + "   ,     scp.IBMSNAP_LOGMARKER)  AS IBMSNAP_LOGMARKER\n"
      + " FROM      {h-schema}HM_SCP_T scp\n"
      + " LEFT JOIN {h-schema}SB_PVDRT sbp ON sbp.IDENTIFIER = scp.FKSB_PVDRT\n"
      + " WHERE scp.IBMSNAP_LOGMARKER > :dateAfter\n"
      + " UNION\n"
      + " SELECT scp.FKPLC_HM_T AS IDENTIFIER\n"
      + "   , MAX(NVL(sbp.IBMSNAP_LOGMARKER, scp.IBMSNAP_LOGMARKER)\n"
      + "   ,     scp.IBMSNAP_LOGMARKER)  AS IBMSNAP_LOGMARKER\n"
      + " FROM      {h-schema}HM_SCP_T scp\n"
      + " LEFT JOIN {h-schema}SB_PVDRT sbp ON sbp.IDENTIFIER = scp.FKSB_PVDRT\n"
      + " WHERE sbp.IBMSNAP_LOGMARKER > :dateAfter\n"
      + " UNION\n"
      + " SELECT plh.IDENTIFIER\n"
      + "    , MAX(NVL(cst.IBMSNAP_LOGMARKER, plh.IBMSNAP_LOGMARKER)\n"
      + "    , NVL(stf.IBMSNAP_LOGMARKER, plh.IBMSNAP_LOGMARKER)\n"
      + "    ,     plh.IBMSNAP_LOGMARKER)  AS IBMSNAP_LOGMARKER\n"
      + " FROM      {h-schema}PLC_HM_T plh\n"
      + " LEFT JOIN {h-schema}CNTY_CST cst ON cst.IDENTIFIER = plh.FKCNTY_CST\n"
      + " LEFT JOIN {h-schema}STFPERST stf ON stf.IDENTIFIER = cst.FKSTFPERST\n"
      + " WHERE plh.IBMSNAP_LOGMARKER > :dateAfter\n"
      + "   AND plh.LICENSR_CD <> 'CL'\n"
      + "   AND plh.PLC_FCLC   <> 1420\n"
      + " UNION\n"
      + " SELECT plh.IDENTIFIER \n"
      + "    , MAX(NVL(cst.IBMSNAP_LOGMARKER, plh.IBMSNAP_LOGMARKER)\n"
      + "    , NVL(stf.IBMSNAP_LOGMARKER, plh.IBMSNAP_LOGMARKER)\n"
      + "    ,     plh.IBMSNAP_LOGMARKER)  AS IBMSNAP_LOGMARKER\n"
      + " FROM      {h-schema}PLC_HM_T plh\n"
      + " LEFT JOIN {h-schema}CNTY_CST cst ON cst.IDENTIFIER = plh.FKCNTY_CST\n"
      + " LEFT JOIN {h-schema}STFPERST stf ON stf.IDENTIFIER = cst.FKSTFPERST\n"
      + " WHERE cst.IBMSNAP_LOGMARKER > :dateAfter\n"
      + "   AND plh.LICENSR_CD <> 'CL'\n"
      + "   AND plh.PLC_FCLC   <> 1420\n"
      + " UNION\n"
      + " SELECT plh.IDENTIFIER \n"
      + "    , MAX(NVL(cst.IBMSNAP_LOGMARKER, plh.IBMSNAP_LOGMARKER)\n"
      + "    , NVL(stf.IBMSNAP_LOGMARKER, plh.IBMSNAP_LOGMARKER)\n"
      + "    ,     plh.IBMSNAP_LOGMARKER)  AS IBMSNAP_LOGMARKER\n"
      + " FROM      {h-schema}PLC_HM_T plh\n"
      + " LEFT JOIN {h-schema}CNTY_CST cst ON cst.IDENTIFIER = plh.FKCNTY_CST\n"
      + " LEFT JOIN {h-schema}STFPERST stf ON stf.IDENTIFIER = cst.FKSTFPERST\n"
      + " WHERE stf.IBMSNAP_LOGMARKER > :dateAfter\n"
      + "   AND plh.LICENSR_CD <> 'CL'\n"
      + "   AND plh.PLC_FCLC   <> 1420\n"
      + " ORDER BY 2, 1\n"
      + " FETCH FIRST BATCH_SIZE ROWS ONLY\n"
      + ")\n"
      + "SELECT MAX(s1.IBMSNAP_LOGMARKER)\n"
      + "FROM STEP1 s1\n"
      + "FOR READ ONLY WITH UR";
    //@formatter:on

    //@formatter:off
    public static final String GET_NEXT_SAVEPOINT_QUERY =
        "WITH STEP1 AS (\n"
      + " SELECT scp.IBMSNAP_LOGMARKER, scp.IBMSNAP_OPERATION\n"
      + " FROM {h-schema}HM_SCP_T scp\n"
      + " JOIN {h-schema}SB_PVDRT sbp ON sbp.IDENTIFIER = scp.FKSB_PVDRT\n"
      + " WHERE scp.IBMSNAP_LOGMARKER > :dateAfter\n"
      + " UNION ALL\n"
      + " SELECT sbp.IBMSNAP_LOGMARKER, sbp.IBMSNAP_OPERATION\n"
      + " FROM {h-schema}HM_SCP_T scp\n"
      + " JOIN {h-schema}SB_PVDRT sbp ON sbp.IDENTIFIER = scp.FKSB_PVDRT\n"
      + " WHERE sbp.IBMSNAP_LOGMARKER > :dateAfter\n"   
      + " UNION ALL\n"
      + " SELECT x.IBMSNAP_LOGMARKER, 'X' AS IBMSNAP_OPERATION\n"
      + " FROM (\n"
      + "     SELECT MAX(plh.IBMSNAP_LOGMARKER) AS IBMSNAP_LOGMARKER\n"
      + "     FROM {h-schema}PLC_HM_T plh\n"
      + "     WHERE plh.LICENSR_CD <> 'CL'\n"
      + "       AND plh.PLC_FCLC   <> 1420\n"
      + "       AND plh.IBMSNAP_LOGMARKER > :dateAfter\n"
      + " ) x\n"
      + " UNION ALL\n"
      + " SELECT cst.IBMSNAP_LOGMARKER, cst.IBMSNAP_OPERATION\n"
      + " FROM {h-schema}CNTY_CST cst\n"
      + " WHERE cst.IBMSNAP_LOGMARKER > :dateAfter\n"
      + " UNION ALL\n"
      + " SELECT stf.IBMSNAP_LOGMARKER, stf.IBMSNAP_OPERATION\n"
      + " FROM      {h-schema}CNTY_CST cst\n"
      + " LEFT JOIN {h-schema}STFPERST stf ON stf.IDENTIFIER = cst.FKSTFPERST\n"
      + " WHERE stf.IBMSNAP_LOGMARKER > :dateAfter\n"
      + "), STEP2 AS (\n"
      + " SELECT s1.IBMSNAP_LOGMARKER, s1.IBMSNAP_OPERATION\n"
      + " FROM STEP1 s1\n"
      + " WHERE s1.IBMSNAP_OPERATION IN ('X','I','U','D')\n"
      + "   AND s1.IBMSNAP_LOGMARKER > :dateAfter\n"
      + ")\n"
      + "SELECT MAX(s2.IBMSNAP_LOGMARKER) AS MAX_LST_UPD_TS\n"
      + "FROM STEP2 s2\n"
      + "FOR READ ONLY WITH UR";
    //@formatter:on

    private IncrementalMode() {
      // utility class
    }
  }

}
