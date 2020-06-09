package gov.ca.cwds.jobs.cals.facility.lisfas.identifier;

import java.io.Serializable;

import javax.persistence.Entity;

import org.hibernate.annotations.NamedQuery;

import gov.ca.cwds.jobs.cals.facility.lisfas.savepoint.LicenseNumberSavePoint;
import gov.ca.cwds.jobs.common.RecordChangeOperation;
import gov.ca.cwds.jobs.common.identifier.ChangedEntityIdentifier;

/**
 * Created by Alexander Serbin on 6/27/2018.
 */
@NamedQuery(name = LicenseNumberIdentifier.LIS_INITIAL_LOAD_QUERY_NAME,
    query = LicenseNumberIdentifier.INITIAL_LOAD_SQL)
@NamedQuery(name = LicenseNumberIdentifier.LIS_GET_MAX_TIMESTAMP_QUERY_NAME,
    query = LicenseNumberIdentifier.LIS_GET_MAX_TIMESTAMP_SQL)
@Entity
public class LicenseNumberIdentifier extends ChangedEntityIdentifier<LicenseNumberSavePoint> {

  private static final long serialVersionUID = 1L;

  static final String SHARED_PART =
      " from LisFacFile where facilityTypeCode in (400, 403, 430, 431, 433, 710, 711, 720, 721, "
          + "722, 726, 728, 729, 730, 731, 732, 733) ";

  public static final String LIS_GET_MAX_TIMESTAMP_SQL =
      "select max(timestamp) as m " + SHARED_PART;

  public static final String INITIAL_LOAD_SQL = "select new LicenseNumberIdentifier(facNbr) "
      + SHARED_PART + " and facNbr > :facNbr order by facNbr";

  public static final String LIS_GET_MAX_TIMESTAMP_QUERY_NAME =
      "LicenseNumberIdentifier.lisGetMaxTimestamp";
  public static final String LIS_INITIAL_LOAD_QUERY_NAME =
      "LicenseNumberIdentifier.lisInitialLoadQuery";

  public LicenseNumberIdentifier(int id) {
    super(String.valueOf(id), RecordChangeOperation.U, new LicenseNumberSavePoint(id));
  }

  @Override
  public int compareTo(ChangedEntityIdentifier<LicenseNumberSavePoint> o) {
    return getSavePoint().compareTo(o.getSavePoint());
  }

  @Override
  public Serializable getPrimaryKey() {
    return getId();
  }

}
