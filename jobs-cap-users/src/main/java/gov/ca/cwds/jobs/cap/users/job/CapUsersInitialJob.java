package gov.ca.cwds.jobs.cap.users.job;

import com.google.inject.Inject;
import gov.ca.cwds.jobs.cap.users.dto.CapJobResult;
import gov.ca.cwds.jobs.common.inject.PrimaryFinalizer;
import gov.ca.cwds.jobs.common.mode.JobModeFinalizer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CapUsersInitialJob extends AbstractCapUsersJob {

  private static final Logger LOGGER = LoggerFactory.getLogger(CapUsersInitialJob.class);

  @Inject
  CapUsersBatchProcessor batchProcessor;

  @Inject
  @PrimaryFinalizer
  private JobModeFinalizer jobFinalizer;

  @Override
  CapJobResult runJob() {
    LOGGER.info("Initial Cap Users Job is running");
    batchProcessor.processBatches();
    LOGGER.info("Finishing Initial Cap Users Job");
    jobFinalizer.doFinalizeJob();
    return new CapJobResult(true, true);
  }

  @Override
  public void close() {
    batchProcessor.destroy();
    super.close();
  }

}
