package gov.ca.cwds.jobs.cals.facility.cws;

/**
 * Created by Alexander Serbin on 12/3/2018
 */
public final class QueryConstants {

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

    //@formatter:off
    public static final String GET_IDENTIFIERS_AFTER_TIMESTAMP_QUERY =
          "SELECT plh.IDENTIFIER, plh.IBMSNAP_OPERATION, plh.LST_UPD_TS AS IBMSNAP_LOGMARKER\n"
        + "FROM      {h-schema}PLC_HM_T plh\n"
        + "LEFT JOIN {h-schema}CNTY_CST cst ON cst.IDENTIFIER = plh.FKCNTY_CST\n"
        + "LEFT JOIN {h-schema}STFPERST stf ON stf.IDENTIFIER = cst.FKSTFPERST\n"
        + "WHERE plh.LICENSR_CD <> 'CL'\n"
        + "  AND plh.PLC_FCLC   <> 1420\n"
        + "  AND plh.IBMSNAP_OPERATION IN ('I','U')\n"
        + "  AND (\n"
        + "      plh.LST_UPD_TS > :dateAfter\n"
        + "   OR cst.LST_UPD_TS > :dateAfter\n"
        + "   OR stf.LST_UPD_TS > :dateAfter\n"
        + ")\n"
        + "ORDER BY plh.IBMSNAP_LOGMARKER, cst.IBMSNAP_LOGMARKER, stf.IBMSNAP_LOGMARKER, plh.IDENTIFIER\n"
        + "FETCH FIRST BATCH_SIZE ROWS ONLY\n"
        + "FOR READ ONLY WITH UR";
    //@formatter:on

    //@formatter:off
    public static final String GET_IDENTIFIERS_BETWEEN_TIMESTAMPS_QUERY =
        "WITH STEP1 AS (\n"
      + " SELECT\n"
      + "   plh.LST_UPD_TS AS PLH_TS,\n"
      + "   cst.LST_UPD_TS AS CST_TS,\n"
      + "   stf.LST_UPD_TS AS STF_TS,\n"
      + "   plh.IDENTIFIER\n"
      + " FROM      {h-schema}PLC_HM_T plh\n"
      + " LEFT JOIN {h-schema}CNTY_CST cst ON cst.IDENTIFIER = plh.FKCNTY_CST\n"
      + " LEFT JOIN {h-schema}STFPERST stf ON stf.IDENTIFIER = cst.FKSTFPERST\n"
      + " WHERE plh.LICENSR_CD <> 'CL'\n"
      + "   AND plh.PLC_FCLC   <> 1420\n"
      + "   AND plh.IBMSNAP_OPERATION IN ('I','U')\n"
      + "      AND (\n"
      + "          plh.LST_UPD_TS BETWEEN :dateBefore AND :dateAfter\n"
      + "       OR cst.LST_UPD_TS BETWEEN :dateBefore AND :dateAfter\n"
      + "       OR stf.LST_UPD_TS BETWEEN :dateBefore AND :dateAfter\n"
      + "    )\n"
      + " ORDER BY plh.LST_UPD_TS, cst.LST_UPD_TS, stf.LST_UPD_TS, plh.IDENTIFIER\n"
      + " FETCH FIRST BATCH_SIZE ROWS ONLY\n"
      + ")\n"
      + "SELECT MAX(x.LAST_TS) FROM (\n"
      + "  SELECT MAX(s1.PLH_TS) AS LAST_TS FROM STEP1 s1\n"
      + "  UNION ALL\n"
      + "  SELECT MAX(s1.CST_TS) AS LAST_TS FROM STEP1 s1 WHERE s1.CST_TS IS NOT NULL\n"
      + "  UNION ALL\n"
      + "  SELECT MAX(s1.STF_TS) AS LAST_TS FROM STEP1 s1 WHERE s1.STF_TS IS NOT NULL\n"
      + ") x\n"
      + "FOR READ ONLY WITH UR";
    //@formatter:on

