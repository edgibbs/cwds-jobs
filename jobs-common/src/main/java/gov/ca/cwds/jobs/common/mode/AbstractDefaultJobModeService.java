package gov.ca.cwds.jobs.common.mode;

import static gov.ca.cwds.jobs.common.mode.DefaultJobMode.INCREMENTAL_LOAD;
import static gov.ca.cwds.jobs.common.mode.DefaultJobMode.INITIAL_LOAD;

import com.google.inject.Inject;
import gov.ca.cwds.jobs.common.savepoint.SavePoint;
import gov.ca.cwds.jobs.common.savepoint.SavePointContainerService;
import gov.ca.cwds.rest.api.ApiException;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
import org.apache.commons.io.IOUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by Alexander Serbin on 6/20/2018.
 */
public abstract class AbstractDefaultJobModeService<S extends SavePoint> implements
    JobModeService<DefaultJobMode> {

  private static final Logger LOG = LoggerFactory.getLogger(AbstractDefaultJobModeService.class);
  public static final String CURRENT_JOB_MODE_IS = "Current job mode is {}";

  @Inject
  private SavePointContainerService<S, DefaultJobMode> savePointContainerService;

  @Override
  public DefaultJobMode getCurrentJobMode() {
    if (!savePointContainerService.savePointContainerExists()) {
      LOG.info("Save point container file is not found");
      LOG.info(CURRENT_JOB_MODE_IS, INITIAL_LOAD);
      return INITIAL_LOAD;
    }
    return extractJobMode();
  }

  private DefaultJobMode extractJobMode() {
    Path pathToSavePointContainerFile = savePointContainerService.getSavePointFile();
    LOG.info("Path to the save point container file: {}", pathToSavePointContainerFile);
    try (Reader reader = Files.newBufferedReader(pathToSavePointContainerFile)) {
      String savePointContainer = IOUtils.toString(reader);
      LOG.info("Save point container is {}", savePointContainer);
      JSONObject jsonObject = new JSONObject(savePointContainer);
      String jobMode = jsonObject.getString("jobMode");
      switch (jobMode) {
        case "INITIAL_LOAD":
          LOG.info(CURRENT_JOB_MODE_IS, INITIAL_LOAD);
          return INITIAL_LOAD;
        case "INCREMENTAL_LOAD":
          LOG.info(CURRENT_JOB_MODE_IS, INCREMENTAL_LOAD);
          return INCREMENTAL_LOAD;
        default:
          throw new IllegalStateException(String.format("Unexpected job mode %s", jobMode));
      }
    } catch (IOException | JSONException e) {
      LOG.error(e.getMessage(), e);
      throw new ApiException("Can't parse save point container file", e);
    }
  }

  public void setSavePointContainerService(
      SavePointContainerService<S, DefaultJobMode> savePointContainerService) {
    this.savePointContainerService = savePointContainerService;
  }

}
