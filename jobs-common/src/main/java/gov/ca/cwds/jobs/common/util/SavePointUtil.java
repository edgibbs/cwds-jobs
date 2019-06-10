package gov.ca.cwds.jobs.common.util;

import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Alexander Serbin on 6/6/2019
 */
public final class SavePointUtil {

  private SavePointUtil() {
  }

  public static String extractProperty(Path savePointFile, String propertyName) {
    try (Reader reader = Files.newBufferedReader(savePointFile)) {
      String savePointContainer = IOUtils.toString(reader);
      JSONObject jsonObject = new JSONObject(savePointContainer);
      String propertyValue = jsonObject.getString(propertyName);
      if (StringUtils.isNoneEmpty(propertyValue)) {
        return propertyValue;
      }
      throw new IllegalStateException(
          String.format("Requested property %s is unexpectedly empty ", propertyName));
    } catch (IOException | JSONException e) {
      throw new IllegalStateException(String.format("Unable to extract property %s ", propertyName),
          e);
    }
  }

}
