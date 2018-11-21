package gov.ca.cwds.jobs.cals.facility.lisfas;

import static org.junit.Assert.assertEquals;

import gov.ca.cwds.jobs.cals.facility.lisfas.inject.LisFacilityJobModule;
import gov.ca.cwds.jobs.common.inject.ElasticSearchModule;
import gov.ca.cwds.jobs.common.inject.JobModule;
import gov.ca.cwds.jobs.common.inject.MultiThreadModule;
import java.nio.file.Paths;
import org.junit.Test;

public class LisJobModuleBuilderTest {

  @Test
  public void buildJobModuleTest() {
    JobModule jobModule = new LisJobModuleBuilder().buildJobModule(getModuleArgs());
    assertEquals(3, jobModule.getModules().size());
    assertEquals(1,
        jobModule.getModules().stream().filter(m -> m instanceof ElasticSearchModule).count());
    assertEquals(1,
        jobModule.getModules().stream().filter(m -> m instanceof LisFacilityJobModule).count());
    assertEquals(1,
        jobModule.getModules().stream().filter(m -> m instanceof MultiThreadModule).count());
  }

  private String[] getModuleArgs() {
    return new String[]{"-c", getConfigFilePath(), "-l", "test"};
  }

  private static String getConfigFilePath() {
    return Paths.get("src", "test", "resources", "lis-test-facility-jobrunner-test.yaml")
        .normalize().toAbsolutePath().toString();
  }

}