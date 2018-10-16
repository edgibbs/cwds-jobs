package gov.ca.cwds.jobs.common.configuration;

public class MultiThreadJobConfiguration implements JobConfiguration {

  private MultiThreadJobConfiguration multiThread;

  public MultiThreadJobConfiguration getMultiThread() {
    return multiThread;
  }

  public void setMultiThread(MultiThreadJobConfiguration multiThread) {
    this.multiThread = multiThread;
  }

}
