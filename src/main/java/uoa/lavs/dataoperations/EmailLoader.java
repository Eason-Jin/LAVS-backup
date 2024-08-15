package uoa.lavs.dataoperations;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import uoa.lavs.mainframe.Instance;
import uoa.lavs.mainframe.Status;
import uoa.lavs.mainframe.messages.customer.LoadCustomerEmail;
import uoa.lavs.models.Email;

public class EmailLoader {

  public static Email loadData(String customerId, int number) {
    Email email = new Email();
    try {
      email = loadFromMainframe(customerId, number);
      if (email.getAddress() == null) {
        throw new Exception("Email not in mainframe");
      }
    } catch (Exception e) {
      System.out.println("Mainframe load failed: " + e.getMessage());
      System.out.println("Trying to load from database");
      try {
        email = loadFromDatabase(customerId, number);
      } catch (Exception e1) {
        System.out.println("Database load failed: " + e1.getMessage());
      }
    }
    return email;
  }

  private static Email loadFromDatabase(String customerId, int number) throws Exception {
    Email email = new Email();
    String query = "SELECT * FROM Email WHERE CustomerID = ? AND Number = ?";
    try (Connection connection = Instance.getDatabaseConnection();
        PreparedStatement statement = connection.prepareStatement(query)) {
      statement.setString(1, customerId);
      statement.setInt(2, number);
      try (ResultSet resultSet = statement.executeQuery()) {
        if (!resultSet.next()) {
          throw new Exception("Email not in database");
        }
        email.setCustomerId(resultSet.getString("CustomerID"));
        email.setNumber(resultSet.getInt("Number"));
        email.setAddress(resultSet.getString("Address"));
        email.setIsPrimary(resultSet.getBoolean("IsPrimary"));
      }
    }
    return email;
  }

  private static Email loadFromMainframe(String customerId, int number) throws Exception {
    LoadCustomerEmail loadCustomerEmail = new LoadCustomerEmail();
    loadCustomerEmail.setCustomerId(customerId);
    loadCustomerEmail.setNumber(number);
    Status status = loadCustomerEmail.send(Instance.getConnection());
    if (!status.getWasSuccessful()) {
      System.out.println(
          "Something went wrong - the Mainframe send failed! The code is " + status.getErrorCode());
      throw new Exception("Mainframe send failed");
    }

    Email email = new Email();
    email.setCustomerId(customerId);
    email.setNumber(number);
    email.setAddress(loadCustomerEmail.getAddressFromServer());
    email.setIsPrimary(loadCustomerEmail.getIsPrimaryFromServer());
    return email;
  }
}
