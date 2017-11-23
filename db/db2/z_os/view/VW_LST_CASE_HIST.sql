-- SET CURRENT SCHEMA = 'CWSRS1' ;

DROP VIEW VW_LST_CASE_HIST;

CREATE VIEW VW_LST_CASE_HIST (
  CASE_ID,
  START_DATE,
  END_DATE,
  COUNTY,
  SERVICE_COMP,
  LIMITED_ACCESS_CODE,
  LIMITED_ACCESS_DATE,
  LIMITED_ACCESS_DESCRIPTION,
  LIMITED_ACCESS_GOVERNMENT_ENT,
  CASE_LAST_UPDATED,
  FOCUS_CHLD_FIRST_NM,
  FOCUS_CHLD_LAST_NM,
  FOCUS_CHILD_ID,
  FOCUS_CHILD_SENSITIVITY_IND,
  FOCUS_CHILD_LAST_UPDATED,
  PARENT_FIRST_NM,
  PARENT_LAST_NM,
  PARENT_RELATIONSHIP,
  PARENT_ID,
  PARENT_SENSITIVITY_IND,
  PARENT_LAST_UPDATED,
  PARENT_SOURCE_TABLE,
  WORKER_FIRST_NM,
  WORKER_LAST_NM,
  WORKER_ID,
  WORKER_LAST_UPDATED,  
  CAS_IBMSNAP_LOGMARKER,
  CAS_IBMSNAP_OPERATION,
  CLC_IBMSNAP_LOGMARKER,
  CLC_IBMSNAP_OPERATION,
  CLP_IBMSNAP_LOGMARKER,
  CLP_IBMSNAP_OPERATION,
  STF_IBMSNAP_LOGMARKER,
  STF_IBMSNAP_OPERATION,
  LAST_CHG
) AS
SELECT
	CAS.IDENTIFIER AS CASE_ID,
	CAS.START_DT   AS START_DATE,
	CAS.END_DT     AS END_DATE,
	CAS.GVR_ENTC   AS COUNTY,
	CAS.SRV_CMPC   AS SERVICE_COMP,
	CAS.LMT_ACSSCD AS LIMITED_ACCESS_CODE,
	CAS.LMT_ACS_DT AS LIMITED_ACCESS_DATE,
	CAS.LMT_ACSDSC AS LIMITED_ACCESS_DESCRIPTION,
	CAS.L_GVR_ENTC AS LIMITED_ACCESS_GOVERNMENT_ENT,
	CAS.LST_UPD_TS AS CASE_LAST_UPDATED,
	CLC.COM_FST_NM AS FOCUS_CHLD_FIRST_NM,
	CLC.COM_LST_NM AS FOCUS_CHLD_LAST_NM,
	CLC.IDENTIFIER AS FOCUS_CHILD_ID,
	CLC.SENSTV_IND AS FOCUS_CHILD_SENSITIVITY_IND,
	CLC.LST_UPD_TS AS FOCUS_CHILD_LAST_UPDATED,  
	CLP.COM_FST_NM AS PARENT_FIRST_NM,
	CLP.COM_LST_NM AS PARENT_LAST_NM,
	CLR.CLNTRELC   AS PARENT_RELATIONSHIP,
	CLP.IDENTIFIER AS PARENT_ID,
	CLP.SENSTV_IND AS PARENT_SENSITIVITY_IND,
	CLP.LST_UPD_TS AS PARENT_LAST_UPDATED,  
	'CLIENT_T'     AS PARENT_SOURCE_TABLE,
	STF.FIRST_NM   AS WORKER_FIRST_NM,
	STF.LAST_NM    AS WORKER_LAST_NM,
	STF.IDENTIFIER AS WORKER_ID,
	STF.LST_UPD_TS AS WORKER_LAST_UPDATED,
	CAS.IBMSNAP_LOGMARKER AS CAS_IBMSNAP_LOGMARKER,
	CAS.IBMSNAP_OPERATION AS CAS_IBMSNAP_OPERATION,
	CLC.IBMSNAP_LOGMARKER AS CLC_IBMSNAP_LOGMARKER,
	CLC.IBMSNAP_OPERATION AS CLC_IBMSNAP_OPERATION,
	CLP.IBMSNAP_LOGMARKER AS CLP_IBMSNAP_LOGMARKER,
	CLP.IBMSNAP_OPERATION AS CLP_IBMSNAP_OPERATION,
	STF.IBMSNAP_LOGMARKER AS STF_IBMSNAP_LOGMARKER,
	STF.IBMSNAP_OPERATION AS STF_IBMSNAP_OPERATION,
	MAX(
		NVL(CAS.IBMSNAP_LOGMARKER, TIMESTAMP('2008-09-30 11:54:40')),
		NVL(CLC.IBMSNAP_LOGMARKER, TIMESTAMP('2008-09-30 11:54:40')),
		NVL(CLP.IBMSNAP_LOGMARKER, TIMESTAMP('2008-09-30 11:54:40'))   
	) LAST_CHG
