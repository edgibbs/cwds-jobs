package gov.ca.cwds.jobs.cap.users.report;

import static gov.ca.cwds.config.api.idm.Roles.CWS_WORKER;
import static gov.ca.cwds.util.Utils.toSet;
import static org.junit.Assert.assertEquals;

import gov.ca.cwds.idm.dto.User;
import gov.ca.cwds.jobs.cap.users.dto.ChangedUserDto;
import gov.ca.cwds.jobs.common.RecordChangeOperation;
import java.util.Arrays;
import java.util.List;
import org.junit.Before;
import org.junit.Test;

public class UsersReportBuilderTest {

  private UsersReportBuilder usersReportBuilder;

  @Before
  public void before() {
    usersReportBuilder = new UsersReportBuilder();
  }

  @Test
  public void testBuildHeader() {
    assertEquals(
        "Name, Role, Permissions, County, RACFID, Email, Status, Enabled\n",
        usersReportBuilder.buildHeader());
  }

  @Test
  public void testEmptyRow() {
    assertEquals(" , , , , , , , \n", usersReportBuilder.buildRow(new User()));
  }

  @Test
  public void testFullRow() {
    User user = new User();
    user.setFirstName("FirstName");
    user.setLastName("LastName");
    user.setRoles(toSet(CWS_WORKER));
    user.setPermissions(toSet("Snapshot-rollout", "Facility-search-rollout"));
    user.setCountyName("Madera");
    user.setRacfid("IDRACF");
    user.setEmail("some.email@gmail.com");
    user.setStatus("CONFIRMED");
    user.setEnabled(Boolean.TRUE);

    assertEquals(
        "FirstName LastName, CWS-worker, Snapshot-rollout:Facility-search-rollout, Madera, "
            + "IDRACF, some.email@gmail.com, CONFIRMED, true\n",
        usersReportBuilder.buildRow(user));
  }

  @Test
  public void testBuildRows() {
    User user0 = new User();
    user0.setFirstName("First");
    ChangedUserDto dto0 = dto(user0);

    User user1 = new User();
    user1.setFirstName("Second");
    ChangedUserDto dto1 = dto(user1);

    List<ChangedUserDto> dtos = Arrays.asList(dto0, dto1);
    assertEquals("First , , , , , , , \nSecond , , , , , , , \n",
        usersReportBuilder.buildRows(dtos));

  }

  private static ChangedUserDto dto(User user) {
    return new ChangedUserDto(user, RecordChangeOperation.I);
  }
}
