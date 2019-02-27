package gov.ca.cwds.jobs.common.batch;

import com.google.inject.Inject;
import gov.ca.cwds.jobs.common.elastic.BulkCollector;
import gov.ca.cwds.jobs.common.exception.JobExceptionHandler;
import gov.ca.cwds.jobs.common.exception.JobsException;
import gov.ca.cwds.jobs.common.iterator.JobBatchIterator;
import gov.ca.cwds.jobs.common.mode.JobMode;
import gov.ca.cwds.jobs.common.mode.JobModeFinalizer;
import gov.ca.cwds.jobs.common.savepoint.SavePoint;
import gov.ca.cwds.jobs.common.savepoint.SavePointService;
import gov.ca.cwds.jobs.common.timereport.JobTimeReport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by Alexander Serbin on 4/2/2018.
 */
public class BatchProcessor<E, S extends SavePoint, J extends JobMode> {

  private static final Logger LOGGER = LoggerFactory.getLogger(BatchProcessor.class);

  @Inject
  private BulkCollector<E> elasticSearchBulkCollector;

  @Inject
  private BatchReadersPool<E, S> batchReadersPool;

  @Inject
  private JobBatchIterator<S> jobBatchIterator;

  @Inject
  private JobModeFinalizer jobModeFinalizer;

  @Inject
  private SavePointService<S, J> savePointService;

  public void init() {
    batchReadersPool.init(elasticSearchBulkCollector);
  }

  public void processBatches() {
    JobTimeReport jobTimeReport = new JobTimeReport();
    JobBatch<S> batch = jobBatchIterator.getNextPortion();
    while (!batch.isEmpty()) {
      LOGGER.info("Batch processing, batch size = {}", batch.getSize());
      batchReadersPool.loadEntities(batch.getChangedEntityIdentifiers());
      handleBatchSavepoint(batch);
      batch = jobBatchIterator.getNextPortion();
    }
    jobModeFinalizer.doFinalizeJob();
    jobTimeReport.printTimeSpent();
  }

  private void handleBatchSavepoint(JobBatch<S> batch) {
    S savePoint = savePointService.defineSavepoint(batch);
    LOGGER.info("Last batch in portion save point {}", savePoint);
    if (!JobExceptionHandler.isExceptionHappened()) {
      LOGGER.info("Save point has been reached. Batch save point is {}. Trying to save", savePoint);
      savePointService.saveSavePoint(savePoint);
      if (LOGGER.isInfoEnabled()) {
        //TODO  jobTimeReport.printTimeReport(portionBatchNumber);
      }
    } else {
      LOGGER.error("Exception occured during batch processing. Job has been terminated." +
          " Batch timestamp {} has not been recorded", savePoint);
      throw new JobsException("Exception occured during batch processing");
    }
  }

  public void destroy() {
    batchReadersPool.destroy();
    elasticSearchBulkCollector.destroy();
  }
}