package gov.ca.cwds.jobs.cals.facility.lisfas.entity;

import com.google.inject.Inject;

import gov.ca.cwds.cals.service.LisFacilityService;
import gov.ca.cwds.cals.service.dto.FacilityDto;
import gov.ca.cwds.jobs.cals.facility.AbstractChangedFacilityService;
import gov.ca.cwds.jobs.cals.facility.ChangedFacilityDto;
import gov.ca.cwds.jobs.common.entity.ChangedEntityService;
import gov.ca.cwds.jobs.common.identifier.ChangedEntityIdentifier;

/**
 * @author CWDS TPT-2
 */
public class LisChangedFacilityService extends AbstractChangedFacilityService
    implements ChangedEntityService<ChangedFacilityDto> {

  @Inject
  private LisFacilityService lisFacilityService;

  @Override
  protected FacilityDto loadEntityById(ChangedEntityIdentifier identifier) {
    return lisFacilityService.loadFacilityFromLis(identifier.getId()).orElse(null);
  }

}
