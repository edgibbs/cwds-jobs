package gov.ca.cwds.jobs.cap.users.job;

import com.google.inject.Inject;
import gov.ca.cwds.idm.dto.User;
import gov.ca.cwds.idm.dto.UsersPage;
import gov.ca.cwds.jobs.cap.users.dto.ChangedUserDto;
import gov.ca.cwds.jobs.cap.users.paginator.CapUsersInitialJobPaginator;
import gov.ca.cwds.jobs.common.RecordChangeOperation;
import gov.ca.cwds.jobs.common.elastic.BulkCollector;
import java.util.List;
import java.util.stream.Collectors;
import liquibase.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CapUsersBatchProcessor {

  private static final Logger LOGGER = LoggerFactory.getLogger(CapUsersBatchProcessor.class);

  @Inject
  private CapUsersInitialJobPaginator capUsersJobBatchPaginator;

  @Inject
  private BulkCollector<ChangedUserDto> elasticSearchBulkCollector;

  public void processBatches() {
    int numberOfProcessedItems = 0;
    String paginationToken = null;

    do {
      UsersPage usersPage = capUsersJobBatchPaginator.getNextPage(paginationToken);
      List<User> users = usersPage.getUserList();
      paginationToken = usersPage.getPaginationToken();

      if (users != null) {
        numberOfProcessedItems += usersPage.getUserList().size();
        loadEntities(users);
      }
    } while (StringUtils.isNotEmpty(paginationToken));

    elasticSearchBulkCollector.flush();
    elasticSearchBulkCollector.getWriter().flush();
    LOGGER.info("total number of items processed: {}", numberOfProcessedItems);
  }

  private void loadEntities(List<User> users) {
    List<ChangedUserDto> entitiesList = users.stream()
            .map(u -> new ChangedUserDto(u, RecordChangeOperation.I))
            .collect(Collectors.toList());
    entitiesList.forEach(elasticSearchBulkCollector::addEntity);
  }

  public void destroy() {
    elasticSearchBulkCollector.destroy();
  }
}
