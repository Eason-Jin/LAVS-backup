package uoa.lavs.dataoperations;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import uoa.lavs.mainframe.Instance;
import uoa.lavs.mainframe.Status;
import uoa.lavs.mainframe.messages.customer.UpdateCustomerEmail;
import uoa.lavs.models.Email;

public class EmailUpdater {

  public static void updateData(String customerID, Email email) {
    try {
      updateMainframe(customerID, email);
    } catch (Exception e) {
      System.out.println("Mainframe update failed: " + e.getMessage());
    } finally {
      try {
        updateDatabase(customerID, email);
      } catch (Exception e) {
        System.out.println("Database update failed: " + e.getMessage());
      }
    }
  }

  private static Integer updateMainframe(String customerID, Email email) throws Exception {
    UpdateCustomerEmail updateCustomerEmail = new UpdateCustomerEmail();
    updateCustomerEmail.setCustomerId(customerID);
    updateCustomerEmail.setNumber(email.getNumber());
    if (email.getNumber() != null) {
      Email existingEmail = EmailLoader.loadData(customerID, email.getNumber());
      updateCustomerEmail.setAddress(
          email.getAddress() != null ? email.getAddress() : existingEmail.getAddress());
      updateCustomerEmail.setIsPrimary(
          email.getIsPrimary() != null ? email.getIsPrimary() : existingEmail.getIsPrimary());
    } else {
      updateCustomerEmail.setAddress(email.getAddress());
      updateCustomerEmail.setIsPrimary(email.getIsPrimary());
    }

    Status status = updateCustomerEmail.send(Instance.getConnection());
    if (!status.getWasSuccessful()) {
      System.out.println(
          "Something went wrong - the Mainframe send failed! The code is " + status.getErrorCode());
      throw new Exception("Mainframe send failed");
    }
    email.setNumber(updateCustomerEmail.getNumberFromServer());
    return updateCustomerEmail.getNumberFromServer();
  }

  private static void updateDatabase(String customerID, Email email) throws SQLException {
    boolean exists = false;
    String CHECK_SQL = "SELECT COUNT(*) FROM Email WHERE CustomerID = ? AND Number = ?";

    if (customerID != null && email.getNumber() != null) {
      try (Connection connection = DriverManager.getConnection("jdbc:sqlite:database.sqlite");
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
      sql =
          "UPDATE Email SET "
              + "Address = COALESCE(?, Address), "
              + "IsPrimary = COALESCE(?, IsPrimary) "
              + "WHERE CustomerID = ? AND Number = ?";
    } else {
      sql = "INSERT INTO Email ( Address, IsPrimary, CustomerID, Number) VALUES (?, ?, ?, ?)";
    }

    try (Connection connection = DriverManager.getConnection("jdbc:sqlite:database.sqlite");
        PreparedStatement statement =
            connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

      statement.setString(1, email.getAddress());
      statement.setBoolean(2, email.getIsPrimary());
      statement.setString(3, customerID);
      statement.setInt(4, email.getNumber());

      statement.executeUpdate();
    }
  }
}
