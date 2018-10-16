package gov.ca.cwds.jobs.common;

import static gov.ca.cwds.jobs.common.mode.DefaultJobMode.INCREMENTAL_LOAD;
import static gov.ca.cwds.jobs.common.mode.DefaultJobMode.INITIAL_LOAD;
import static gov.ca.cwds.jobs.utils.DataSourceFactoryUtils.fixDatasourceFactory;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import gov.ca.cwds.jobs.common.TestCustomModule.TestEntityWriter;
import gov.ca.cwds.jobs.common.configuration.JobConfiguration;
import gov.ca.cwds.jobs.common.core.JobPreparator;
import gov.ca.cwds.jobs.common.core.JobRunner;
import gov.ca.cwds.jobs.common.exception.JobsException;
import gov.ca.cwds.jobs.common.inject.BrokenTestEntityServiceProvider;
import gov.ca.cwds.jobs.common.inject.JobModule;
import gov.ca.cwds.jobs.common.inject.MultiThreadModule;
import gov.ca.cwds.jobs.common.savepoint.LocalDateTimeSavePointContainer;
import gov.ca.cwds.jobs.common.savepoint.LocalDateTimeSavePointContainerService;
import gov.ca.cwds.jobs.common.util.LastRunDirHelper;
import gov.ca.cwds.test.support.DatabaseHelper;
import io.dropwizard.db.DataSourceFactory;
import java.io.IOException;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import liquibase.exception.LiquibaseException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by Alexander Serbin on 10/12/2018
 */
public class JobMainTest {

  private static final Logger LOGGER = LoggerFactory.getLogger(JobMainTest.class);
  private LastRunDirHelper lastRunDirHelper = new LastRunDirHelper("temp");
  private LocalDateTimeSavePointContainerService savePointContainerService =
      new LocalDateTimeSavePointContainerService(
          lastRunDirHelper.getSavepointContainerFolder().toString());

  @Before
  public void beforeMethod() throws IOException {
    lastRunDirHelper.createSavePointContainerFolder();
  }

  @After
  public void afterMethod() throws IOException {
    lastRunDirHelper.deleteSavePointContainerFolder();
    TestEntityWriter.reset();

  }

  @Test
  public void testCase1_initial_b1() throws IOException {
    runInitialJob("testCase1", "database_structure.xml");
    assertEquals(0, TestEntityWriter.getItems().size());
    LocalDateTimeSavePointContainer savePointContainer = getSavePointContainer();
    assertTrue(savePointContainer.getSavePoint().getTimestamp().equals(
        LocalDateTimeSavePointContainerService.VERY_FIRST_TIMESTAMP));
    assertEquals(INCREMENTAL_LOAD, savePointContainer.getJobMode());
  }

  @Test
  public void testCase2_initial_b10() throws IOException {
    runInitialJob("testCase2", "database_structure.xml");
    assertEquals(0, TestEntityWriter.getItems().size());
    LocalDateTimeSavePointContainer savePointContainer = getSavePointContainer();
    assertTrue(savePointContainer.getSavePoint().getTimestamp().equals(
        LocalDateTimeSavePointContainerService.VERY_FIRST_TIMESTAMP));
    assertEquals(INCREMENTAL_LOAD, savePointContainer.getJobMode());
  }

  @Test
  public void testCase3_initial_b1() throws IOException {
    runInitialJob("testCase3", "database_structure.xml",
        "testcases/testcase3/test_case_3.xml");
    assertEquals(1, TestEntityWriter.getItems().size());
    LocalDateTimeSavePointContainer savePointContainer = getSavePointContainer();
    assertTrue(savePointContainer.getSavePoint().getTimestamp().equals(
        LocalDateTime.of(2010, 1, 12, 5, 25, 13)));
    assertEquals(INCREMENTAL_LOAD, savePointContainer.getJobMode());
  }

  @Test
  public void testCase4_Initial_b10() throws IOException {
    runInitialJob("testCase4", "database_structure.xml",
        "testcases/testcase4/test_case_4.xml");
    assertEquals(1, TestEntityWriter.getItems().size());
    LocalDateTimeSavePointContainer savePointContainer = getSavePointContainer();
    assertTrue(savePointContainer.getSavePoint().getTimestamp().equals(
        LocalDateTime.of(2010, 1, 12, 5, 25, 13)));
    assertEquals(INCREMENTAL_LOAD, savePointContainer.getJobMode());
  }

