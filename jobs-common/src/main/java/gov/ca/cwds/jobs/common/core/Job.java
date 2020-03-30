package gov.ca.cwds.jobs.common.core;

/**
 * @author CWDS API Team
 */
@FunctionalInterface
public interface Job {

  /**
   * Run the job.
   */
  void run();

  default void close() {
    // Default method
  }

}
