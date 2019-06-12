package gov.ca.cwds.jobs.cap.users.job;

import com.google.inject.Inject;
import gov.ca.cwds.jobs.cap.users.dto.CapJobResult;
import gov.ca.cwds.jobs.cap.users.dto.ChangedUserDto;
import gov.ca.cwds.jobs.cap.users.service.CapChangedUsersService;
import gov.ca.cwds.jobs.cap.users.service.CwsChangedUsersService;
import gov.ca.cwds.jobs.cap.users.service.exception.IdmServiceException;
import gov.ca.cwds.jobs.common.elastic.BulkCollector;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CapUsersIncrementalUpdatesProcessor {
  private static final Logger LOGGER = LoggerFactory.getLogger(CapUsersIncrementalUpdatesProcessor.class);

  @Inject
  private BulkCollector<ChangedUserDto> elasticSearchBulkCollector;

  @Inject
  private CwsChangedUsersService cwsChangedUsersService;

  @Inject
  private CapChangedUsersService capChangedUsersService;

  public CapJobResult processUpdates() {
    CapJobResult jobResult = new CapJobResult(true, true);
    try {
      loadEntities(cwsChangedUsersService.getCwsChanges());
    } catch (IdmServiceException e) {
      LOGGER.error(e.getMessage(), e);
      jobResult.setCwsPartSuccess(false);
    }
    try {
      loadEntities(capChangedUsersService.getCapChanges());
    } catch (IdmServiceException e) {
      LOGGER.error(e.getMessage(), e);
      jobResult.setCapPartSuccess(false);
    }
    elasticSearchBulkCollector.flush();
    return jobResult;
  }

  private void loadEntities(List<ChangedUserDto> userList) {
    userList.forEach(elasticSearchBulkCollector::addEntity);
  }

  public void destroy() {
    elasticSearchBulkCollector.destroy();
  }
}