  @Test
  public void testCase5_initial_b1() throws IOException {
    runInitialJob("testCase5", "database_structure.xml",
        "testcases/testcase5/test_case_5.xml");
    assertEquals(3, TestEntityWriter.getItems().size());
    LocalDateTimeSavePointContainer savePointContainer = getSavePointContainer();
    assertTrue(savePointContainer.getSavePoint().getTimestamp().equals(
        LocalDateTime.of(2019, 5, 6, 2, 3, 45)));
    assertEquals(INCREMENTAL_LOAD, savePointContainer.getJobMode());
  }

  @Test
  public void testCase6_initial_b2() throws IOException {
    runInitialJob("testCase6", "database_structure.xml",
        "testcases/testcase6/test_case_6.xml");
    assertEquals(3, TestEntityWriter.getItems().size());
    LocalDateTimeSavePointContainer savePointContainer = getSavePointContainer();
    assertTrue(savePointContainer.getSavePoint().getTimestamp().equals(
        LocalDateTime.of(2019, 5, 6, 2, 3, 45)));
    assertEquals(INCREMENTAL_LOAD, savePointContainer.getJobMode());
  }

  @Test
  public void testCase7_initial_b1() throws IOException {
    runInitialJob("testCase7", "database_structure.xml",
        "testcases/testcase7/test_case_7.xml");
    assertEquals(3, TestEntityWriter.getItems().size());
    LocalDateTimeSavePointContainer savePointContainer = getSavePointContainer();
    assertTrue(savePointContainer.getSavePoint().getTimestamp().equals(
        LocalDateTime.of(2019, 5, 6, 2, 3, 45)));
    assertEquals(INCREMENTAL_LOAD, savePointContainer.getJobMode());
  }

  @Test
  public void testCase8_incremental_b1() throws IOException {
    runInitialJob("testCase7", "database_structure.xml",
        "testcases/testcase7/test_case_7.xml");
    TestEntityWriter.reset();
    runIncrementalJob("testCase7");
    assertEquals(0, TestEntityWriter.getItems().size());
    LocalDateTimeSavePointContainer savePointContainer = getSavePointContainer();
    assertTrue(savePointContainer.getSavePoint().getTimestamp().equals(
        LocalDateTime.of(2019, 5, 6, 2, 3, 45)));
  }

  @Test
  public void testCase9_incremental_b1() throws IOException {
    testIncrementalLoad("testCase9");
  }

  @Test
  public void testCase10_incremental_b2() throws IOException {
    testIncrementalLoad("testCase10");
  }

  @Test
  public void testCase11_incremental_b10() throws IOException {
    testIncrementalLoad("testCase11");
  }

  @Test
  public void testCase12RestoreAfterCrashTest() throws IOException {
    try {
      runCrashJob();
    } catch (JobsException e) {
      assertEquals("java.lang.RuntimeException: Broken entity!!!", e.getCause().getMessage());
      assertEquals(1, TestEntityWriter.getItems().size());
      LocalDateTimeSavePointContainer savePointContainer = (LocalDateTimeSavePointContainer) savePointContainerService
          .readSavePointContainer(LocalDateTimeSavePointContainer.class);
      assertEquals(LocalDateTime.of(2016, 10, 10, 1, 2, 15),
          savePointContainer.getSavePoint().getTimestamp());
      assertEquals(INITIAL_LOAD, savePointContainer.getJobMode());
    }
    TestEntityWriter.reset();
    runInitialJob("testCase12");
    assertEquals(2, TestEntityWriter.getItems().size());
    LocalDateTimeSavePointContainer savePointContainer = (LocalDateTimeSavePointContainer) savePointContainerService
        .readSavePointContainer(LocalDateTimeSavePointContainer.class);
    assertEquals(LocalDateTime.of(2018, 5, 6, 2, 3,45),
        savePointContainer.getSavePoint().getTimestamp());
    assertEquals(INCREMENTAL_LOAD, savePointContainer.getJobMode());
  }

