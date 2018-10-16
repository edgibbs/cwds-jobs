package gov.ca.cwds.jobs.common.entity;

import gov.ca.cwds.data.persistence.PersistentObject;
import java.io.Serializable;
import java.time.LocalDateTime;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 * Created by Alexander Serbin on 6/21/2018.
 */
@Entity
@Table(
    name = "TEST_ENTITY"
)

public class TestEntity implements PersistentObject {

  @Id
  private String id;

  @Column(
      name = "TIMESTAMP",
      nullable = false
  )
  private LocalDateTime timestamp;

  public TestEntity() {}

  public TestEntity(String id, LocalDateTime timestamp) {
    this.id = id;
    this.timestamp = timestamp;
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public LocalDateTime getTimestamp() {
    return timestamp;
  }

  public void setTimestamp(LocalDateTime timestamp) {
    this.timestamp = timestamp;
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
  public Serializable getPrimaryKey() {
    return id;
  }
}
