package gov.ca.cwds.jobs.cals.facility.lisfas.savepoint;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import gov.ca.cwds.jobs.common.savepoint.SavePoint;

/**
 * Created by Alexander Serbin on 6/27/2018.
 */
public class LicenseNumberSavePoint implements SavePoint, Comparable<LicenseNumberSavePoint> {

  private int licenseNumber;

  public LicenseNumberSavePoint(int licenseNumber) {
    this.licenseNumber = licenseNumber;
  }

  public int getLicenseNumber() {
    return licenseNumber;
  }

  public void setLicenseNumber(int licenseNumber) {
    this.licenseNumber = licenseNumber;
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
  public int compareTo(LicenseNumberSavePoint o) {
    return Integer.compare(licenseNumber, o.licenseNumber);
  }

}
