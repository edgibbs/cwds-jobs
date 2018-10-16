package gov.ca.cwds.jobs.cap.users;

import com.google.inject.AbstractModule;
import gov.ca.cwds.jobs.common.configuration.MultiThreadJobConfiguration;
import gov.ca.cwds.jobs.common.core.JobRunner;
import gov.ca.cwds.jobs.common.savepoint.LocalDateTimeSavePointContainer;
import gov.ca.cwds.jobs.common.savepoint.LocalDateTimeSavePointContainerService;
import gov.ca.cwds.jobs.common.util.LastRunDirHelper;
import gov.ca.cwds.test.support.DatabaseHelper;
import io.dropwizard.db.DataSourceFactory;
import liquibase.exception.LiquibaseException;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

import static gov.ca.cwds.jobs.common.mode.DefaultJobMode.INCREMENTAL_LOAD;
import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertEquals;

public class CapUsersJobTest {

  private static final Logger LOGGER = LoggerFactory.getLogger(CapUsersJobTest.class);
  private static final String SCHEMA_NAME = "CWSCMS";

  private static LastRunDirHelper lastRunDirHelper = new LastRunDirHelper("cap_job_temp");
  private LocalDateTimeSavePointContainerService savePointContainerService =
          new LocalDateTimeSavePointContainerService(
                  lastRunDirHelper.getSavepointContainerFolder().toString());

  private DatabaseHelper databaseHelper;

  @Before
  public void init() {

    LOGGER.info("Setup database has been started!!!");
    try {
      Map<String, Object> parameters = new HashMap<>();
      parameters.put("schema.name", SCHEMA_NAME);
      getDataBaseHelper().runScript("liquibase/cws_init_load.xml", parameters, SCHEMA_NAME);
    } catch (LiquibaseException e) {
      LOGGER.error(e.getMessage(), e);
    }
    LOGGER.info("Database setup has been finished!!!");
  }

  @Test
  public void capUsersJobTest() throws IOException, LiquibaseException {
    try {
      lastRunDirHelper.deleteSavePointContainerFolder();
      testInitialLoad();
      testIncrementalLoad();
    } finally {
      lastRunDirHelper.deleteSavePointContainerFolder();
      TestCapUserWriter.reset();
    }
  }

  private void testIncrementalLoad() throws LiquibaseException {
    runJob();
    assertEquals(0, TestCapUserWriter.getItems().size());
    addCwsDataForIncrementalLoad(1);
    runJob();
    assertEquals(4, TestCapUserWriter.getItems().size());
    addCwsDataForIncrementalLoad(2);
    runJob();
    assertEquals(1, TestCapUserWriter.getItems().size());
    addCwsDataForIncrementalLoad(3);
    runJob();
    assertEquals(3, TestCapUserWriter.getItems().size());
    MockedIdmService.capChanges = true;
    runJob();
    assertEquals(2, TestCapUserWriter.getItems().size());
  }


  private void testInitialLoad() {
    LocalDateTime timestampBeforeStart = LocalDateTime.now();
    assertEquals(0, TestCapUserWriter.getItems().size());
    runJob();
    assertEquals(MockedIdmService.NUMBER_OF_USERS, TestCapUserWriter.getItems().size());

    LocalDateTimeSavePointContainer savePointContainer = (LocalDateTimeSavePointContainer) savePointContainerService
            .readSavePointContainer(LocalDateTimeSavePointContainer.class);
    assertTrue(savePointContainer.getSavePoint().getTimestamp().isAfter(timestampBeforeStart));
    assertTrue(savePointContainer.getSavePoint().getTimestamp().isBefore(LocalDateTime.now()));
    assertEquals(INCREMENTAL_LOAD, savePointContainer.getJobMode());
  }

  private void runJob() {
    JobRunner.run(createCapUsersJobModule());
  }

  private void addCwsDataForIncrementalLoad(int i) throws LiquibaseException {
    DateTimeFormatter datetimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");

    Map<String, Object> parameters = new HashMap<>();
    parameters.put("now", datetimeFormatter.format(LocalDateTime.now()));
    String scriptName;
    switch (i) {
      case 1:
        scriptName = "liquibase/cws_userid_changes.xml";
        break;
      case 2:
        scriptName = "liquibase/cws_office_changes.xml";
        break;
      case 3:
        scriptName = "liquibase/cws_staffperson_and_office_changes.xml";
        break;
      default:
        scriptName = "not valid name";
    }
    getDataBaseHelper()
            .runScript(scriptName, parameters, SCHEMA_NAME);
  }

  private DatabaseHelper getDataBaseHelper() {
    if (databaseHelper == null) {
      DataSourceFactory cwsDataSourceFactory = getCapUsersJobConfiguration()
              .getCmsDataSourceFactory();
      databaseHelper = new DatabaseHelper(cwsDataSourceFactory.getUrl(),
              cwsDataSourceFactory.getUser(), cwsDataSourceFactory.getPassword());
    }
    return databaseHelper;
  }

  private CapUsersJobModule createCapUsersJobModule() {
    CapUsersJobModule capUsersJobModule = new CapUsersJobModule(getModuleArgs());

    capUsersJobModule.setIdmService(MockedIdmService.class);
    capUsersJobModule.setElasticSearchModule(new AbstractModule() {
      @Override
      protected void configure() {
        // Do nothing here
      }
    });
    TestCapUserWriter.reset();
    capUsersJobModule.setCapElasticWriterClass(TestCapUserWriter.class);
    return capUsersJobModule;
  }

  private String[] getModuleArgs() {
    return new String[]{"-c", getConfigFilePath(), "-l",
            lastRunDirHelper.getSavepointContainerFolder().toString()};
  }

  private static String getConfigFilePath() {
    return Paths.get("src", "test", "resources", "cap-users-job-test.yaml")
            .normalize().toAbsolutePath().toString();
  }

  private static CapUsersJobConfiguration getCapUsersJobConfiguration() {
    CapUsersJobConfiguration capUsersJobConfiguration =
            MultiThreadJobConfiguration
                .getJobsConfiguration(CapUsersJobConfiguration.class, getConfigFilePath());
    DataSourceFactory dataSourceFactory = capUsersJobConfiguration.getCmsDataSourceFactory();
    dataSourceFactory.setUrl(dataSourceFactory.getProperties().get("hibernate.connection.url"));
    dataSourceFactory
            .setUser(dataSourceFactory.getProperties().get("hibernate.connection.username"));
    dataSourceFactory
            .setPassword(dataSourceFactory.getProperties().get("hibernate.connection.password"));

    return capUsersJobConfiguration;
  }

}
