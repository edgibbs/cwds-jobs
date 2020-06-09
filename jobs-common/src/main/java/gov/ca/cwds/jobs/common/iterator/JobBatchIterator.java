package gov.ca.cwds.jobs.common.iterator;

import gov.ca.cwds.jobs.common.batch.JobBatch;
import gov.ca.cwds.jobs.common.savepoint.SavePoint;

/**
 * Created by Alexander Serbin on 4/3/2018.
 */
@FunctionalInterface
public interface JobBatchIterator<S extends SavePoint> {

  default void init() {
    // Default method
  }

  /**
   * Iterates over target entities' identifiers.
   */
  JobBatch<S> getNextPortion();

}
