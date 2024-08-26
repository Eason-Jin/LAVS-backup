package uoa.lavs.dataoperations.customer;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import uoa.lavs.mainframe.Status;
import uoa.lavs.mainframe.messages.customer.UpdateCustomerAddress;
import uoa.lavs.models.Address;
import uoa.lavs.LocalInstance;

public class AddressUpdater {

  private static boolean failed = false;

  public static void updateData(String customerID, Address address) {
    try {
      updateMainframe(customerID, address);
    } catch (Exception e) {
      failed = true;
      System.out.println("Mainframe update failed: " + e.getMessage());
    } finally {
      try {
        updateDatabase(customerID, address);
      } catch (Exception e) {
        System.out.println("Database update failed: " + e.getMessage());
      } finally {
        if (failed) {
          addFailedUpdate(customerID, address.getNumber());
        } else {
          addInMainframe(customerID, address.getNumber());
        }
      }
    }
  }

  public static Integer updateMainframe(String customerID, Address address) throws Exception {
    UpdateCustomerAddress updateCustomerAddress = new UpdateCustomerAddress();
    updateCustomerAddress.setCustomerId(customerID);
    updateCustomerAddress.setNumber(address.getNumber());
    Address existingAddress = null;

    if (address.getNumber() != null) {
      List<Address> existingAddresses = null;
      try {
        existingAddresses = AddressFinder.findFromMainframe(customerID);
        for (Address addressOnAccount : existingAddresses) {
          if (addressOnAccount.getNumber().equals(address.getNumber())) {
            existingAddress = addressOnAccount;
            break;
          }
        }
      } catch (Exception e) {
        updateCustomerAddress.setNumber(null);
        System.out.println("Address %s not in mainframe: " + e.getMessage());
      }
    }

    if (existingAddress != null) {
      updateCustomerAddress.setType(
          address.getType() != null ? address.getType() : existingAddress.getType());
      updateCustomerAddress.setLine1(
          address.getLine1() != null ? address.getLine1() : existingAddress.getLine1());
      updateCustomerAddress.setLine2(
          address.getLine2() != null ? address.getLine2() : existingAddress.getLine2());
      updateCustomerAddress.setSuburb(
          address.getSuburb() != null ? address.getSuburb() : existingAddress.getSuburb());
      updateCustomerAddress.setCity(
          address.getCity() != null ? address.getCity() : existingAddress.getCity());
      updateCustomerAddress.setPostCode(
          address.getPostCode() != null ? address.getPostCode() : existingAddress.getPostCode());
      updateCustomerAddress.setCountry(
          address.getCountry() != null ? address.getCountry() : existingAddress.getCountry());
      updateCustomerAddress.setIsPrimary(
          address.getIsPrimary() != null ? address.getIsPrimary() : existingAddress.getIsPrimary());
      updateCustomerAddress.setIsMailing(
          address.getIsMailing() != null ? address.getIsMailing() : existingAddress.getIsMailing());
    } else {
      updateCustomerAddress.setType(address.getType());
      updateCustomerAddress.setLine1(address.getLine1());
      updateCustomerAddress.setLine2(address.getLine2());
      updateCustomerAddress.setSuburb(address.getSuburb());
      updateCustomerAddress.setCity(address.getCity());
      updateCustomerAddress.setPostCode(address.getPostCode());
      updateCustomerAddress.setCountry(address.getCountry());
      updateCustomerAddress.setIsPrimary(address.getIsPrimary());
      updateCustomerAddress.setIsMailing(address.getIsMailing());
    }

    Status status = updateCustomerAddress.send(LocalInstance.getConnection());
    if (!status.getWasSuccessful()) {
      failed = true;
      System.out.println(
          "Something went wrong - the Mainframe send failed! The code is " + status.getErrorCode());
      throw new Exception("Mainframe send failed");
    }
    address.setNumber(updateCustomerAddress.getNumberFromServer());
    return updateCustomerAddress.getNumberFromServer();
  }

