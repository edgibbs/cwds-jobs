package gov.ca.cwds.jobs.cals.facility;

import static gov.ca.cwds.jobs.cals.facility.AssertFacilityHelper.assertFacility;
import static gov.ca.cwds.jobs.common.mode.JobMode.INCREMENTAL_LOAD;
import static gov.ca.cwds.jobs.common.mode.JobMode.INITIAL_LOAD;
import static gov.ca.cwds.test.support.DatabaseHelper.setUpDatabase;
import static org.junit.Assert.assertEquals;

import com.fasterxml.jackson.core.JsonProcessingException;
import gov.ca.cwds.DataSourceName;
import gov.ca.cwds.cals.DatabaseHelper;
import gov.ca.cwds.jobs.cals.facility.lisfas.LisFacilityJobConfiguration;
import gov.ca.cwds.jobs.cals.facility.lisfas.inject.LisFacilityJobModule;
import gov.ca.cwds.jobs.cals.facility.lisfas.mode.LisInitialJobModeFinalizerProvider;
import gov.ca.cwds.jobs.cals.facility.lisfas.savepoint.LicenseNumberSavePoint;
import gov.ca.cwds.jobs.cals.facility.lisfas.savepoint.LicenseNumberSavePointContainer;
import gov.ca.cwds.jobs.cals.facility.lisfas.savepoint.LicenseNumberSavePointContainerService;
import gov.ca.cwds.jobs.cals.facility.lisfas.savepoint.LisTimestampSavePointContainer;
import gov.ca.cwds.jobs.cals.facility.lisfas.savepoint.LisTimestampSavePointContainerService;
import gov.ca.cwds.jobs.common.TestWriter;
import gov.ca.cwds.jobs.common.batch.BatchProcessor;
import gov.ca.cwds.jobs.common.configuration.JobConfiguration;
import gov.ca.cwds.jobs.common.configuration.JobOptions;
import gov.ca.cwds.jobs.common.core.JobPreparator;
import gov.ca.cwds.jobs.common.core.JobRunner;
import gov.ca.cwds.jobs.common.inject.JobModule;
import gov.ca.cwds.jobs.common.inject.MultiThreadModule;
import gov.ca.cwds.jobs.common.mode.JobMode;
import gov.ca.cwds.jobs.common.util.LastRunDirHelper;
import gov.ca.cwds.jobs.utils.DataSourceFactoryUtils;
import io.dropwizard.db.DataSourceFactory;
import java.math.BigInteger;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import liquibase.exception.LiquibaseException;
import org.json.JSONException;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by Alexander Serbin on 3/18/2018.
 */
public class LisFacilityJobTest {

  private static final Logger LOGGER = LoggerFactory.getLogger(BatchProcessor.class);

  private static LastRunDirHelper lastRunDirHelper = new LastRunDirHelper("lis_job_temp");
  private static final String LIS_INITIAL_LOAD_FACILITY_ID = "9069";

  public static final DateTimeFormatter lisTimestampFormatter = DateTimeFormatter
      .ofPattern("yyyyMMddHHmmss");

  @Test
  public void lisFacilityJobTest()
      throws Exception {
    try {
      lastRunDirHelper.deleteSavePointContainerFolder();
      testInitialLoad();
      testInitialResumeLoad();
      testIncrementalLoad();
    } finally {
      lastRunDirHelper.deleteSavePointContainerFolder();
      FacilityTestWriter.reset();
    }
  }

  private void testIncrementalLoad()
      throws Exception {
    runJob(INCREMENTAL_LOAD);
    assertEquals(0, TestWriter.getItems().size());
    assertInitialLoadSuccessful();
    String newTimestamp = addLisDataForIncrementalLoad();
    runJob(INCREMENTAL_LOAD);
    assertEquals(1, TestWriter.getItems().size());
    assertFacility("fixtures/facilities-lis.json",
        LIS_INITIAL_LOAD_FACILITY_ID);

    LisTimestampSavePointContainerService savePointContainerService =
        new LisTimestampSavePointContainerService(
            lastRunDirHelper.getSavepointContainerFolder().toString());
    LisTimestampSavePointContainer savePointContainer =
        (LisTimestampSavePointContainer) savePointContainerService
            .readSavePointContainer(LisTimestampSavePointContainer.class);
    assertEquals(new BigInteger(newTimestamp),
        savePointContainer.getSavePoint().getTimestamp());
    assertEquals(INCREMENTAL_LOAD, savePointContainer.getJobMode());
  }

