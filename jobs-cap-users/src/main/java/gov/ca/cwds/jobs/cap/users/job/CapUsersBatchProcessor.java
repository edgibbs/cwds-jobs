package gov.ca.cwds.jobs.cap.users.job;

import com.google.inject.Inject;
import gov.ca.cwds.jobs.cap.users.dto.ChangedUserDto;
import gov.ca.cwds.jobs.cap.users.iterator.CapUsersInitialJobIterator;
import gov.ca.cwds.jobs.common.elastic.BulkCollector;
import java.util.List;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CapUsersBatchProcessor {

  private static final Logger LOGGER = LoggerFactory.getLogger(CapUsersBatchProcessor.class);

  @Inject
  private CapUsersInitialJobIterator capUsersJobBatchIterator;

  @Inject
  private BulkCollector<ChangedUserDto> elasticSearchBulkCollector;

  public void processBatches() {
    List<ChangedUserDto> portion = capUsersJobBatchIterator.getNextPortion();
    int numberOfProcessedItems = 0;
    while (!CollectionUtils.isEmpty(portion)) {
      numberOfProcessedItems += portion.size();
      loadEntities(portion);
      portion = capUsersJobBatchIterator.getNextPortion();
    }
    elasticSearchBulkCollector.flush();
    LOGGER.info("total number of items processed: {}", numberOfProcessedItems);
  }

  private void loadEntities(List<ChangedUserDto> userList) {
    userList.forEach(elasticSearchBulkCollector::addEntity);
  }

  public void destroy() {
    elasticSearchBulkCollector.destroy();
  }
}
