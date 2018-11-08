package gov.ca.cwds.jobs.cap.users.job;

import com.google.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CapUsersInitialJob extends AbstractCapUsersJob {
  private static final Logger LOGGER = LoggerFactory.getLogger(CapUsersInitialJob.class);

  @Inject
  CapUsersBatchProcessor batchProcessor;

  @Override
  void runJob() {
    LOGGER.info("Initial Cap Users Job is running");
    batchProcessor.processBatches();
    LOGGER.info("Finishing Initial Cap Users Job");
  }

  @Override
  public void close() {
    batchProcessor.destroy();
    super.close();
  }
}
