package gov.ca.cwds.jobs.cap.users.service;

import static gov.ca.cwds.jobs.cap.users.CwsCmsDataAccessModule.CWS;

import com.google.inject.Inject;
import gov.ca.cwds.idm.dto.User;
import gov.ca.cwds.jobs.cap.users.dao.CwsUsersDao;
import gov.ca.cwds.jobs.cap.users.dto.ChangedUserDto;
import gov.ca.cwds.jobs.common.RecordChangeOperation;
import io.dropwizard.hibernate.UnitOfWork;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.apache.shiro.util.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CwsChangedUsersServiceImpl implements CwsChangedUsersService {

  private static final Logger LOGGER = LoggerFactory.getLogger(CwsChangedUsersService.class);

  @Inject
  private CapUsersSavePointService savePointService;

  @Inject
  private IdmService idmService;

  @Inject
  private CwsUsersDao dao;

  @UnitOfWork(CWS)
  public List<ChangedUserDto> getCwsChanges() {
    List<String> changedRacfIds = dao.getChangedRacfIds(savePointService.loadSavePoint());
    if (CollectionUtils.isEmpty(changedRacfIds)) {
      LOGGER.info("No changes in CWS/CMS found");
      return Collections.emptyList();
    }
    LOGGER.info("The number of RACFIDs with changed data: {}", changedRacfIds.size());
    changedRacfIds = changedRacfIds.stream().map(String::trim).collect(Collectors.toList());
    List<User> users = idmService.getUsersByRacfIds(changedRacfIds);
    return users.stream()
        .map(e -> new ChangedUserDto(e, RecordChangeOperation.U))
        .collect(Collectors.toList());
  }

}
