package uoa.lavs.dataoperations.customer;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import uoa.lavs.mainframe.Instance;
import uoa.lavs.mainframe.Status;
import uoa.lavs.mainframe.messages.customer.LoadCustomerEmails;
import uoa.lavs.models.Email;

public class EmailFinder {

  public static List<Email> findData(String customerId) {
    List<Email> emails = new ArrayList<>();
    try {
      emails = findFromMainframe(customerId);
    } catch (Exception e) {
      System.out.println("Mainframe find failed: " + e.getMessage());
      System.out.println("Trying to find from database");
      try {
        emails = findFromDatabase(customerId);
      } catch (Exception e1) {
        System.out.println("Database find failed: " + e1.getMessage());
      }
    }
    return emails;
  }

  private static List<Email> findFromMainframe(String customerId) throws Exception {
    LoadCustomerEmails loadCustomerEmails = new LoadCustomerEmails();
    loadCustomerEmails.setCustomerId(customerId);
    Status status = loadCustomerEmails.send(Instance.getConnection());
    if (!status.getWasSuccessful()) {
      System.out.println(
          "Something went wrong - the Mainframe send failed! The code is " + status.getErrorCode());
      throw new Exception("Mainframe send failed");
    }
    Integer emailCount = loadCustomerEmails.getCountFromServer();
    if (emailCount == 0) {
      throw new Exception("Email not found in mainframe");
    }
    List<Email> emails = new ArrayList<>(emailCount);

    for (int i = 1; i <= emailCount; i++) {
      Email email = new Email();
      email.setCustomerId(customerId);
      email.setNumber(loadCustomerEmails.getNumberFromServer(i));
      email.setAddress(loadCustomerEmails.getAddressFromServer(i));
      email.setIsPrimary(loadCustomerEmails.getIsPrimaryFromServer(i));
      emails.add(email);
    }
    return emails;
  }

  private static List<Email> findFromDatabase(String customerId) throws Exception {
    List<Email> emails = new ArrayList<>();
    Connection connection = null;
    PreparedStatement preparedStatement = null;
    ResultSet resultSet = null;

    try {
      connection = Instance.getDatabaseConnection();
      String query = "SELECT * FROM Email WHERE CustomerID = ?";
      preparedStatement = connection.prepareStatement(query);
      preparedStatement.setString(1, customerId);
      resultSet = preparedStatement.executeQuery();

      while (resultSet.next()) {
        Email email = new Email();
        email.setCustomerId(resultSet.getString("CustomerID"));
        email.setNumber(resultSet.getInt("Number"));
        email.setAddress(resultSet.getString("Address"));
        email.setIsPrimary(resultSet.getBoolean("IsPrimary"));
        emails.add(email);
      }
    } finally {
      connection.close();
    }
    return emails;
  }
}
