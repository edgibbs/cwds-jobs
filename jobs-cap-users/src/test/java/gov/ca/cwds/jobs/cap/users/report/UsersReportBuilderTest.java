package gov.ca.cwds.jobs.cap.users.report;

import static org.junit.Assert.assertEquals;

import gov.ca.cwds.idm.dto.User;
import org.junit.Test;

public class UsersReportBuilderTest {

  @Test
  public void testBuildHeader() {
    UsersReportBuilder usersReportBuilder = new UsersReportBuilder();
    assertEquals(
        "Name, Role, Permissions, County, RACFID, Email, Status, Enabled\n",
        usersReportBuilder.buildHeader());
  }

  @Test
  public void testEmptyRow() {
    UsersReportBuilder usersReportBuilder = new UsersReportBuilder();
    assertEquals(" , , , , , , , \n", usersReportBuilder.buildRow(new User()));
  }
}
