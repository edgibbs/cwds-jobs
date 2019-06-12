package gov.ca.cwds.jobs.cap.users.service;

import gov.ca.cwds.jobs.cap.users.dto.ChangedUserDto;
import java.util.List;

public interface CwsChangedUsersService {

  List<ChangedUserDto> getCwsChanges();

}
