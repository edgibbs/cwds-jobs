package gov.ca.cwds.jobs.common.timereport;

import static org.junit.Assert.assertEquals;

import gov.ca.cwds.jobs.common.RecordChangeOperation;
import gov.ca.cwds.jobs.common.identifier.ChangedEntityIdentifier;
import gov.ca.cwds.jobs.common.identifier.TimestampIdentifier;
import gov.ca.cwds.jobs.common.savepoint.LocalDateTimeSavePoint;
import gov.ca.cwds.jobs.common.savepoint.TimestampSavePoint;
import java.time.LocalDateTime;
import org.junit.Ignore;
import org.junit.Test;

/**
 * Created by Alexander Serbin on 3/19/2018.
 */
@Ignore
public class JobTimeReportTest {

  @Test
  public void completionPercentTest() throws InterruptedException {
    ChangedEntityIdentifier<TimestampSavePoint<LocalDateTime>> id = new TimestampIdentifier<>("1",
        RecordChangeOperation.I,
        new LocalDateTimeSavePoint(null));
    JobTimeReport jobTimeReport = new JobTimeReport();
    assertEquals(50, jobTimeReport.getCompletionPercent(0), 0.0000001);
  }
}