  private void runCrashJob() {
    TestJobConfiguration configuration = JobConfiguration
        .getJobsConfiguration(TestJobConfiguration.class, getConfigFilePath("testCase12"));
    String runDir = lastRunDirHelper.getSavepointContainerFolder().toString();
    JobModule<TestJobConfiguration> jobModule = new JobModule<>(
        configuration, runDir);
    TestCustomModule customModule = new TestCustomModule(configuration, runDir);
    customModule.setChangedEntityServiceProvider(BrokenTestEntityServiceProvider.class);
    jobModule.addModules(customModule,
        new MultiThreadModule(configuration.getMultiThread()),
        new TestDataAccessModule());
    jobModule.setJobPreparator(
        new TestJobPreparator(getConfigFilePath("testCase12"), "testCase12",
            "database_structure.xml",
            "testcases/testcase12/test_case_12.xml"));

    JobRunner.run(jobModule);
  }

  private void testIncrementalLoad(String testCase) {
    runInitialJob(testCase, "database_structure.xml");
    TestEntityWriter.reset();
    runIncrementalJob(testCase, "testcases/testcase9/test_case_9.xml");
    assertEquals(3, TestEntityWriter.getItems().size());
    LocalDateTimeSavePointContainer savePointContainer = getSavePointContainer();
    assertTrue(savePointContainer.getSavePoint().getTimestamp().equals(
        LocalDateTime.of(2019, 5, 6, 2, 3, 45)));
    assertEquals(INCREMENTAL_LOAD, savePointContainer.getJobMode());
  }

  private LocalDateTimeSavePointContainer getSavePointContainer() {
    return (LocalDateTimeSavePointContainer) savePointContainerService
        .readSavePointContainer(LocalDateTimeSavePointContainer.class);
  }

  private void runJob(String testCase, String... scripts) {
    TestJobConfiguration configuration = JobConfiguration
        .getJobsConfiguration(TestJobConfiguration.class, getConfigFilePath(testCase));
    String runDir = lastRunDirHelper.getSavepointContainerFolder().toString();
    JobModule<TestJobConfiguration> jobModule = new JobModule<>(
        configuration, runDir);
    jobModule.addModules(new TestCustomModule(configuration, runDir),
        new MultiThreadModule(configuration.getMultiThread()),
        new TestDataAccessModule());
    jobModule.setJobPreparator(
        new TestJobPreparator(getConfigFilePath(testCase), testCase, scripts));
    JobRunner.run(jobModule);
  }

  private void runIncrementalJob(String testCase, String... scripts) {
    runJob(testCase, scripts);
  }

  private void runInitialJob(String testCase, String... scripts) {
    runJob(testCase, scripts);
  }

  private String getConfigFilePath(String testCase) {
    return Paths
        .get("src", "test", "resources", "testcases", testCase, "config.yaml").normalize()
        .toAbsolutePath().toString();
  }

  class TestJobPreparator implements JobPreparator {

    private String configPath;
    private String[] scripts;
    private String testCaseName;

    public TestJobPreparator(String configPath, String testCaseName, String... scripts) {
      this.configPath = configPath;
      this.scripts = scripts;
      this.testCaseName = testCaseName;
    }

    @Override
    public void run() {
      LOGGER.info("Setup database has been started!!!");
      try {
        DatabaseHelper databaseHelper = createDatabaseHelper(configPath);
        for (String script : scripts) {
          databaseHelper.runScript(script, testCaseName);
        }
      } catch (LiquibaseException e) {
        LOGGER.error(e.getMessage(), e);
      }
      LOGGER.info("Setup database has been finished!!!");
    }
  }

  private DatabaseHelper createDatabaseHelper(String configPath) {
    TestJobConfiguration configuration = JobConfiguration
        .getJobsConfiguration(TestJobConfiguration.class, configPath);
    DataSourceFactory dataSourceFactory = configuration.getTestDataSourceFactory();
    fixDatasourceFactory(dataSourceFactory);
    return new DatabaseHelper(dataSourceFactory.getUrl(),
        dataSourceFactory.getUser(), dataSourceFactory.getPassword());
  }

}
