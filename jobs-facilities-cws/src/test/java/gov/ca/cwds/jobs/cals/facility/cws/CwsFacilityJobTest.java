package gov.ca.cwds.jobs.cals.facility.cws;

import static gov.ca.cwds.jobs.cals.facility.AssertFacilityHelper.assertFacility;
import static gov.ca.cwds.jobs.common.mode.DefaultJobMode.INCREMENTAL_LOAD;
import static gov.ca.cwds.test.support.DatabaseHelper.setUpDatabase;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.inject.AbstractModule;
import gov.ca.cwds.DataSourceName;
import gov.ca.cwds.jobs.cals.facility.BaseFacilityJobConfiguration;
import gov.ca.cwds.jobs.cals.facility.ChangedFacilityDto;
import gov.ca.cwds.jobs.cals.facility.FacilityTestWriter;
import gov.ca.cwds.jobs.cals.facility.cws.inject.CwsFacilityJobModule;
import gov.ca.cwds.jobs.common.TestWriter;
import gov.ca.cwds.jobs.common.core.JobPreparator;
import gov.ca.cwds.jobs.common.core.JobRunner;
import gov.ca.cwds.jobs.common.mode.DefaultJobMode;
import gov.ca.cwds.jobs.common.savepoint.LocalDateTimeSavePointContainer;
import gov.ca.cwds.jobs.common.savepoint.LocalDateTimeSavePointContainerService;
import gov.ca.cwds.jobs.common.util.LastRunDirHelper;
import gov.ca.cwds.jobs.utils.DataSourceFactoryUtils;
import gov.ca.cwds.test.support.DatabaseHelper;
import io.dropwizard.db.DataSourceFactory;
import java.io.IOException;
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
public class CwsFacilityJobTest {

  private static final Logger LOGGER = LoggerFactory.getLogger(CwsFacilityJobTest.class);

  private static LastRunDirHelper lastRunDirHelper = new LastRunDirHelper("cws_job_temp");
  private LocalDateTimeSavePointContainerService savePointContainerService =
      new LocalDateTimeSavePointContainerService(
          lastRunDirHelper.getSavepointContainerFolder().toString());

  private static final String CWSCMS_INITIAL_LOAD_FACILITY_ID = "3w6sOO50Ki";
  private static final String CWSCMS_INCREMENTAL_LOAD_NEW_FACILITY_ID = "AAAAAAAAAA";
  private static final String CWSCMS_INCREMENTAL_LOAD_UPDATED_FACILITY_ID = "AP9Ewb409u";
  private static final String CWSCMS_INCREMENTAL_LOAD_DELETED_FACILITY_ID = "AyT7r860AB";

  @Test
  public void cwsFacilityJobTest()
      throws IOException, JSONException, InterruptedException, LiquibaseException {
    try {
      lastRunDirHelper.deleteSavePointContainerFolder();
      testInitialLoad();
      testInitialResumeLoad(DefaultJobMode.INITIAL_LOAD);
      testInitialResumeLoad(DefaultJobMode.INITIAL_LOAD_RESUME);
      testIncrementalLoad();
    } finally {
      lastRunDirHelper.deleteSavePointContainerFolder();
      FacilityTestWriter.reset();
    }
  }

  private void testInitialResumeLoad(DefaultJobMode jobMode) {
    LocalDateTimeSavePointContainer container = (LocalDateTimeSavePointContainer) savePointContainerService
        .readSavePointContainer(LocalDateTimeSavePointContainer.class);
    container.setJobMode(jobMode);
    LocalDateTime savePoint = LocalDateTime.of(2010, 01, 14, 9, 35, 17, 664000000);
    container.getSavePoint().setTimestamp(savePoint);
    savePointContainerService.writeSavePointContainer(container);
    runInitialLoad();
    assertEquals(2, TestWriter.getItems().size());
    assertFacilityPresent("2qiZOcd04Y");
    assertFacilityPresent("3UGSdyX0Ki");
    assertEquals(INCREMENTAL_LOAD, savePointContainerService
        .readSavePointContainer(LocalDateTimeSavePointContainer.class).getJobMode());
  }

  private void assertFacilityPresent(String facilityId) {
    assertEquals(1, TestWriter.getItems().stream()
        .filter(o -> facilityId.equals(((ChangedFacilityDto) o).getId())).count());
  }

