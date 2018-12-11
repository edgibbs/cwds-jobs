package gov.ca.cwds.jobs.cals.facility;

import static org.junit.Assert.assertEquals;

import gov.ca.cwds.cals.service.dto.FacilityDto;
import gov.ca.cwds.jobs.common.RecordChangeOperation;
import org.junit.Test;

public class ChangedFacilityDtoTest {

  @Test
  public void equals() {
    FacilityDto facilityDto = new FacilityDto();
    facilityDto.setName("Some Facility");
    ChangedFacilityDto changedFacilityDTO1 = new ChangedFacilityDto(facilityDto,
        RecordChangeOperation.U);

    ChangedFacilityDto changedFacilityDTO2 = new ChangedFacilityDto(facilityDto,
        RecordChangeOperation.U);

    assertEquals(changedFacilityDTO1, changedFacilityDTO2);
  }

  @Test
  public void getDTO() {
    FacilityDto facilityDto = new FacilityDto();
    facilityDto.setId("FacilityId");
    ChangedFacilityDto changedFacilityDTO = new ChangedFacilityDto(facilityDto,
        RecordChangeOperation.U);
    assertEquals(facilityDto, changedFacilityDTO.getDTO());
  }

  @Test
  public void getId() {
    FacilityDto facilityDto = new FacilityDto();
    String facilityId = "FacilityId";
    facilityDto.setId(facilityId);
    ChangedFacilityDto changedFacilityDto = new ChangedFacilityDto(facilityDto,
        RecordChangeOperation.U);
    assertEquals(facilityId, changedFacilityDto.getId());
  }
}