    //@formatter:off
    public static final String GET_FIRST_TS_AFTER_SAVEPOINT_QUERY =
          "WITH STEP1 AS (\n"
        + " SELECT\n"
        + "   plh.LST_UPD_TS AS PLH_TS,\n"
        + "   cst.LST_UPD_TS AS CST_TS,\n"
        + "   stf.LST_UPD_TS AS STF_TS,\n"
        + "   plh.IDENTIFIER\n"
        + " FROM      {h-schema}PLC_HM_T plh\n"
        + " LEFT JOIN {h-schema}CNTY_CST cst ON cst.IDENTIFIER = plh.FKCNTY_CST\n"
        + " LEFT JOIN {h-schema}STFPERST stf ON stf.IDENTIFIER = cst.FKSTFPERST\n"
        + " WHERE plh.LICENSR_CD <> 'CL'\n"
        + "   AND plh.PLC_FCLC   <> 1420\n"
        + "   AND plh.IBMSNAP_OPERATION IN ('I','U')\n"
        + "      AND (\n"
        + "          plh.IBMSNAP_LOGMARKER > :dateAfter\n"
        + "       OR cst.IBMSNAP_LOGMARKER > :dateAfter\n"
        + "       OR stf.IBMSNAP_LOGMARKER > :dateAfter\n"
        + "    )\n"
        + " ORDER BY plh.IBMSNAP_LOGMARKER, cst.IBMSNAP_LOGMARKER, stf.IBMSNAP_LOGMARKER, plh.IDENTIFIER\n"
        + " FETCH FIRST BATCH_SIZE ROWS ONLY\n"
        + ")\n"
        + "SELECT MAX(x.LAST_TS) FROM (\n"
        + "  SELECT MAX(s1.PLH_TS) AS LAST_TS FROM STEP1 s1\n"
        + "  UNION ALL\n"
        + "  SELECT MAX(s1.CST_TS) AS LAST_TS FROM STEP1 s1 WHERE s1.CST_TS IS NOT NULL\n"
        + "  UNION ALL\n"
        + "  SELECT MAX(s1.STF_TS) AS LAST_TS FROM STEP1 s1 WHERE s1.STF_TS IS NOT NULL\n"
        + ") x\n"
        + "FOR READ ONLY WITH UR";
    //@formatter:on

    //@formatter:off
    public static final String GET_NEXT_SAVEPOINT_QUERY =
          "WITH STEP1 AS (\n"
        + " SELECT x.IBMSNAP_LOGMARKER, 'X' AS IBMSNAP_OPERATION\n"
        + " FROM (\n"
        + "     SELECT MAX(plh.LST_UPD_TS) AS IBMSNAP_LOGMARKER\n"
        + "     FROM {h-schema}PLC_HM_T plh\n"
        + "     WHERE plh.LICENSR_CD <> 'CL'\n"
        + "       AND plh.PLC_FCLC   <> 1420\n"
        + "       AND plh.IBMSNAP_OPERATION IN ('I','U')\n"
        + "       AND plh.IBMSNAP_LOGMARKER > :dateAfter\n"
        + " ) x\n"
        + " UNION ALL\n"
        + " SELECT cst.LST_UPD_TS AS IBMSNAP_LOGMARKER, cst.IBMSNAP_OPERATION\n"
        + " FROM {h-schema}CNTY_CST cst\n"
        + " WHERE cst.IBMSNAP_LOGMARKER > :dateAfter\n"
        + " UNION ALL\n"
        + " SELECT stf.LST_UPD_TS AS IBMSNAP_LOGMARKER, stf.IBMSNAP_OPERATION\n"
        + " FROM      {h-schema}CNTY_CST cst\n"
        + " LEFT JOIN {h-schema}STFPERST stf ON stf.IDENTIFIER = cst.FKSTFPERST\n"
        + " WHERE stf.IBMSNAP_LOGMARKER > :dateAfter\n"
        + "), STEP2 AS (\n"
        + "  SELECT s1.IBMSNAP_LOGMARKER, s1.IBMSNAP_OPERATION\n"
        + "  FROM STEP1 s1\n"
        + "  WHERE s1.IBMSNAP_OPERATION IN ('X','I','U')\n"
        + "    AND s1.IBMSNAP_LOGMARKER > :dateAfter\n"
        + ")\n"
        + "SELECT MAX(s2.IBMSNAP_LOGMARKER) AS MAX_LST_UPD_TS\n"
        + "FROM STEP2 s2\n"
        + "FOR READ ONLY WITH UR";
    //@formatter:on

