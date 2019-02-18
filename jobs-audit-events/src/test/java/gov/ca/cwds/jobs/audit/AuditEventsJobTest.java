package gov.ca.cwds.jobs.audit;

import static gov.ca.cwds.jobs.common.mode.DefaultJobMode.INCREMENTAL_LOAD;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import com.fasterxml.jackson.core.JsonProcessingException;
import gov.ca.cwds.jobs.common.configuration.JobConfiguration;
import gov.ca.cwds.jobs.common.configuration.JobOptions;
import gov.ca.cwds.jobs.common.core.JobPreparator;
import gov.ca.cwds.jobs.common.core.JobRunner;
import gov.ca.cwds.jobs.common.inject.JobModule;
import gov.ca.cwds.jobs.common.inject.MultiThreadModule;
import gov.ca.cwds.jobs.common.mode.DefaultJobMode;
import gov.ca.cwds.jobs.common.savepoint.LocalDateTimeSavePointContainer;
import gov.ca.cwds.jobs.common.savepoint.LocalDateTimeSavePointContainerService;
import gov.ca.cwds.jobs.common.util.LastRunDirHelper;
import gov.ca.cwds.jobs.utils.DataSourceFactoryUtils;
import gov.ca.cwds.test.support.DatabaseHelper;
import io.dropwizard.db.DataSourceFactory;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import liquibase.exception.LiquibaseException;
import org.json.JSONException;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AuditEventsJobTest {

  private static final Logger LOGGER = LoggerFactory.getLogger(AuditEventsJobTest.class);
  private static LastRunDirHelper lastRunDirHelper = new LastRunDirHelper("audit_events_job_temp");
  private static byte INITIAL_AUDIT_EVENTS_COUNT = 3;
  private static LocalDateTime SAVEPOINT_2 = LocalDateTime
      .of(2001, 9, 25, 11, 0, 10);
  private static LocalDateTime SAVEPOINT_3 = LocalDateTime
      .of(2003, 10, 11, 9, 54, 8);
  private LocalDateTimeSavePointContainerService savePointContainerService =
      new LocalDateTimeSavePointContainerService(
          lastRunDirHelper.getSavepointContainerFolder().toString());

  @Test
  public void auditJobTest() throws Exception {
    lastRunDirHelper.createSavePointContainerFolder();
    try {
      testInitialLoad();
      testInitialResumeLoad(DefaultJobMode.INITIAL_LOAD);
      testIncrementalLoad();
    } finally {
      lastRunDirHelper.deleteSavePointContainerFolder();
    }
  }

  private void testInitialLoad() throws JsonProcessingException, JSONException {
    LocalDateTime now = LocalDateTime.now();
    assertEquals(0, AuditEventTestWriter.getItems().size());
    runInitialLoad();
    assertEquals(INITIAL_AUDIT_EVENTS_COUNT, AuditEventTestWriter.getItems().size());
    assertEquals("{'test':1}", getAuditEventById("1").getDTO());

    LocalDateTimeSavePointContainer savePointContainer = (LocalDateTimeSavePointContainer) savePointContainerService
        .readSavePointContainer(LocalDateTimeSavePointContainer.class);
    assertEquals(SAVEPOINT_3, savePointContainer.getSavePoint().getTimestamp());
    assertEquals(INCREMENTAL_LOAD, savePointContainer.getJobMode());
  }

  private void runInitialLoad() {
    JobOptions jobOptions = JobOptions.parseCommandLine(getModuleArgs());
    AuditEventsJobConfiguration jobConfiguration = JobConfiguration
        .getJobsConfiguration(AuditEventsJobConfiguration.class,
            jobOptions.getConfigFileLocation());
    JobModule jobModule = new JobModule(jobOptions.getLastRunLoc());
    jobModule.addModules(new MultiThreadModule(jobConfiguration.getMultiThread()));
    AuditEventsJobModule auditEventsJobModule = new AuditEventsJobModule(jobConfiguration,
        jobOptions.getLastRunLoc());
    auditEventsJobModule.setAuditEventWriterClass(AuditEventTestWriter.class);
    AuditEventTestWriter.reset();
    jobModule.addModule(auditEventsJobModule);
    jobModule.setJobPreparator(new AuditEventsJobPreparator());
    JobRunner.run(jobModule);
  }

  private void testInitialResumeLoad(DefaultJobMode jobMode) {
    LocalDateTimeSavePointContainer container = (LocalDateTimeSavePointContainer) savePointContainerService
        .readSavePointContainer(LocalDateTimeSavePointContainer.class);
    container.setJobMode(jobMode);
    container.getSavePoint().setTimestamp(SAVEPOINT_2);
    savePointContainerService.writeSavePointContainer(container);
    runInitialLoad();
    assertEquals(1, AuditEventTestWriter.getItems().size());
    assertEquals(INCREMENTAL_LOAD, savePointContainerService
        .readSavePointContainer(LocalDateTimeSavePointContainer.class).getJobMode());
    assertEquals(SAVEPOINT_3, savePointContainerService
        .readSavePointContainer(LocalDateTimeSavePointContainer.class).getSavePoint()
        .getTimestamp());
  }

  private void runIncrementalLoad() {
    runInitialLoad();
  }

  private void testIncrementalLoad() throws Exception {
    runIncrementalLoad();
    assertEquals(0, AuditEventTestWriter.getItems().size());
    addDataForIncrementalLoad();
    runIncrementalLoad();
    assertEquals(1, AuditEventTestWriter.getItems().size());
    assertEquals("inc", ((AuditEventChangedDto) (AuditEventTestWriter.getItems().get(0))).getId());
  }

  private void addDataForIncrementalLoad() throws LiquibaseException {
    DateTimeFormatter datetimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");
    Map<String, Object> parameters = new HashMap<>();
    parameters.put("now", datetimeFormatter.format(LocalDateTime.now()));
    getDatabaseHelper()
        .runScript("liquibase/ns_audit_events_incremental_data.xml", parameters, "ns");

  }

  private String[] getModuleArgs() {
    return new String[]{"-c", getConfigFilePath(), "-l",
        lastRunDirHelper.getSavepointContainerFolder().toString()};
  }

  private static String getConfigFilePath() {
    return Paths.get("src", "test", "resources", "audit-events-test.yaml")
        .normalize().toAbsolutePath().toString();
  }

  private static AuditEventsJobConfiguration getJobConfiguration() {
    AuditEventsJobConfiguration configuration =
        JobConfiguration
            .getJobsConfiguration(AuditEventsJobConfiguration.class, getConfigFilePath());
    DataSourceFactoryUtils.fixDatasourceFactory(configuration.getNsDataSourceFactory());
    return configuration;
  }

  static class AuditEventsJobPreparator implements JobPreparator {

    @Override
    public void run() {
      LOGGER.info("Setup database has been started!!!");
      try {
        DatabaseHelper databaseHelper = getDatabaseHelper();
        databaseHelper.runScript("liquibase/perry_database_master.xml");
        databaseHelper.runScript("liquibase/ns_audit_events_initial_data.xml");
      } catch (LiquibaseException e) {
        LOGGER.error(e.getMessage(), e);
      }
      LOGGER.info("Setup database has been finished!!!");
    }
  }

  private static DatabaseHelper getDatabaseHelper() {
    DataSourceFactory nsDataSourceFactory = getJobConfiguration().getNsDataSourceFactory();
    return new DatabaseHelper(
        nsDataSourceFactory.getUrl(), nsDataSourceFactory.getUser(),
        nsDataSourceFactory.getPassword());
  }

  private static AuditEventChangedDto getAuditEventById(String eventId) {
    Optional<AuditEventChangedDto> optional = AuditEventTestWriter.getItems().stream()
        .filter(o -> eventId.equals(((AuditEventChangedDto) o).getId())).findAny();
    assertTrue(optional.isPresent());
    return optional.orElse(null);
  }

}
