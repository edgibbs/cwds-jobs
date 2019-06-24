package gov.ca.cwds.jobs.cap.users.paginator;

import com.google.inject.Inject;
import gov.ca.cwds.idm.dto.UsersPage;
import gov.ca.cwds.jobs.cap.users.service.IdmService;

public class CapUsersInitialJobPaginator {

  @Inject
  private IdmService idmService;

  public UsersPage getNextPage(String paginationToken) {
    return idmService.getUserPage(paginationToken);
  }
}