FROM CASE_T CAS
LEFT JOIN CWSRS1.CHLD_CLT CCL ON CCL.FKCLIENT_T = CAS.FKCHLD_CLT
LEFT JOIN CWSRS1.CLIENT_T CLC ON CLC.IDENTIFIER = CCL.FKCLIENT_T
LEFT JOIN CLIENT_T CLC ON CLC.IDENTIFIER = CCL.FKCLIENT_T
LEFT JOIN CLN_RELT CLR ON CLR.FKCLIENT_0 = CCL.FKCLIENT_T AND ((CLR.CLNTRELC BETWEEN 187 and 214) OR
(CLR.CLNTRELC BETWEEN 245 and 254) OR (CLR.CLNTRELC BETWEEN 282 and 294) OR (CLR.CLNTRELC IN (272, 273, 5620, 6360, 6361)))
LEFT JOIN CLIENT_T CLP ON CLP.IDENTIFIER = CLR.FKCLIENT_T
LEFT JOIN STFPERST STF ON STF.IDENTIFIER = CAS.FKSTFPERST
WHERE CAS.IDENTIFIER IN (
	SELECT GT.IDENTIFIER FROM GT_ID GT
)
UNION
SELECT
  CAS.IDENTIFIER AS CASE_ID,
  CAS.START_DT   AS START_DATE,
  CAS.END_DT     AS END_DATE,
  CAS.GVR_ENTC   AS COUNTY,
  CAS.SRV_CMPC   AS SERVICE_COMP,
  CAS.LMT_ACSSCD AS LIMITED_ACCESS_CODE,
  CAS.LMT_ACS_DT AS LIMITED_ACCESS_DATE,
  CAS.LMT_ACSDSC AS LIMITED_ACCESS_DESCRIPTION,
  CAS.L_GVR_ENTC AS LIMITED_ACCESS_GOVERNMENT_ENT,
  CAS.LST_UPD_TS AS CASE_LAST_UPDATED,
  CLC.COM_FST_NM AS FOCUS_CHLD_FIRST_NM,
  CLC.COM_LST_NM AS FOCUS_CHLD_LAST_NM,
  CLC.IDENTIFIER AS FOCUS_CHILD_ID,
  CLC.SENSTV_IND AS FOCUS_CHILD_SENSITIVITY_IND,
  CLC.LST_UPD_TS AS FOCUS_CHILD_LAST_UPDATED,
  CLP.COM_FST_NM AS PARENT_FIRST_NM,
  CLP.COM_LST_NM AS PARENT_LAST_NM,
  CLR.CLNTRELC   AS PARENT_RELATIONSHIP,
  CLP.IDENTIFIER AS PARENT_ID,
  CLP.SENSTV_IND AS PARENT_SENSITIVITY_IND,
  CLP.LST_UPD_TS AS PARENT_LAST_UPDATED,
  'CLIENT_T'     AS PARENT_SOURCE_TABLE,
  STF.FIRST_NM   AS WORKER_FIRST_NM,
  STF.LAST_NM    AS WORKER_LAST_NM,
  STF.IDENTIFIER AS WORKER_ID,
  STF.LST_UPD_TS AS WORKER_LAST_UPDATED,  
  CAS.IBMSNAP_LOGMARKER AS CAS_IBMSNAP_LOGMARKER,
  CAS.IBMSNAP_OPERATION AS CAS_IBMSNAP_OPERATION,
  CLC.IBMSNAP_LOGMARKER AS CLC_IBMSNAP_LOGMARKER,
  CLC.IBMSNAP_OPERATION AS CLC_IBMSNAP_OPERATION,
  CLP.IBMSNAP_LOGMARKER AS CLP_IBMSNAP_LOGMARKER,
  CLP.IBMSNAP_OPERATION AS CLP_IBMSNAP_OPERATION,
  STF.IBMSNAP_LOGMARKER AS STF_IBMSNAP_LOGMARKER,
  STF.IBMSNAP_OPERATION AS STF_IBMSNAP_OPERATION,
  MAX (
    NVL(CAS.IBMSNAP_LOGMARKER, TIMESTAMP('2008-09-30 11:54:40')),
    NVL(CLC.IBMSNAP_LOGMARKER, TIMESTAMP('2008-09-30 11:54:40')),
    NVL(CLP.IBMSNAP_LOGMARKER, TIMESTAMP('2008-09-30 11:54:40'))    
  ) LAST_CHG
FROM CASE_T CAS
LEFT JOIN CHLD_CLT CCL ON CCL.FKCLIENT_T = CAS.FKCHLD_CLT
LEFT JOIN CLIENT_T CLC ON CLC.IDENTIFIER = CCL.FKCLIENT_T
LEFT JOIN CLN_RELT CLR ON CLR.FKCLIENT_T = CCL.FKCLIENT_T AND ((CLR.CLNTRELC BETWEEN 187 and 214) OR
(CLR.CLNTRELC BETWEEN 245 and 254) OR (CLR.CLNTRELC BETWEEN 282 and 294) OR (CLR.CLNTRELC IN (272, 273, 5620, 6360, 6361)))
LEFT JOIN CLIENT_T CLP ON CLP.IDENTIFIER = CLR.FKCLIENT_0
LEFT JOIN STFPERST STF ON STF.IDENTIFIER = CAS.FKSTFPERST
WHERE CAS.IDENTIFIER IN (
	SELECT GT.IDENTIFIER FROM GT_ID GT
);

COMMIT;
