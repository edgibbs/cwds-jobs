package gov.ca.cwds.jobs.cap.users;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

import gov.ca.cwds.jobs.cap.users.dto.CapJobResult;
import gov.ca.cwds.jobs.cap.users.dto.ChangedUserDto;
import gov.ca.cwds.jobs.cap.users.job.CapUsersIncrementalUpdatesProcessor;
import gov.ca.cwds.jobs.cap.users.service.CapChangedUsersService;
import gov.ca.cwds.jobs.cap.users.service.CwsChangedUsersService;
import gov.ca.cwds.jobs.cap.users.service.exception.IdmServiceException;
import gov.ca.cwds.jobs.common.elastic.BulkCollector;
import java.util.Collections;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class CapUsersIncrementalUpdatesProcessorTest {

  @Mock
  private CwsChangedUsersService cwsChangedUsersService;

  @Mock
  private CapChangedUsersService capChangedUsersService;

  @Mock
  private BulkCollector<ChangedUserDto> elasticSearchBulkCollector;

  @InjectMocks
  CapUsersIncrementalUpdatesProcessor updatesProcessor;

  @Test
  public void testWhenEverythingWorksFine() {
    when(cwsChangedUsersService.getCwsChanges()).thenReturn(Collections.emptyList());
    when(capChangedUsersService.getCapChanges()).thenReturn(Collections.emptyList());
    assertEquals(updatesProcessor.processUpdates(), new CapJobResult(true, true));
  }

  @Test
  public void testWhenCwsChangesRequestFails() {
    when(cwsChangedUsersService.getCwsChanges()).thenThrow(IdmServiceException.class);
    when(capChangedUsersService.getCapChanges()).thenReturn(Collections.emptyList());
    assertEquals(updatesProcessor.processUpdates(), new CapJobResult(false, true));
  }

  @Test
  public void testWhenCapChangesRequestFails() {
    when(cwsChangedUsersService.getCwsChanges()).thenReturn(Collections.emptyList());
    when(capChangedUsersService.getCapChanges()).thenThrow(IdmServiceException.class);
    assertEquals(updatesProcessor.processUpdates(), new CapJobResult(true, false));
  }

  @Test
  public void testWhenBothRequestsFail() {
    when(cwsChangedUsersService.getCwsChanges()).thenThrow(IdmServiceException.class);
    when(capChangedUsersService.getCapChanges()).thenThrow(IdmServiceException.class);
    assertEquals(updatesProcessor.processUpdates(), new CapJobResult(false, false));
  }


}
