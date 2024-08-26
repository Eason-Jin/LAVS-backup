package uoa.lavs.dataoperations.customer;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import uoa.lavs.LocalInstance;
import uoa.lavs.mainframe.Status;
import uoa.lavs.mainframe.messages.customer.UpdateCustomerEmail;
import uoa.lavs.models.Email;

public class EmailUpdater {

  private static boolean failed = false;

  public static void updateData(String customerID, Email email) {
    try {
      updateMainframe(customerID, email);
    } catch (Exception e) {
      System.out.println("Mainframe update failed: " + e.getMessage());
      failed = true;
    } finally {
      try {
        updateDatabase(customerID, email);
      } catch (Exception e) {
        System.out.println("Database update failed: " + e.getMessage());
      } finally {
        if (failed) {
          addFailedUpdate(customerID, email.getNumber());
        } else {
          addInMainframe(customerID, email.getNumber());
        }
      }
    }
  }

  public static Integer updateMainframe(String customerID, Email email) throws Exception {
    UpdateCustomerEmail updateCustomerEmail = new UpdateCustomerEmail();
    updateCustomerEmail.setCustomerId(customerID);
    Email existingEmail = null;

    if (email.getNumber() != null) {
      List<Email> existingEmails = null;
      try {
        existingEmails = EmailFinder.findFromMainframe(customerID);
        for (Email emailOnAccount : existingEmails) {
          if (emailOnAccount.getNumber().equals(email.getNumber())) {
            existingEmail = emailOnAccount;
            break;
          }
          updateCustomerEmail.setNumber(null);
        }
      } catch (Exception e) {
        updateCustomerEmail.setNumber(null);
        System.out.println("Email %s not in mainframe: " + e.getMessage());
      }
    }

    if (existingEmail != null) {
      updateCustomerEmail.setNumber(email.getNumber());
      updateCustomerEmail.setAddress(
          email.getAddress() != null ? email.getAddress() : existingEmail.getAddress());
      updateCustomerEmail.setIsPrimary(
          email.getIsPrimary() != null ? email.getIsPrimary() : existingEmail.getIsPrimary());
    } else {
      updateCustomerEmail.setAddress(email.getAddress());
      updateCustomerEmail.setIsPrimary(email.getIsPrimary());
    }

    Status status = updateCustomerEmail.send(LocalInstance.getConnection());
    if (!status.getWasSuccessful()) {
      failed = true;
      System.out.println(
          "Something went wrong - the Mainframe send failed! The code is " + status.getErrorCode());
      throw new Exception("Mainframe send failed");
    }
    email.setNumber(updateCustomerEmail.getNumberFromServer());
    return updateCustomerEmail.getNumberFromServer();
  }

  public static void updateDatabase(String customerID, Email email) throws SQLException {
    boolean exists = false;
    String CHECK_SQL = "SELECT COUNT(*) FROM Email WHERE CustomerID = ? AND Number = ?";

    if (customerID != null && email.getNumber() != null) {
      try (Connection connection = LocalInstance.getDatabaseConnection();
          PreparedStatement checkStatement = connection.prepareStatement(CHECK_SQL)) {
        checkStatement.setString(1, customerID);
        checkStatement.setInt(2, email.getNumber());
        try (ResultSet resultSet = checkStatement.executeQuery()) {
          if (resultSet.next()) {
            exists = resultSet.getInt(1) > 0;
          }
        }
      }
    }

    String sql;
    if (exists) {
      sql = "UPDATE Email SET "
          + "Address = COALESCE(?, Address), "
          + "IsPrimary = COALESCE(?, IsPrimary) "
          + "WHERE CustomerID = ? AND Number = ?";
    } else {
      if (email.getNumber() == null) {
        String GET_MAX_NUMBER_SQL = "SELECT COALESCE(MAX(Number), 0) + 1 FROM Email WHERE CustomerID = ?";
        try (Connection connection = LocalInstance.getDatabaseConnection();
            PreparedStatement getMaxNumberStatement = connection.prepareStatement(GET_MAX_NUMBER_SQL)) {
          getMaxNumberStatement.setString(1, customerID);
          try (ResultSet resultSet = getMaxNumberStatement.executeQuery()) {
            if (resultSet.next()) {
              email.setNumber(resultSet.getInt(1));
            }
          }
        }
      }
      sql = "INSERT INTO Email ( Address, IsPrimary, CustomerID, Number) VALUES (?, ?, ?, ?)";
    }

    try (Connection connection = LocalInstance.getDatabaseConnection();
        PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

      statement.setString(1, email.getAddress());
      statement.setBoolean(2, email.getIsPrimary());
      statement.setString(3, customerID);
      statement.setInt(4, email.getNumber());

      statement.executeUpdate();
    }
  }

  private static void addFailedUpdate(String customerID, Integer number) {
    String sql = "UPDATE Email SET InMainframe = false WHERE CustomerID = ? AND Number = ?";
    try (Connection connection = LocalInstance.getDatabaseConnection();
        PreparedStatement statement = connection.prepareStatement(sql)) {
      statement.setString(1, customerID);
      statement.setInt(2, number);
      statement.executeUpdate();
    } catch (SQLException e) {
      System.out.println("Failed to update database: " + e.getMessage());
    }
  }

  private static void addInMainframe(String customerID, Integer number) {
    String sql = "UPDATE Email SET InMainframe = true WHERE CustomerID = ? AND Number = ?";
    try (Connection connection = LocalInstance.getDatabaseConnection();
        PreparedStatement statement = connection.prepareStatement(sql)) {
      statement.setString(1, customerID);
      statement.setInt(2, number);
      statement.executeUpdate();
    } catch (SQLException e) {
      System.out.println("Failed to update database: " + e.getMessage());
    }
  }

  public static List<Email> getFailedUpdates() {
    List<Email> failedUpdates = new ArrayList<>();
    String sql = "SELECT CustomerID, Number FROM Email WHERE InMainframe = false";
    try (Connection connection = LocalInstance.getDatabaseConnection();
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery(sql)) {
      while (resultSet.next()) {
        String customerID = resultSet.getString("CustomerID");
        Integer number = resultSet.getInt("Number");
        List<Email> emails;
        emails = EmailFinder.findFromDatabase(customerID);
        for (Email emailOnAccount : emails) {
          if (emailOnAccount.getNumber().equals(number)) {
            failedUpdates.add(emailOnAccount);
          }
        }
      }
    } catch (Exception e) {
      System.out.println("Failed to get failed updates: " + e.getMessage());
    }
    return failedUpdates;
  }

  public static void retryFailedUpdates() throws Exception {
    List<Email> failedUpdates = getFailedUpdates();
    for (Email email : failedUpdates) {
      String customerID = email.getCustomerId();
      Integer number = email.getNumber();
      updateMainframe(customerID, email);
      addInMainframe(customerID, number);
    }
  }
}
