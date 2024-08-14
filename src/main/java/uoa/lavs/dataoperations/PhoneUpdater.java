package uoa.lavs.dataoperations;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import uoa.lavs.mainframe.Instance;
import uoa.lavs.mainframe.Status;
import uoa.lavs.mainframe.messages.customer.UpdateCustomerPhoneNumber;
import uoa.lavs.models.Phone;

public class PhoneUpdater {

  public static void updateData(String customerID, Phone phone) {
    try {
      updateMainframe(customerID, phone);
    } catch (Exception e) {
      System.out.println("Mainframe update failed: " + e.getMessage());
    } finally {
      try {
        updateDatabase(customerID, phone);
      } catch (Exception e) {
        System.out.println("Database update failed: " + e.getMessage());
      }
    }
  }

  private static Integer updateMainframe(String customerID, Phone phone) throws Exception {
    UpdateCustomerPhoneNumber updateCustomerPhone = new UpdateCustomerPhoneNumber();
    updateCustomerPhone.setCustomerId(customerID);
    updateCustomerPhone.setNumber(phone.getNumber());

    if (phone.getNumber() != null) {
      Phone existingPhone = PhoneLoader.loadData(customerID, phone.getNumber());

      updateCustomerPhone.setType(
          phone.getType() != null ? phone.getType() : existingPhone.getType());
      updateCustomerPhone.setPrefix(
          phone.getPrefix() != null ? phone.getPrefix() : existingPhone.getPrefix());
      updateCustomerPhone.setPhoneNumber(
          phone.getPhoneNumber() != null ? phone.getPhoneNumber() : existingPhone.getPhoneNumber());
      updateCustomerPhone.setIsPrimary(
          phone.getIsPrimary() != null ? phone.getIsPrimary() : existingPhone.getIsPrimary());
      updateCustomerPhone.setCanSendTxt(
          phone.getCanSendText() != null ? phone.getCanSendText() : existingPhone.getCanSendText());
    } else {
      updateCustomerPhone.setType(phone.getType());
      updateCustomerPhone.setPrefix(phone.getPrefix());
      updateCustomerPhone.setPhoneNumber(phone.getPhoneNumber());
      updateCustomerPhone.setIsPrimary(phone.getIsPrimary());
      updateCustomerPhone.setCanSendTxt(phone.getCanSendText());
    }

    Status status = updateCustomerPhone.send(Instance.getConnection());
    if (!status.getWasSuccessful()) {
      System.out.println(
          "Something went wrong - the Mainframe send failed! The code is " + status.getErrorCode());
      throw new Exception("Mainframe send failed");
    }
    phone.setNumber(updateCustomerPhone.getNumberFromServer());
    return updateCustomerPhone.getNumberFromServer();
  }

  private static void updateDatabase(String customerID, Phone phone) throws SQLException {
    boolean exists = false;
    String CHECK_SQL = "SELECT COUNT(*) FROM Phone WHERE CustomerID = ? AND Number = ?";

    if (customerID != null && phone.getNumber() != null) {
      try (Connection connection = DriverManager.getConnection("jdbc:sqlite:database.sqlite");
          PreparedStatement checkStatement = connection.prepareStatement(CHECK_SQL)) {
        checkStatement.setString(1, customerID);
        checkStatement.setInt(2, phone.getNumber());
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
          "UPDATE Phone SET "
              + "Type = COALESCE(?, Type), "
              + "Prefix = COALESCE(?, Prefix), "
              + "PhoneNumber = COALESCE(?, PhoneNumber), "
              + "IsPrimary = COALESCE(?, IsPrimary), "
              + "CanSendText = COALESCE(?, CanSendText) "
              + "WHERE CustomerID = ? AND Number = ?";
    } else {
      sql =
          "INSERT INTO Phone (Type, Prefix, PhoneNumber, IsPrimary, CanSendText, CustomerID,"
              + " Number) VALUES (?, ?, ?, ?, ?, ?, ?)";
    }

    try (Connection connection = DriverManager.getConnection("jdbc:sqlite:database.sqlite");
        PreparedStatement statement =
            connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

      statement.setString(1, phone.getType());
      statement.setString(2, phone.getPrefix());
      statement.setString(3, phone.getPhoneNumber());
      statement.setBoolean(4, phone.getIsPrimary());
      statement.setBoolean(5, phone.getCanSendText());
      statement.setString(6, customerID);
      statement.setInt(7, phone.getNumber());

      statement.executeUpdate();
    }
  }
}