    private InitialMode() {
      // utility class
    }
  }

  public static class IncrementalMode {

    //@formatter:off
    public static final String GET_IDENTIFIERS_AFTER_TIMESTAMP_QUERY =
          "SELECT plh.IDENTIFIER, plh.IBMSNAP_OPERATION, plh.IBMSNAP_LOGMARKER\n"
        + "FROM      {h-schema}PLC_HM_T plh\n"
        + "LEFT JOIN {h-schema}CNTY_CST cst ON cst.IDENTIFIER = plh.FKCNTY_CST\n"
        + "LEFT JOIN {h-schema}STFPERST stf ON stf.IDENTIFIER = cst.FKSTFPERST\n"
        + "WHERE plh.LICENSR_CD <> 'CL'\n"
        + "  AND plh.PLC_FCLC   <> 1420\n"
        + "  AND (\n"
        + "      plh.IBMSNAP_LOGMARKER > :dateAfter\n"
        + "   OR cst.IBMSNAP_LOGMARKER > :dateAfter\n"
        + "   OR stf.IBMSNAP_LOGMARKER > :dateAfter\n"
        + ")\n"
        + "ORDER BY plh.IBMSNAP_LOGMARKER, cst.IBMSNAP_LOGMARKER, stf.IBMSNAP_LOGMARKER, plh.IDENTIFIER\n"
        + "FETCH FIRST BATCH_SIZE ROWS ONLY\n"
        + "FOR READ ONLY WITH UR";
    //@formatter:on

    //@formatter:off
    public static final String GET_IDENTIFIERS_BETWEEN_TIMESTAMPS_QUERY =
        "WITH STEP1 AS (\n"
      + " SELECT\n"
      + "   plh.IBMSNAP_LOGMARKER AS PLH_TS,\n"
      + "   cst.IBMSNAP_LOGMARKER AS CST_TS,\n"
      + "   stf.IBMSNAP_LOGMARKER AS STF_TS,\n"
      + "   plh.IDENTIFIER\n"
      + " FROM      {h-schema}PLC_HM_T plh\n"
      + " LEFT JOIN {h-schema}CNTY_CST cst ON cst.IDENTIFIER = plh.FKCNTY_CST\n"
      + " LEFT JOIN {h-schema}STFPERST stf ON stf.IDENTIFIER = cst.FKSTFPERST\n"
      + " WHERE plh.LICENSR_CD <> 'CL'\n"
      + "   AND plh.PLC_FCLC   <> 1420\n"
      + "      AND (\n"
      + "          plh.IBMSNAP_LOGMARKER BETWEEN :dateBefore AND :dateAfter\n"
      + "       OR cst.IBMSNAP_LOGMARKER BETWEEN :dateBefore AND :dateAfter\n"
      + "       OR stf.IBMSNAP_LOGMARKER BETWEEN :dateBefore AND :dateAfter\n"
      + "    )\n"
      + " ORDER BY plh.IBMSNAP_LOGMARKER, cst.IBMSNAP_LOGMARKER, stf.IBMSNAP_LOGMARKER, plh.IDENTIFIER\n"
      + " FETCH FIRST BATCH_SIZE ROWS ONLY\n"
      + ")\n"
      + "SELECT MAX(x.LAST_TS) FROM (\n"
      + "  SELECT MAX(s1.PLH_TS) AS LAST_TS FROM STEP1 s1\n"
      + "  UNION ALL\n"
      + "  SELECT MAX(s1.CST_TS) AS LAST_TS FROM STEP1 s1 WHERE s1.CST_TS IS NOT NULL\n"
      + "  UNION ALL\n"
      + "  SELECT MAX(s1.STF_TS) AS LAST_TS FROM STEP1 s1 WHERE s1.STF_TS IS NOT NULL\n"
      + ") x\n"
      + "FOR READ ONLY WITH UR";
    //@formatter:on

    //@formatter:off
    public static final String GET_FIRST_TS_AFTER_SAVEPOINT_QUERY =
          "WITH STEP1 AS (\n"
        + " SELECT\n"
        + "   plh.IBMSNAP_LOGMARKER AS PLH_TS,\n"
        + "   cst.IBMSNAP_LOGMARKER AS CST_TS,\n"
        + "   stf.IBMSNAP_LOGMARKER AS STF_TS,\n"
        + "   plh.IDENTIFIER\n"
        + " FROM      {h-schema}PLC_HM_T plh\n"
        + " LEFT JOIN {h-schema}CNTY_CST cst ON cst.IDENTIFIER = plh.FKCNTY_CST\n"
        + " LEFT JOIN {h-schema}STFPERST stf ON stf.IDENTIFIER = cst.FKSTFPERST\n"
        + " WHERE plh.LICENSR_CD <> 'CL'\n"
        + "   AND plh.PLC_FCLC   <> 1420\n"
        + "      AND (\n"
        + "          plh.IBMSNAP_LOGMARKER > :dateAfter\n"
        + "       OR cst.IBMSNAP_LOGMARKER > :dateAfter\n"
        + "       OR stf.IBMSNAP_LOGMARKER > :dateAfter\n"
        + "    )\n"
        + " ORDER BY plh.IBMSNAP_LOGMARKER, cst.IBMSNAP_LOGMARKER, stf.IBMSNAP_LOGMARKER, plh.IDENTIFIER\n"
        + " FETCH FIRST BATCH_SIZE ROWS ONLY\n"
        + ")\n"
        + "SELECT MAX(x.LAST_TS) FROM (\n"
        + "  SELECT MAX(s1.PLH_TS) AS LAST_TS FROM STEP1 s1\n"
        + "  UNION ALL\n"
        + "  SELECT MAX(s1.CST_TS) AS LAST_TS FROM STEP1 s1 WHERE s1.CST_TS IS NOT NULL\n"
        + "  UNION ALL\n"
        + "  SELECT MAX(s1.STF_TS) AS LAST_TS FROM STEP1 s1 WHERE s1.STF_TS IS NOT NULL\n"
        + ") x\n"
        + "FOR READ ONLY WITH UR";
    //@formatter:on

    //@formatter:off
    public static final String GET_NEXT_SAVEPOINT_QUERY =
          "WITH STEP1 AS (\n"
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
        + "  SELECT s1.IBMSNAP_LOGMARKER, s1.IBMSNAP_OPERATION\n"
        + "  FROM STEP1 s1\n"
        + "  WHERE s1.IBMSNAP_OPERATION IN ('X','I','U','D')\n"
        + "    AND s1.IBMSNAP_LOGMARKER > :dateAfter\n"
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
