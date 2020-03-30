package gov.ca.cwds.jobs.common.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;

import gov.ca.cwds.jobs.common.batch.BatchProcessor;
import gov.ca.cwds.jobs.common.exception.JobExceptionHandler;
import gov.ca.cwds.jobs.common.savepoint.SavePoint;
import gov.ca.cwds.jobs.common.util.ConsumerCounter;

/**
 * Created by Alexander Serbin on 3/5/2018.
 */
public class JobImpl<E, S extends SavePoint> implements Job {

  private static final Logger LOGGER = LoggerFactory.getLogger(JobImpl.class);

  @Inject
  private BatchProcessor<E, S> batchProcessor;

  @Inject
  private JobPreparator jobPreparator;

  @Override
  public void run() {
    try {
      jobPreparator.run();
      batchProcessor.init();
      batchProcessor.processBatches();
      if (LOGGER.isInfoEnabled()) {
        LOGGER.info("Added {} entities to the job writer", ConsumerCounter.getCounter());
      }
    } finally {
      JobExceptionHandler.reset();
      close();
    }
  }

  @Override
  public void close() {
    batchProcessor.destroy();
  }

}
