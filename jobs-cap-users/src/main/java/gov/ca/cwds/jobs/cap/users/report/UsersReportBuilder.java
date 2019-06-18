package gov.ca.cwds.jobs.cap.users.report;

import java.util.LinkedList;
import java.util.List;
import java.util.StringJoiner;

public class UsersReportBuilder {

  private List<Column> columns = new LinkedList<>();

  public UsersReportBuilder() {
    columns.add(new Column("Name"));
    columns.add(new Column("Role"));
    columns.add(new Column("Permissions"));
    columns.add(new Column("County"));
    columns.add(new Column("RACFID"));
    columns.add(new Column("Email"));
    columns.add(new Column("Status"));
  }

  public String buildHeader() {
    StringJoiner joiner = new StringJoiner(", ");
    for(Column column : columns) {
      joiner.add(column.getName());
    }
    return joiner.toString() + "\n";
  }

}
