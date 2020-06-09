package gov.ca.cwds.jobs.common.timereport;

import java.time.LocalDateTime;

import gov.ca.cwds.jobs.common.util.TimeSpentUtil;

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

  public float getCompletionPercent() {
    return 0;
  }

}
