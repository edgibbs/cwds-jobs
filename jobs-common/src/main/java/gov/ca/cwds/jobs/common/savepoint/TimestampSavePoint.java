package gov.ca.cwds.jobs.common.savepoint;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 * Created by Alexander Serbin on 6/19/2018.
 */
public abstract class TimestampSavePoint<T>
    implements SavePoint, Comparable<TimestampSavePoint<T>> {

  private T timestamp;

  public TimestampSavePoint() {}

  public TimestampSavePoint(T timestamp) {
    this.timestamp = timestamp;
  }

  public T getTimestamp() {
    return timestamp;
  }

  public void setTimestamp(T timestamp) {
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
  public String toString() {
    return timestamp != null ? timestamp.toString() : "Empty timestamp";
  }

}
