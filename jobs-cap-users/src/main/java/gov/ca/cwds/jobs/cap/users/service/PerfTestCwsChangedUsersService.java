package gov.ca.cwds.jobs.cap.users.service;

import static gov.ca.cwds.jobs.cap.users.CwsCmsDataAccessModule.CWS;

import com.google.inject.Inject;
import gov.ca.cwds.idm.dto.User;
import gov.ca.cwds.jobs.cap.users.dao.CwsUsersDao;
import gov.ca.cwds.jobs.cap.users.dto.ChangedUserDto;
import gov.ca.cwds.jobs.common.RecordChangeOperation;
import io.dropwizard.hibernate.UnitOfWork;
import java.util.List;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PerfTestCwsChangedUsersService implements CwsChangedUsersService {

  private static final Logger LOGGER = LoggerFactory
      .getLogger(PerfTestCwsChangedUsersService.class);

  @Inject
  private IdmService idmService;

  @Inject
  private CwsUsersDao dao;

  @UnitOfWork(CWS)
  @Override
  public List<ChangedUserDto> getCwsChanges() {
    List<String> allRacfIds = dao.getAllRacfIds();
    LOGGER.info("PERFORMANCE TEST MODE: All {} RACFIDs are used", allRacfIds.size());
    allRacfIds = allRacfIds.stream().map(String::trim).collect(Collectors.toList());
    List<User> users = idmService.getUsersByRacfIds(allRacfIds);
    return users.stream()
        .map(e -> new ChangedUserDto(e, RecordChangeOperation.U))
        .collect(Collectors.toList());
  }
}