  public static void updateDatabase(String customerID, Address address) throws SQLException {
    boolean exists = false;
    String CHECK_SQL = "SELECT COUNT(*) FROM Address WHERE CustomerID = ? AND Number = ?";

    if (customerID != null && address.getNumber() != null) {
      try (Connection connection = LocalInstance.getDatabaseConnection();
          PreparedStatement checkStatement = connection.prepareStatement(CHECK_SQL)) {
        checkStatement.setString(1, customerID);
        checkStatement.setInt(2, address.getNumber());
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
          "UPDATE Address SET "
              + "Type = COALESCE(?, Type), "
              + "Line1 = COALESCE(?, Line1), "
              + "Line2 = COALESCE(?, Line2), "
              + "Suburb = COALESCE(?, Suburb), "
              + "City = COALESCE(?, City), "
              + "PostCode = COALESCE(?, PostCode), "
              + "Country = COALESCE(?, Country), "
              + "IsPrimary = COALESCE(?, IsPrimary), "
              + "IsMailing = COALESCE(?, IsMailing) "
              + "WHERE CustomerID = ? AND Number = ?";
    } else {
      if (address.getNumber() == null) {
        String GET_MAX_NUMBER_SQL =
            "SELECT COALESCE(MAX(Number), 0) + 1 FROM Address WHERE CustomerID = ?";
        try (Connection connection = LocalInstance.getDatabaseConnection();
            PreparedStatement getMaxNumberStatement =
                connection.prepareStatement(GET_MAX_NUMBER_SQL)) {
          getMaxNumberStatement.setString(1, customerID);
          try (ResultSet resultSet = getMaxNumberStatement.executeQuery()) {
            if (resultSet.next()) {
              address.setNumber(resultSet.getInt(1));
            }
          }
        }
      }
      sql =
          "INSERT INTO Address (Type, Line1, Line2, Suburb, City, PostCode, Country, IsPrimary,"
              + " IsMailing, CustomerID, Number) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
    }

    try (Connection connection = LocalInstance.getDatabaseConnection();
        PreparedStatement statement =
            connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

      statement.setString(1, address.getType());
      statement.setString(2, address.getLine1());
      statement.setString(3, address.getLine2());
      statement.setString(4, address.getSuburb());
      statement.setString(5, address.getCity());
      statement.setString(6, address.getPostCode());
      statement.setString(7, address.getCountry());
      statement.setBoolean(8, address.getIsPrimary());
      statement.setBoolean(9, address.getIsMailing());
      statement.setString(10, customerID);
      statement.setInt(11, address.getNumber());

      statement.executeUpdate();
    }
  }

  private static void addFailedUpdate(String customerID, Integer number) {
    String sql = "UPDATE Address SET InMainframe = false WHERE CustomerID = ? AND Number = ?";
    try (Connection connection = LocalInstance.getDatabaseConnection();
        PreparedStatement statement = connection.prepareStatement(sql)) {
      statement.setString(1, customerID);
      statement.setInt(2, number);
      statement.executeUpdate();
    } catch (SQLException e) {
      System.out.println("Failed to update InMainframe: " + e.getMessage());
    }
  }

  private static void addInMainframe(String customerID, Integer number) {
    String sql = "UPDATE Address SET InMainframe = true WHERE CustomerID = ? AND Number = ?";
    try (Connection connection = LocalInstance.getDatabaseConnection();
        PreparedStatement statement = connection.prepareStatement(sql)) {
      statement.setString(1, customerID);
      statement.setInt(2, number);
      statement.executeUpdate();
    } catch (SQLException e) {
      System.out.println("Failed to update InMainframe: " + e.getMessage());
    }
  }

  public static List<Address> getFailedUpdates() {
    List<Address> failedUpdates = new ArrayList<>();
    String sql = "SELECT CustomerID, Number FROM Address WHERE InMainframe = false";
    try (Connection connection = LocalInstance.getDatabaseConnection();
        PreparedStatement statement = connection.prepareStatement(sql);
        ResultSet resultSet = statement.executeQuery()) {
      while (resultSet.next()) {
        String customerID = resultSet.getString("CustomerID");
        Integer number = resultSet.getInt("Number");
        List<Address> addresses = AddressFinder.findFromDatabase(customerID);
        for (Address addressOnAccount : addresses) {
          if (addressOnAccount.getNumber().equals(number)) {
            failedUpdates.add(addressOnAccount);
          }
        }
      }
    } catch (Exception e) {
      System.out.println("Failed to get failed updates: " + e.getMessage());
    }
    return failedUpdates;
  }

  public static void retryFailedUpdates() throws Exception {
    List<Address> failedUpdates = getFailedUpdates();
    for (Address address : failedUpdates) {
      String customerID = address.getCustomerId();
      System.out.println("Retrying update for customer " + customerID);
      Integer number = address.getNumber();
        updateMainframe(customerID, address);
        addInMainframe(customerID, number);
    }
  }
}