  private void testIncrementalLoad()
      throws LiquibaseException, JSONException, JsonProcessingException {
    runIncrementalLoad();
    assertEquals(0, TestWriter.getItems().size());
    addCwsDataForIncrementalLoad();
    runIncrementalLoad();
    assertEquals(3, TestWriter.getItems().size());
    assertFacility("fixtures/cwsrs_new_facility.json",
        CWSCMS_INCREMENTAL_LOAD_NEW_FACILITY_ID);
    assertFacility("fixtures/cwsrs_updated_facility.json",
        CWSCMS_INCREMENTAL_LOAD_UPDATED_FACILITY_ID);
    assertFacility("fixtures/cwsrs_deleted_facility.json",
        CWSCMS_INCREMENTAL_LOAD_DELETED_FACILITY_ID);
  }

  private void testInitialLoad() throws IOException, JSONException {
    LocalDateTime now = LocalDateTime.now();
    assertEquals(0, TestWriter.getItems().size());
    runInitialLoad();
    assertEquals(167, TestWriter.getItems().size());
    assertFacility("fixtures/facilities-initial-load-cwscms.json",
        CWSCMS_INITIAL_LOAD_FACILITY_ID);

    LocalDateTimeSavePointContainer savePointContainer = (LocalDateTimeSavePointContainer) savePointContainerService
        .readSavePointContainer(LocalDateTimeSavePointContainer.class);
    assertTrue(savePointContainer.getSavePoint().getTimestamp().equals(LocalDateTimeSavePointContainerService.VERY_FIRST_TIMESTAMP));
    assertEquals(INCREMENTAL_LOAD, savePointContainer.getJobMode());
  }

  private static CwsFacilityJobConfiguration getFacilityJobConfiguration() {
    CwsFacilityJobConfiguration facilityJobConfiguration =
        BaseFacilityJobConfiguration
            .getJobsConfiguration(CwsFacilityJobConfiguration.class, getConfigFilePath());
    DataSourceFactoryUtils.fixDatasourceFactory(facilityJobConfiguration.getCmsDataSourceFactory());
    DataSourceFactoryUtils
        .fixDatasourceFactory(facilityJobConfiguration.getCalsnsDataSourceFactory());
    return facilityJobConfiguration;
  }

  private void addCwsDataForIncrementalLoad() throws LiquibaseException {
    DateTimeFormatter datetimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");
    DataSourceFactory cwsDataSourceFactory = getFacilityJobConfiguration()
        .getCmsDataSourceFactory();
    DatabaseHelper cwsDatabaseHelper = new DatabaseHelper(cwsDataSourceFactory.getUrl(),
        cwsDataSourceFactory.getUser(), cwsDataSourceFactory.getPassword());
    Map<String, Object> parameters = new HashMap<>();
    parameters.put("now", datetimeFormatter.format(LocalDateTime.now()));
    cwsDatabaseHelper
        .runScript("liquibase/cwsrs_facility_incremental_load.xml", parameters, "CWSCMSRS");
  }

  private void runInitialLoad() {
    JobRunner.run(createCwsFacilityJobModule(CwsJobPreparator.class));
  }

  private CwsFacilityJobModule createCwsFacilityJobModule(
      Class<? extends JobPreparator> jobPreparatorClass) {
    CwsFacilityJobModule cwsFacilityJobModule = new CwsFacilityJobModule(getModuleArgs());
    cwsFacilityJobModule.setElasticSearchModule(new AbstractModule() {
      @Override
      protected void configure() {
        // Do nothing here
      }
    });
    FacilityTestWriter.reset();
    cwsFacilityJobModule.setFacilityElasticWriterClass(FacilityTestWriter.class);
    cwsFacilityJobModule.setJobPreparator(jobPreparatorClass);
    return cwsFacilityJobModule;
  }

  private void runIncrementalLoad() {
    runInitialLoad();
  }

  private String[] getModuleArgs() {
    return new String[]{"-c", getConfigFilePath(), "-l",
        lastRunDirHelper.getSavepointContainerFolder().toString()};
  }

  private static String getConfigFilePath() {
    return Paths.get("src", "test", "resources", "cws-test-facility-job.yaml")
        .normalize().toAbsolutePath().toString();
  }

  static class CwsJobPreparator implements JobPreparator {

    @Override
    public void run() {
      LOGGER.info("Setup database has been started!!!");
      CwsFacilityJobConfiguration configuration = getFacilityJobConfiguration();
      try {
        setUpDatabase(configuration.getCalsnsDataSourceFactory(), DataSourceName.NS);
        setUpDatabase(configuration.getCmsDataSourceFactory(), DataSourceName.CWSRS);
      } catch (LiquibaseException e) {
        LOGGER.error(e.getMessage(), e);
      }

      LOGGER.info("Setup database has been finished!!!");
    }
  }

}