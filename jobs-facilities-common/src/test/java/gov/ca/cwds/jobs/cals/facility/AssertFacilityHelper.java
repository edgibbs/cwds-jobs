package gov.ca.cwds.jobs.cals.facility;

import static gov.ca.cwds.jobs.utils.AssertEntityHelper.assertEntity;
import static org.junit.Assert.assertTrue;

import com.fasterxml.jackson.core.JsonProcessingException;
import java.util.Optional;
import org.json.JSONException;

/**
 * Created by Alexander Serbin on 3/28/2018.
 */
public final class AssertFacilityHelper {

  private AssertFacilityHelper() {
  }

  public static void assertFacility(String fixturePath, String facilityId)
      throws JSONException, JsonProcessingException {
    assertEntity(fixturePath, getFacilityById(facilityId));
  }

  private static ChangedFacilityDto getFacilityById(String facilityId) {
    Optional<ChangedFacilityDto> optional = FacilityTestWriter.getItems().stream()
        .filter(o -> facilityId.equals(((ChangedFacilityDto) o).getId())).findAny();
    assertTrue(optional.isPresent());
    return optional.orElse(null);
  }

}
