package gov.ca.cwds.jobs.cals.facility.cws.entity;

import com.google.inject.Inject;
import gov.ca.cwds.cals.service.CwsFacilityService;
import gov.ca.cwds.cals.service.dto.FacilityDto;
import gov.ca.cwds.jobs.cals.facility.AbstractChangedFacilityService;
import gov.ca.cwds.jobs.cals.facility.ChangedFacilityDto;
import gov.ca.cwds.jobs.common.entity.ChangedEntityService;
import gov.ca.cwds.jobs.common.identifier.ChangedEntityIdentifier;

/**
 * @author CWDS TPT-2
 */

public class CwsChangedFacilityService extends AbstractChangedFacilityService implements
    ChangedEntityService<ChangedFacilityDto> {

  @Inject
  private CwsFacilityService cwsFacilityService;

  @Override
  protected FacilityDto loadEntityById(ChangedEntityIdentifier identifier) {
    return cwsFacilityService.loadFacilityFromCwsCms(identifier.getId());
  }
}
