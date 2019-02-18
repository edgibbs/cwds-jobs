package gov.ca.cwds.jobs.utils;

import static io.dropwizard.testing.FixtureHelpers.fixture;
import static org.skyscreamer.jsonassert.JSONAssert.assertEquals;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.dropwizard.jackson.Jackson;
import org.json.JSONException;
import org.skyscreamer.jsonassert.JSONCompareMode;

/**
 * Created by Alexander Serbin on 3/28/2018.
 */
public final class AssertEntityHelper {

  private AssertEntityHelper() {
  }

  public static <T> void assertEntity(String fixturePath, T entity)
      throws JsonProcessingException, JSONException {
    assertEquals(
        fixture(fixturePath),
        Jackson.newObjectMapper().writeValueAsString(entity),
        JSONCompareMode.STRICT);
  }

}
