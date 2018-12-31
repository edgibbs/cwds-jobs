package gov.ca.cwds.jobs.audit;

import gov.ca.cwds.jobs.common.configuration.JobConfiguration;
import gov.ca.cwds.jobs.common.configuration.JobOptions;
import gov.ca.cwds.jobs.common.core.JobRunner;
import gov.ca.cwds.jobs.common.inject.JobModule;
import gov.ca.cwds.jobs.common.util.LastRunDirHelper;
import java.io.IOException;
import java.nio.file.Paths;
import org.junit.Test;

public class AuditEventsJobTest {

  private static LastRunDirHelper lastRunDirHelper = new LastRunDirHelper("audit_events_job_temp");

  @Test
  public void capUsersJobTest() throws IOException {
    try {
      lastRunDirHelper.deleteSavePointContainerFolder();
      JobOptions jobOptions = JobOptions.parseCommandLine(getModuleArgs());
      AuditEventsJobConfiguration jobConfiguration = JobConfiguration
          .getJobsConfiguration(AuditEventsJobConfiguration.class,
              jobOptions.getConfigFileLocation());
      JobModule jobModule = new JobModule(jobOptions.getLastRunLoc());
      AuditEventsJobModule capUsersJobModule = new AuditEventsJobModule(jobConfiguration);
      jobModule.addModule(capUsersJobModule);
      JobRunner.run(jobModule);
    } finally {
      lastRunDirHelper.deleteSavePointContainerFolder();
    }
  }

  private String[] getModuleArgs() {
    return new String[]{"-c", getConfigFilePath(), "-l",
        lastRunDirHelper.getSavepointContainerFolder().toString()};
  }

  private static String getConfigFilePath() {
    return Paths.get("src", "test", "resources", "audit-events-test.yaml")
        .normalize().toAbsolutePath().toString();
  }


}
