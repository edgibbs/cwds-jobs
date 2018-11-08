package gov.ca.cwds.jobs.cap.users.entity;

import gov.ca.cwds.data.legacy.cms.CmsPersistentObject;
import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;


@NamedQueries({
    @NamedQuery(
        name = CwsOffice.GET_MAX_LAST_UPDATED_TIME,
        query = CwsOffice.GET_MAX_LAST_UPDATED_TIME_QUERY
    )
})
@Entity
@Table(name = "CWS_OFFT")
public class CwsOffice extends CmsPersistentObject {

  private static final long serialVersionUID = 2458409600546521184L;

  static final String GET_MAX_LAST_UPDATED_TIME_QUERY = "select max(lastUpdatedTime) from CwsOffice";
  public static final String GET_MAX_LAST_UPDATED_TIME = "CwsOffice.getMaxLastUpdatedTime";

  @Id
  @Column(name = "IDENTIFIER")
  private String officeId;


  @Override
  public Serializable getPrimaryKey() {
    return this.getOfficeId();
  }

  public String getOfficeId() {
    return officeId;
  }

  public void setOfficeId(String officeId) {
    this.officeId = officeId;
  }

}
