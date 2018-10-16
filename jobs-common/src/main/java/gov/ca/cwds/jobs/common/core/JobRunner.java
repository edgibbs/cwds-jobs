package gov.ca.cwds.jobs.common.core;

import com.google.inject.Guice;
import gov.ca.cwds.jobs.common.inject.JobModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author CWDS TPT-2
 */
public final class JobRunner {

  private static final Logger LOGGER = LoggerFactory.getLogger(JobRunner.class);

  private JobRunner() {
  }

  public static void run(JobModule mainModule) {
    Job job = null;
    try {
      LOGGER.info("Job has been started");
      job = Guice.createInjector(mainModule).getInstance(Job.class);
      job.run();
      LOGGER.info("Job has been finished");
    } finally {
      if (job != null) {
        job.close();
      }
    }
  }

}
