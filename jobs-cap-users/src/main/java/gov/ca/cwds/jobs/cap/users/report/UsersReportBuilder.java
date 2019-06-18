package gov.ca.cwds.jobs.cap.users.report;

import static gov.ca.cwds.jobs.cap.users.report.Utils.emptyIfNull;
import static java.util.stream.Collectors.toList;

import gov.ca.cwds.idm.dto.User;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.function.Function;

public class UsersReportBuilder {

  private List<Column> columns = new LinkedList<>();

  public UsersReportBuilder() {
    addColumn("Name", user -> {
      String firstName = emptyIfNull(user.getFirstName());
      String lastName = emptyIfNull(user.getLastName());
      return String.join(" ", firstName, lastName);
    });
    addColumn("Role", user -> getMultiValueProperty(user.getRoles()));
    addColumn("Permissions", user -> getMultiValueProperty(user.getPermissions()));
    addColumn("County", User::getCountyName);
    addColumn("RACFID", User::getRacfid);
    addColumn("Email", User::getEmail);
    addColumn("Status", User::getStatus);
    addColumn("Enabled", user -> {
      Boolean enabled = user.getEnabled();
      return enabled != null ? Boolean.toString(enabled) : "";
    });
  }

  private void addColumn(String name, Function<User, String> cellFunction) {
    columns.add(new Column(name, cellFunction));
  }

  private String getMultiValueProperty(Set<String> values) {
    return String.join(":", values);
  }

  public String buildHeader() {
    return String.join(", ", columns.stream().map(Column::getName).collect(toList())) + "\n";
  }

  public String buildRow(User user) {
    return String.join(", ", columns.stream().map(c -> c.getCellValue(user)).collect(toList()))
        + "\n";
  }
}
