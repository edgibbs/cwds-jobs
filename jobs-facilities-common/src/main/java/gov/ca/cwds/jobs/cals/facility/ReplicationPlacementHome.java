package gov.ca.cwds.jobs.cals.facility;

import gov.ca.cwds.data.legacy.cms.entity.BasePlacementHome;
import gov.ca.cwds.data.legacy.cms.entity.CountyLicenseCase;
import gov.ca.cwds.jobs.common.RecordChangeOperation;
import java.time.LocalDateTime;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Table;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 * Created by Alexander Serbin on 4/4/2018.
 */
@Entity
@Table(
    name = "PLC_HM_T"
)
public class ReplicationPlacementHome extends BasePlacementHome {

  @Column(
      name = "IBMSNAP_LOGMARKER",
      nullable = false
  )
  private LocalDateTime replicationLastUpdated;

  @Enumerated(EnumType.STRING)
  @Column(name = "IBMSNAP_OPERATION", updatable = false)
  private RecordChangeOperation recordChangeOperation;

  public LocalDateTime getReplicationLastUpdated() {
    return replicationLastUpdated;
  }

  public void setReplicationLastUpdated(LocalDateTime replicationLastUpdated) {
    this.replicationLastUpdated = replicationLastUpdated;
  }

  public RecordChangeOperation getRecordChangeOperation() {
    return recordChangeOperation;
  }

  public void setRecordChangeOperation(RecordChangeOperation recordChangeOperation) {
    this.recordChangeOperation = recordChangeOperation;
  }

  @Override
  public CountyLicenseCase getCountyLicenseCase() {
    throw new UnsupportedOperationException();
  }

  @Override
  public boolean equals(Object o) {
    return EqualsBuilder.reflectionEquals(this, o);
  }

  @Override
  public int hashCode() {
    return HashCodeBuilder.reflectionHashCode(this);
  }

}
