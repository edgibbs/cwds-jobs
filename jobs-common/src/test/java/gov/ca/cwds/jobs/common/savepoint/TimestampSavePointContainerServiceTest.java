package gov.ca.cwds.jobs.common.savepoint;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import gov.ca.cwds.jobs.common.mode.JobMode;
import gov.ca.cwds.jobs.common.util.LastRunDirHelper;
import java.io.IOException;
import java.time.LocalDateTime;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * Created by Alexander Serbin on 2/6/2018.
 */
public class TimestampSavePointContainerServiceTest {

  private LastRunDirHelper lastRunDirHelper = new LastRunDirHelper("temp");

  @Test
  public void readWriteTimestampTest() throws Exception {
    LocalDateTime timestamp = LocalDateTime.of(2018, 2, 6, 4, 14, 20, 50);
    LocalDateTimeSavePointContainerService savePointContainerService = new LocalDateTimeSavePointContainerService(
        lastRunDirHelper.getSavepointContainerFolder().toString());
    assertFalse(savePointContainerService.savePointContainerExists());
    SavePointContainer<LocalDateTimeSavePoint> savePointContainer =
        new LocalDateTimeSavePointContainer();
    savePointContainer.setJobMode(JobMode.INITIAL_LOAD);
    savePointContainer.setSavePoint(new LocalDateTimeSavePoint(timestamp));
    savePointContainerService.writeSavePointContainer(savePointContainer);
    assertEquals(savePointContainer,
        savePointContainerService.readSavePointContainer(LocalDateTimeSavePointContainer.class));

    assertTrue(savePointContainerService.savePointContainerExists());
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