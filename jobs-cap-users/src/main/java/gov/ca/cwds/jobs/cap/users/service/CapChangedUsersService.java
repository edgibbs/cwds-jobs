package gov.ca.cwds.jobs.cap.users.service;

import com.google.inject.Inject;
import gov.ca.cwds.idm.dto.UserAndOperation;
import gov.ca.cwds.idm.persistence.ns.OperationType;
import gov.ca.cwds.jobs.cap.users.dto.ChangedUserDto;
import gov.ca.cwds.jobs.common.RecordChangeOperation;
import gov.ca.cwds.jobs.common.savepoint.LocalDateTimeSavePointService;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CapChangedUsersService {

  private static final Logger LOGGER = LoggerFactory.getLogger(CapChangedUsersService.class);

  @Inject
  private LocalDateTimeSavePointService savePointService;

  @Inject
  private IdmService idmService;

  public List<ChangedUserDto> getCapChanges() {
    LocalDateTime savePointTime = savePointService.loadSavePoint().getTimestamp();
    List<UserAndOperation> userAndOperations = idmService.getCapChanges(savePointTime);
    LOGGER.info("the number of CAP changes recieved is: {}", userAndOperations.size());
    return userAndOperations.stream()
        .map(e -> new ChangedUserDto(e.getUser(), transformOperation(e.getOperation())))
        .collect(Collectors.toList());
  }

  private RecordChangeOperation transformOperation(OperationType operation) {
    switch (operation) {
      case CREATE:
        return RecordChangeOperation.I;
      case UPDATE:
        return RecordChangeOperation.U;
      default:
        throw new IllegalArgumentException("Unsupported OperationType: " + operation);
    }
  }
}
