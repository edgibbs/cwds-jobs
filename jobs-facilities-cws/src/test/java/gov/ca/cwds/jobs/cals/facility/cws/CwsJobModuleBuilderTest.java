package gov.ca.cwds.jobs.cals.facility.cws;

import static org.junit.Assert.assertEquals;

import gov.ca.cwds.jobs.cals.facility.cws.inject.CwsFacilityJobModule;
import gov.ca.cwds.jobs.common.inject.JobModule;
import gov.ca.cwds.jobs.common.inject.MultiThreadModule;
import java.nio.file.Paths;
import org.junit.Test;

public class CwsJobModuleBuilderTest {

  @Test
  public void buildJobModuleTest() {
    JobModule jobModule = new CwsJobModuleBuilder().buildJobModule(getModuleArgs(), false);
    assertEquals(2, jobModule.getModules().size());
    assertEquals(1,
        jobModule.getModules().stream().filter(m -> m instanceof CwsFacilityJobModule).count());
    assertEquals(1,
        jobModule.getModules().stream().filter(m -> m instanceof MultiThreadModule).count());
  }

  private String[] getModuleArgs() {
    return new String[]{"-c", getConfigFilePath(), "-l", "test"};
  }

  private static String getConfigFilePath() {
    return Paths.get("src", "test", "resources", "cws-test-facility-jobrunner-test.yaml")
        .normalize().toAbsolutePath().toString();
  }

}