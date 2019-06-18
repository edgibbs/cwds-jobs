package gov.ca.cwds.jobs.cap.users.report;

import static gov.ca.cwds.config.api.idm.Roles.CWS_WORKER;
import static gov.ca.cwds.util.Utils.toSet;
import static org.junit.Assert.assertEquals;

import gov.ca.cwds.idm.dto.User;
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
    user.setRacfid("RACFID");
    user.setEmail("some.email@gmail.com");
    user.setStatus("CONFIRMED");
    user.setEnabled(Boolean.TRUE);

    assertEquals(
        "FirstName LastName, CWS-worker, Snapshot-rollout:Facility-search-rollout, Madera, "
            + "RACFID, some.email@gmail.com, CONFIRMED, true\n",
        usersReportBuilder.buildRow(user));
  }
}
