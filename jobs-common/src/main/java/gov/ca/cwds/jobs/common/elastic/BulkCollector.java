package gov.ca.cwds.jobs.common.elastic;

import com.google.inject.Inject;
import gov.ca.cwds.jobs.common.BulkWriter;
import gov.ca.cwds.jobs.common.inject.ElasticsearchBulkSize;
import java.util.ArrayList;
import java.util.List;

/**
 * Must be thread-safe. Created by Alexander Serbin on 3/16/2018.
 */
public class BulkCollector<E> {

  @Inject
  private BulkWriter<E> jobWriter;

  @Inject
  @ElasticsearchBulkSize
  private int bulkSize;

  private List<E> entities = new ArrayList<>(bulkSize);

  public synchronized void addEntity(E entity) {
    entities.add(entity);
    if (entities.size() == bulkSize) {
      flush();
    }
  }

  public synchronized void flush() {
    jobWriter.write(entities);
    resetEntities();
  }

  private void resetEntities() {
    entities = new ArrayList<>(bulkSize);
  }

  public void destroy() {
    jobWriter.destroy();
  }

  public synchronized BulkWriter<E> getWriter() {
    return jobWriter;
  }
}
