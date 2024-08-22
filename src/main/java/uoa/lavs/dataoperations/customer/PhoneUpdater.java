package uoa.lavs.dataoperations.customer;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import uoa.lavs.mainframe.Instance;
import uoa.lavs.mainframe.Status;
import uoa.lavs.mainframe.messages.customer.UpdateCustomerPhoneNumber;
import uoa.lavs.models.Phone;

public class PhoneUpdater {

  private static boolean failed = false;

  public static void updateData(String customerID, Phone phone) {
    try {
      updateMainframe(customerID, phone);
    } catch (Exception e) {
      System.out.println("Mainframe update failed: " + e.getMessage());
      failed = true;
    } finally {
      try {
        updateDatabase(customerID, phone);
      } catch (Exception e) {
        System.out.println("Database update failed: " + e.getMessage());
      } finally {
        if (failed) {
          addFailedUpdate(customerID, phone.getNumber());
        } else {
          addInMainframe(customerID, phone.getNumber());
        }
      }
    }
  }

  private static Integer updateMainframe(String customerID, Phone phone) throws Exception {
    UpdateCustomerPhoneNumber updateCustomerPhone = new UpdateCustomerPhoneNumber();
    updateCustomerPhone.setCustomerId(customerID);
    updateCustomerPhone.setNumber(phone.getNumber());
    Phone existingPhone = null;

    if (phone.getNumber() != null) {
      List<Phone> existingPhones = null;
      try {
        existingPhones = PhoneFinder.findData(customerID);
        for (Phone phoneOnAccount : existingPhones) {
          if (phoneOnAccount.getNumber().equals(phone.getNumber())
              && phoneOnAccount.getCustomerId().equals(phone.getCustomerId())) {
            existingPhone = phoneOnAccount;
            break;
          }
        }
      } catch (Exception e) {
        System.out.println("Phone %s not in mainframe: " + e.getMessage());
      }
    }

    if (existingPhone != null) {
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
      failed = true;
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
      try (Connection connection = Instance.getDatabaseConnection();
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
      if (phone.getNumber() == null) {
        String GET_MAX_NUMBER_SQL =
            "SELECT COALESCE(MAX(Number), 0) + 1 FROM Phone WHERE CustomerID = ?";
        try (Connection connection = Instance.getDatabaseConnection();
            PreparedStatement getMaxNumberStatement =
                connection.prepareStatement(GET_MAX_NUMBER_SQL)) {
          getMaxNumberStatement.setString(1, customerID);
          try (ResultSet resultSet = getMaxNumberStatement.executeQuery()) {
            if (resultSet.next()) {
              phone.setNumber(resultSet.getInt(1));
            }
          }
        }
      }
      sql =
          "INSERT INTO Phone (Type, Prefix, PhoneNumber, IsPrimary, CanSendText, CustomerID,"
              + " Number) VALUES (?, ?, ?, ?, ?, ?, ?)";
    }

    try (Connection connection = Instance.getDatabaseConnection();
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

  private static void addFailedUpdate(String customerID, Integer number) {
    String sql = "UPDATE Phone SET InMainframe = false WHERE CustomerID = ? AND Number = ?";
    try (Connection connection = Instance.getDatabaseConnection();
        PreparedStatement statement = connection.prepareStatement(sql)) {
      statement.setString(1, customerID);
      statement.setInt(2, number);
      statement.executeUpdate();
    } catch (SQLException e) {
      System.out.println("Failed to update InMainframe status: " + e.getMessage());
    }
  }

  private static void addInMainframe(String customerID, Integer number) {
    String sql = "UPDATE Phone SET InMainframe = true WHERE CustomerID = ? AND Number = ?";
    try (Connection connection = Instance.getDatabaseConnection();
        PreparedStatement statement = connection.prepareStatement(sql)) {
      statement.setString(1, customerID);
      statement.setInt(2, number);
      statement.executeUpdate();
    } catch (SQLException e) {
      System.out.println("Failed to update InMainframe status: " + e.getMessage());
    }
  }

  public static List<Phone> getFailedUpdates() {
    List<Phone> failedUpdates = new ArrayList<>();
    String sql = "SELECT CustomerID, Number FROM Phone WHERE InMainframe = false";
    try (Connection connection = Instance.getDatabaseConnection();
        PreparedStatement statement = connection.prepareStatement(sql);
        ResultSet resultSet = statement.executeQuery()) {
      while (resultSet.next()) {
        String customerID = resultSet.getString(1);
        Integer number = resultSet.getInt(2);
        List<Phone> phones;
        try {
          phones = PhoneFinder.findFromDatabase(customerID);
          for (Phone phoneOnAccount : phones) {
            if (phoneOnAccount.getNumber().equals(number)
                && phoneOnAccount.getCustomerId().equals(customerID)) {
              failedUpdates.add(phoneOnAccount);
              break;
            }
          }
        } catch (Exception e) {
          System.out.println("Failed to get failed updates: " + e.getMessage());
        }
      }
    } catch (SQLException e) {
      System.out.println("Failed to get failed updates: " + e.getMessage());
    }
    return failedUpdates;
  }

  public static void retryFailedUpdates() throws Exception {
    List<Phone> failedUpdates = getFailedUpdates();
    for (Phone phone : failedUpdates) {
      String customerID = phone.getCustomerId();
      Integer number = phone.getNumber();
        updateMainframe(customerID, phone);
        addInMainframe(customerID, number);
    }
  }
}
