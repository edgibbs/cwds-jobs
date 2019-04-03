package gov.ca.cwds.jobs.common.mode;

import static gov.ca.cwds.jobs.common.mode.JobMode.INCREMENTAL_LOAD;
import static gov.ca.cwds.jobs.common.mode.JobMode.INITIAL_LOAD;
import static org.junit.Assert.assertEquals;

import gov.ca.cwds.jobs.common.savepoint.LocalDateTimeSavePoint;
import gov.ca.cwds.jobs.common.savepoint.LocalDateTimeSavePointContainer;
import gov.ca.cwds.jobs.common.savepoint.LocalDateTimeSavePointContainerService;
import gov.ca.cwds.jobs.common.util.LastRunDirHelper;
import java.io.IOException;
import java.time.LocalDateTime;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * Created by Alexander Serbin on 6/20/2018.
 */
public class DefaultJobModeServiceTest {

  private LastRunDirHelper lastRunDirHelper = new LastRunDirHelper("temp");

  private LocalDateTimeSavePointContainerService savePointContainerService = new LocalDateTimeSavePointContainerService(
      lastRunDirHelper.getSavepointContainerFolder().toString());

  private LocalDateTimeSavePointContainer savePointContainer = new LocalDateTimeSavePointContainer();

  private LocalDateTimeJobModeService defaultJobModeService = new LocalDateTimeJobModeService();

  {
    defaultJobModeService.setSavePointContainerService(savePointContainerService);
  }

  @Test
  public void getInitialJobModeTest() throws Exception {
    assertEquals(INITIAL_LOAD, defaultJobModeService.getCurrentJobMode());
  }

  @Test
  public void getInitialResumeJobModeTest() throws Exception {
    savePointContainer.setJobMode(INITIAL_LOAD);
    savePointContainer.setSavePoint(new LocalDateTimeSavePoint(LocalDateTime.of(2017, 1, 1, 5, 4)));
    savePointContainerService.writeSavePointContainer(savePointContainer);
    assertEquals(INITIAL_LOAD, defaultJobModeService.getCurrentJobMode());
  }

  @Test
  public void getIncrementalJobModeTest() throws Exception {
    savePointContainer.setJobMode(INCREMENTAL_LOAD);
    savePointContainer.setSavePoint(new LocalDateTimeSavePoint(LocalDateTime.now()));
    savePointContainerService.writeSavePointContainer(savePointContainer);
    assertEquals(INCREMENTAL_LOAD, defaultJobModeService.getCurrentJobMode());
  }

  @Before
  public void beforeMethod() throws IOException {
    lastRunDirHelper.createSavePointContainerFolder();
  }

  @After
  public void afterMethod() throws IOException {
    lastRunDirHelper.deleteSavePointContainerFolder();
  }

}