  private void testInitialResumeLoad() {
    LicenseNumberSavePointContainerService licenseNumberSavePointContainerService =
        new LicenseNumberSavePointContainerService(
            lastRunDirHelper.getSavepointContainerFolder().toString());
    LicenseNumberSavePointContainer container = new LicenseNumberSavePointContainer();
    container.setJobMode(INITIAL_LOAD);
    container.setSavePoint(new LicenseNumberSavePoint(909045136));
    licenseNumberSavePointContainerService.writeSavePointContainer(container);
    runJob(INITIAL_LOAD);
    assertInitialLoadSuccessful();
  }

  private void testInitialLoad() throws JSONException, JsonProcessingException {
    runJob(INITIAL_LOAD);
    assertEquals(316, TestWriter.getItems().size());
    assertFacility("fixtures/facilities-lis.json", LIS_INITIAL_LOAD_FACILITY_ID);
    assertInitialLoadSuccessful();
  }

  private void assertInitialLoadSuccessful() {
    LisTimestampSavePointContainerService savePointContainerService =
        new LisTimestampSavePointContainerService(
            lastRunDirHelper.getSavepointContainerFolder().toString());
    LisTimestampSavePointContainer savePointContainer =
        (LisTimestampSavePointContainer) savePointContainerService
            .readSavePointContainer(LisTimestampSavePointContainer.class);
    assertEquals(new BigInteger("20180319163643"),
        savePointContainer.getSavePoint().getTimestamp());
    assertEquals(INCREMENTAL_LOAD, savePointContainer.getJobMode());
  }

  private String addLisDataForIncrementalLoad() throws LiquibaseException {
    String newTimestamp = lisTimestampFormatter.format(LocalDateTime.now());
    DataSourceFactory lisDataSourceFactory = getFacilityJobConfiguration()
        .getLisDataSourceFactory();
    DatabaseHelper lisDatabaseHelper = new DatabaseHelper(lisDataSourceFactory.getUrl(),
        lisDataSourceFactory.getUser(), lisDataSourceFactory.getPassword());
    Map<String, Object> parameters = new HashMap<>();
    parameters.put("now", newTimestamp);
    lisDatabaseHelper.runScript("liquibase/lis_facility_incremental_load.xml", parameters, "lis");
    return newTimestamp;
  }

  private static LisFacilityJobConfiguration getFacilityJobConfiguration() {
    LisFacilityJobConfiguration facilityJobConfiguration =
        JobConfiguration
            .getJobsConfiguration(LisFacilityJobConfiguration.class, getConfigFilePath());
    DataSourceFactoryUtils
        .fixDatasourceFactory(facilityJobConfiguration.getCalsnsDataSourceFactory());
    DataSourceFactoryUtils.fixDatasourceFactory(facilityJobConfiguration.getLisDataSourceFactory());
    DataSourceFactoryUtils.fixDatasourceFactory(facilityJobConfiguration.getFasDataSourceFactory());

    return facilityJobConfiguration;
  }

  private void runJob(JobMode jobMode) {
    JobOptions jobOptions = JobOptions.parseCommandLine(getModuleArgs());
    LisFacilityJobConfiguration jobConfiguration = JobConfiguration
        .getJobsConfiguration(LisFacilityJobConfiguration.class,
            jobOptions.getConfigFileLocation());
    JobModule jobModule = new JobModule(jobOptions.getLastRunLoc());
    jobModule.addModules(new MultiThreadModule(jobConfiguration.getMultiThread()));
    LisFacilityJobModule lisFacilityJobModule = new LisFacilityJobModule(jobConfiguration, jobMode);
    jobModule.setJobPreparator(new LisJobPreparator());
    jobModule.addModule(lisFacilityJobModule);
    FacilityTestWriter.reset();
    lisFacilityJobModule.setFacilityElasticWriterClass(FacilityTestWriter.class);
    lisFacilityJobModule
        .setPrimaryJobFinalizerProviderClass(LisInitialJobModeFinalizerProvider.class);
    JobRunner.run(jobModule);
  }

  private String[] getModuleArgs() {
    return new String[]{"-c", getConfigFilePath(), "-l",
        lastRunDirHelper.getSavepointContainerFolder().toString()};
  }

  private static String getConfigFilePath() {
    return Paths.get("src", "test", "resources", "lis-test-facility-job.yaml")
        .normalize().toAbsolutePath().toString();
  }

  static class LisJobPreparator implements JobPreparator {

    @Override
    public void run() {
      LOGGER.info("Setup database has been started!!!");
      LisFacilityJobConfiguration configuration = getFacilityJobConfiguration();
      try {
        setUpDatabase(configuration.getCalsnsDataSourceFactory(), DataSourceName.NS);
        setUpDatabase(configuration.getLisDataSourceFactory(), DataSourceName.LIS);
        setUpDatabase(configuration.getFasDataSourceFactory(), DataSourceName.FAS);
      } catch (LiquibaseException e) {
        LOGGER.error(e.getMessage(), e);
      }
      LOGGER.info("Setup database has been finished!!!");
    }
  }
}
