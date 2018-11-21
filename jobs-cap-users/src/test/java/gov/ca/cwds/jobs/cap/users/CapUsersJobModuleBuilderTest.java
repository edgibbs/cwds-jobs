package gov.ca.cwds.jobs.cap.users;

import static org.junit.Assert.assertEquals;

import gov.ca.cwds.jobs.common.inject.ElasticSearchModule;
import gov.ca.cwds.jobs.common.inject.JobModule;
import java.nio.file.Paths;
import org.junit.Test;

public class CapUsersJobModuleBuilderTest {

  @Test
  public void buildJobModuleTest() {
    JobModule jobModule = new CapUsersJobModuleBuilder().buildJobModule(getModuleArgs());
    assertEquals(2, jobModule.getModules().size());
    assertEquals(1,
        jobModule.getModules().stream().filter(m -> m instanceof ElasticSearchModule).count());
    assertEquals(1,
        jobModule.getModules().stream().filter(m -> m instanceof CapUsersJobModule).count());
  }

  private String[] getModuleArgs() {
    return new String[]{"-c", getConfigFilePath(), "-l", "test"};
  }

  private static String getConfigFilePath() {
    return Paths.get("src", "test", "resources", "cap-users-job-runner-test.yaml")
        .normalize().toAbsolutePath().toString();
  }

}