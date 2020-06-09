package gov.ca.cwds.jobs.common.mode;

import static gov.ca.cwds.jobs.common.mode.JobMode.INCREMENTAL_LOAD;
import static gov.ca.cwds.jobs.common.mode.JobMode.INITIAL_LOAD;
import static gov.ca.cwds.jobs.common.mode.JobMode.INITIAL_RESUME;
import static gov.ca.cwds.jobs.common.util.SavePointUtil.extractProperty;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;

import gov.ca.cwds.jobs.common.inject.PrimaryContainerService;
import gov.ca.cwds.jobs.common.savepoint.SavePoint;
import gov.ca.cwds.jobs.common.savepoint.SavePointContainerService;

/**
 * Created by Alexander Serbin on 6/20/2018.
 */
public abstract class AbstractJobModeService<S extends SavePoint> implements JobModeService {

  private static final Logger LOG = LoggerFactory.getLogger(AbstractJobModeService.class);

  public static final String CURRENT_JOB_MODE_IS = "Current job mode is {}";

  @Inject
  @PrimaryContainerService
  private SavePointContainerService<S> savePointContainerService;

  @Override
  public JobMode getCurrentJobMode() {
    if (!savePointContainerService.savePointContainerExists()) {
      LOG.info("Save point container file is not found");
      LOG.info(CURRENT_JOB_MODE_IS, INITIAL_LOAD);
      return INITIAL_LOAD;
    }
    return extractJobMode();
  }

  private JobMode extractJobMode() {
    String jobMode = extractProperty(savePointContainerService.getSavePointFile(), "jobMode");
    switch (jobMode) {
      case "INITIAL_RESUME":
      case "INITIAL_LOAD":
        LOG.info(CURRENT_JOB_MODE_IS, INITIAL_RESUME);
        return INITIAL_RESUME;
      case "INCREMENTAL_LOAD":
        LOG.info(CURRENT_JOB_MODE_IS, INCREMENTAL_LOAD);
        return INCREMENTAL_LOAD;
      default:
        throw new IllegalStateException(String.format("Unexpected job mode %s", jobMode));
    }
  }

  public void setSavePointContainerService(SavePointContainerService<S> savePointContainerService) {
    this.savePointContainerService = savePointContainerService;
  }

}
