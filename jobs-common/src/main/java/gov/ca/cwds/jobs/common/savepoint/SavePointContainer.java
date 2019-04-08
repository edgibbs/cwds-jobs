package gov.ca.cwds.jobs.common.savepoint;

import gov.ca.cwds.jobs.common.mode.JobMode;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;

/**
 * Container to load and store job save point.
 * Created by Alexander Serbin on 6/18/2018.
 */
public class SavePointContainer<S extends SavePoint> {

  private JobMode jobMode;

  private S savePoint;

  public JobMode getJobMode() {
    return jobMode;
  }

  public void setJobMode(JobMode jobMode) {
    this.jobMode = jobMode;
  }

  public S getSavePoint() {
    return savePoint;
  }

  public void setSavePoint(S savePoint) {
    this.savePoint = savePoint;
  }

  public boolean equals(Object o) {
    return EqualsBuilder.reflectionEquals(this, o);
  }

  public int hashCode() {
    return HashCodeBuilder.reflectionHashCode(this);
  }

  @Override
  public String toString() {
    return ReflectionToStringBuilder.toString(this);
  }
}
