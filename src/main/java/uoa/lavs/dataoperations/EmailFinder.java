package uoa.lavs.dataoperations;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import uoa.lavs.mainframe.Instance;
import uoa.lavs.mainframe.Status;
import uoa.lavs.mainframe.messages.customer.FindCustomerEmail;
import uoa.lavs.models.Email;

public class EmailFinder {

  public static List<Email> findData(String customerId) {
    List<Email> emails = new ArrayList<>();
    try {
      emails = findFromDatabase(customerId);
    } catch (Exception e) {
      System.out.println("Database find failed: " + e.getMessage());
      System.out.println("Trying to find from mainframe");
      try {
        emails = findFromMainframe(customerId);
      } catch (Exception e1) {
        System.out.println("Mainframe find failed: " + e1.getMessage());
      }
    }
    return emails;
  }

  private static List<Email> findFromMainframe(String customerId) throws Exception {
    FindCustomerEmail findCustomerEmail = new FindCustomerEmail();
    findCustomerEmail.setCustomerId(customerId);
    Status status = findCustomerEmail.send(Instance.getConnection());
    if (!status.getWasSuccessful()) {
      System.out.println(
          "Something went wrong - the Mainframe send failed! The code is " + status.getErrorCode());
      throw new Exception("Mainframe send failed");
    }
    Integer emailCount = findCustomerEmail.getCountFromServer();
    if (emailCount == 0) {
      throw new Exception("Email not found in mainframe");
    }
    List<Email> emails = new ArrayList<>(emailCount);

    for (int i = 1; i <= emailCount; i++) {
      Email email = new Email();
      email.setCustomerId(customerId);
      email.setNumber(findCustomerEmail.getNumberFromServer(i));
      email.setAddress(findCustomerEmail.getAddressFromServer(i));
      email.setIsPrimary(findCustomerEmail.getIsPrimaryFromServer(i));
      emails.add(email);
    }
    return emails;
  }

  private static List<Email> findFromDatabase(String customerId) throws Exception {
    List<Email> emails = new ArrayList<>();
    Connection connection = Instance.getDatabaseConnection();
    Statement statement = connection.createStatement();
    String query = "SELECT * FROM Email WHERE CustomerID LIKE '%" + customerId + "%'";
    ResultSet resultSet = statement.executeQuery(query);
    while (resultSet.next()) {
      Email email = new Email();
      email.setCustomerId(resultSet.getString("CustomerID"));
      email.setNumber(resultSet.getInt("Number"));
      email.setAddress(resultSet.getString("Address"));
      email.setIsPrimary(resultSet.getBoolean("IsPrimary"));
      emails.add(email);
    }
    connection.close();
    if (emails.isEmpty()) {
      throw new Exception("Email not found in database");
    }
    return emails;
  }
}
