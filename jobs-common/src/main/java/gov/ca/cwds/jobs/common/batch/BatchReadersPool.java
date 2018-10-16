package gov.ca.cwds.jobs.common.batch;

import com.google.inject.Inject;
import gov.ca.cwds.jobs.common.elastic.BulkCollector;
import gov.ca.cwds.jobs.common.entity.ChangedEntityService;
import gov.ca.cwds.jobs.common.exception.JobsException;
import gov.ca.cwds.jobs.common.identifier.ChangedEntityIdentifier;
import gov.ca.cwds.jobs.common.inject.ReaderThreadsCount;
import gov.ca.cwds.jobs.common.savepoint.SavePoint;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by Alexander Serbin on 3/16/2018.
 */
public class BatchReadersPool<E, S extends SavePoint> {

  private static final Logger LOGGER = LoggerFactory.getLogger(BatchReadersPool.class);

  @Inject
  @ReaderThreadsCount
  private int readersThreadsCount;

  @Inject
  private ChangedEntityService<E> changedEntityService;

  private ExecutorService executorService;

  private BulkCollector<E> elasticSearchBulkCollector;

  public void init(BulkCollector<E> elasticSearchBulkCollector) {
    this.elasticSearchBulkCollector = elasticSearchBulkCollector;
    if (this.executorService != null) {
      this.executorService.shutdown();
    }
    this.executorService = Executors.newFixedThreadPool(readersThreadsCount);
  }

  public void loadEntities(List<ChangedEntityIdentifier<S>> changedEntityIdentifiers) {
    List<Future> futures = changedEntityIdentifiers.parallelStream().
        map(identifier -> (Runnable) () -> elasticSearchBulkCollector.addEntity(
            changedEntityService.loadEntity(identifier)))
        .map(executorService::submit)
        .collect(Collectors.toList());
    for (Future future : futures) {
      try {
        future.get();
      } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
        throw new JobsException("Can't load entities", e);
      } catch (ExecutionException e) {
        throw new JobsException("Can't load entities", e);
      }
    }
    elasticSearchBulkCollector.flush();
  }

  public void destroy() {
    if (executorService != null) {
      executorService.shutdown();
      try {
        executorService.awaitTermination(1, TimeUnit.MINUTES);
      } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
        LOGGER.error("Can't properly shutdown readers pool", e);
        throw new JobsException("Can't properly shutdown readers pool", e);
      }
    }
  }

}
