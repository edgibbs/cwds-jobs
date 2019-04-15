package gov.ca.cwds.jobs.common.timereport;

import gov.ca.cwds.jobs.common.util.TimeSpentUtil;
import java.time.LocalDateTime;

/**
 * Created by Alexander Serbin on 3/19/2018.
 */
public class JobTimeReport {

  private LocalDateTime jobStartTime;


  public JobTimeReport() {
    this.jobStartTime = LocalDateTime.now();
  }

  public void printTimeSpent() {
    TimeSpentUtil.printTimeSpent("Overall batch processing", jobStartTime);
  }

  public float getCompletionPercent(int finishedBatchNumber) {
    return 0;
  }
}
