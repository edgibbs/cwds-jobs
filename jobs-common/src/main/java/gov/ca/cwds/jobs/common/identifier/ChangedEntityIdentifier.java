package gov.ca.cwds.jobs.common.identifier;

import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.Transient;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import gov.ca.cwds.data.persistence.PersistentObject;
import gov.ca.cwds.jobs.common.RecordChangeOperation;
import gov.ca.cwds.jobs.common.savepoint.SavePoint;

/**
 * Created by Alexander Serbin on 3/5/2018.
 */
@MappedSuperclass
public abstract class ChangedEntityIdentifier<S extends SavePoint>
    implements Comparable<ChangedEntityIdentifier<S>>, PersistentObject {

  private static final long serialVersionUID = 1L;

  @Id
  private String id;

  private RecordChangeOperation recordChangeOperation;

  @SuppressWarnings("squid:S1948") // suppressing sonarqube warning about non-transient field
  @Transient
  private S savePoint;

  protected ChangedEntityIdentifier() {}

  public ChangedEntityIdentifier(String id, S savePoint) {
    this.id = id;
    this.savePoint = savePoint;
  }

  public ChangedEntityIdentifier(String id, RecordChangeOperation recordChangeOperation,
      S savePoint) {
    this(id, savePoint);
    this.recordChangeOperation = recordChangeOperation;
  }

  public String getId() {
    return id;
  }

  public Integer getIntId() {
    return Integer.valueOf(id);
  }

  public void setId(String id) {
    this.id = id;
  }

  public RecordChangeOperation getRecordChangeOperation() {
    return recordChangeOperation;
  }

  public void setRecordChangeOperation(RecordChangeOperation recordChangeOperation) {
    this.recordChangeOperation = recordChangeOperation;
  }

  public S getSavePoint() {
    return savePoint;
  }

  public void setSavePoint(S savePoint) {
    this.savePoint = savePoint;
  }

  @Override
  public boolean equals(Object o) {
    return EqualsBuilder.reflectionEquals(this, o);
  }

  @Override
  public int hashCode() {
    return HashCodeBuilder.reflectionHashCode(this);
  }

  @Override
  public String toString() {
    return "id=" + id + ", recordChangeOperation=" + recordChangeOperation + ", savePoint="
        + savePoint + "\n";
  }

}
