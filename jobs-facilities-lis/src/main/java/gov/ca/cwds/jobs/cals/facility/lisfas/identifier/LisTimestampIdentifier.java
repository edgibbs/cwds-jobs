package gov.ca.cwds.jobs.cals.facility.lisfas.identifier;

import static gov.ca.cwds.jobs.cals.facility.lisfas.identifier.LicenseNumberIdentifier.SHARED_PART;

import java.math.BigInteger;

import javax.persistence.Entity;

import org.hibernate.annotations.NamedQuery;

import gov.ca.cwds.jobs.cals.facility.lisfas.savepoint.LisTimestampSavePoint;
import gov.ca.cwds.jobs.common.RecordChangeOperation;
import gov.ca.cwds.jobs.common.identifier.TimestampIdentifier;

/**
 * Created by Alexander Serbin on 6/27/2018.
 */
@NamedQuery(name = LisTimestampIdentifier.LIS_INCREMENTAL_LOAD_QUERY_NAME,
    query = LisTimestampIdentifier.INCREMENTAL_LOAD_SQL)
@Entity
public class LisTimestampIdentifier extends TimestampIdentifier<BigInteger> {

  private static final long serialVersionUID = 1L;

  public static final String INCREMENTAL_LOAD_SQL =
      "select new LisTimestampIdentifier(facNbr, timestamp) " + SHARED_PART
          + " and timestamp > :dateAfter " + "order by timestamp, facNbr";

  public static final String LIS_INCREMENTAL_LOAD_QUERY_NAME =
      "RecordChange.lisIncrementalLoadQuery";

  public LisTimestampIdentifier(int id, BigInteger savePoint) {
    super(String.valueOf(id), RecordChangeOperation.U, new LisTimestampSavePoint(savePoint));
  }

}
