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
        name = StaffPerson.GET_MAX_LAST_UPDATED_TIME,
        query = StaffPerson.GET_MAX_LAST_UPDATED_TIME_QUERY
    )
})
@Entity
@Table(name = "STFPERST")
public class StaffPerson extends CmsPersistentObject {

  private static final long serialVersionUID = 6483188919867162459L;

  static final String GET_MAX_LAST_UPDATED_TIME_QUERY = "select max(lastUpdatedTime) from StaffPerson";
  public static final String GET_MAX_LAST_UPDATED_TIME = "StaffPerson.getMaxLastUpdatedTime";

  @Id
  @Column(
      name = "IDENTIFIER"
  )
  private String id;

  @Column(
      name = "FKCWS_OFFT"
  )
  private String cwsOffice;


  @Override
  public Serializable getPrimaryKey() {
    return this.getId();
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getCwsOffice() {
    return cwsOffice;
  }

  public void setCwsOffice(String cwsOffice) {
    this.cwsOffice = cwsOffice;
  }
}
