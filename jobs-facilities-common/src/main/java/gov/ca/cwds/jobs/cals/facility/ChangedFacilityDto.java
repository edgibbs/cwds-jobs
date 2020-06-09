package gov.ca.cwds.jobs.cals.facility;

import java.io.Serializable;
import java.util.Objects;

import gov.ca.cwds.cals.service.dto.FacilityDto;
import gov.ca.cwds.dto.BaseDTO;
import gov.ca.cwds.jobs.common.ChangedDTO;
import gov.ca.cwds.jobs.common.RecordChangeOperation;

/**
 * @author CWDS TPT-2
 */
public class ChangedFacilityDto extends BaseDTO implements ChangedDTO<FacilityDto>, Serializable {

  private static final long serialVersionUID = 1L;

  private FacilityDto facilityDto;

  private RecordChangeOperation recordChangeOperation;

  public ChangedFacilityDto(FacilityDto facilityDto, RecordChangeOperation recordChangeOperation) {
    this.facilityDto = facilityDto;
    this.recordChangeOperation = recordChangeOperation;
  }

  public ChangedFacilityDto() {
    // default constructor
  }

  @Override
  public RecordChangeOperation getRecordChangeOperation() {
    return recordChangeOperation;
  }

  @Override
  public FacilityDto getDTO() {
    return facilityDto;
  }

  @Override
  public String getId() {
    return facilityDto.getId();
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ChangedFacilityDto that = (ChangedFacilityDto) o;
    return recordChangeOperation == that.recordChangeOperation
        && Objects.equals(facilityDto, that.facilityDto);
  }

  @Override
  public int hashCode() {
    return Objects.hash(facilityDto, recordChangeOperation);
  }

}
