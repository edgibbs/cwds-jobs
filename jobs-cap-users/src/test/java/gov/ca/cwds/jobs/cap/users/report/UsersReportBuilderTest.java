package gov.ca.cwds.jobs.cap.users.report;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import org.junit.Test;

public class UsersReportBuilderTest {

  @Test
  public void testBuildHeader() {
    UsersReportBuilder usersReportBuilder = new UsersReportBuilder();
    assertThat(usersReportBuilder.buildHeader(),
        is("Name, Role, Permissions, County, RACFID, Email, Status\n"));
  }
}
