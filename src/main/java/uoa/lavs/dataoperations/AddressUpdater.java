package uoa.lavs.dataoperations;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import uoa.lavs.mainframe.Instance;
import uoa.lavs.mainframe.Status;
import uoa.lavs.mainframe.messages.customer.UpdateCustomerAddress;
import uoa.lavs.models.Address;

public class AddressUpdater implements Updater<Address> {

  @Override
  public void updateData(String customerID, Address address) {
    try {
      updateMainframe(customerID, address);
    } catch (Exception e) {
      System.out.println("Mainframe update failed: " + e.getMessage());
    } finally {
      try {
        updateDatabase(customerID, address);
      } catch (Exception e) {
        System.out.println("Database update failed: " + e.getMessage());
      }
    }
  }

  private Integer updateMainframe(String customerID, Address address) throws Exception {
    UpdateCustomerAddress updateCustomerAddress = new UpdateCustomerAddress();
    updateCustomerAddress.setCustomerId(customerID);
    updateCustomerAddress.setNumber(address.getNumber());

    if (address.getNumber() != null) {
      AddressLoader addressLoader = new AddressLoader();
      Address existingAddress = addressLoader.loadData(customerID, address.getNumber());

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
          address.isPrimary() != null ? address.isPrimary() : existingAddress.isPrimary());
      updateCustomerAddress.setIsMailing(
          address.isMailing() != null ? address.isMailing() : existingAddress.isMailing());
    } else {
      updateCustomerAddress.setType(address.getType());
      updateCustomerAddress.setLine1(address.getLine1());
      updateCustomerAddress.setLine2(address.getLine2());
      updateCustomerAddress.setSuburb(address.getSuburb());
      updateCustomerAddress.setCity(address.getCity());
      updateCustomerAddress.setPostCode(address.getPostCode());
      updateCustomerAddress.setCountry(address.getCountry());
      updateCustomerAddress.setIsPrimary(address.isPrimary());
      updateCustomerAddress.setIsMailing(address.isMailing());
    }

    Status status = updateCustomerAddress.send(Instance.getConnection());
    if (!status.getWasSuccessful()) {
      System.out.println(
          "Something went wrong - the Mainframe send failed! The code is " + status.getErrorCode());
      throw new Exception("Mainframe send failed");
    }
    address.setNumber(updateCustomerAddress.getNumberFromServer());
    return updateCustomerAddress.getNumberFromServer();
  }

  private void updateDatabase(String customerID, Address address) throws SQLException {
    boolean exists = false;
    String CHECK_SQL = "SELECT COUNT(*) FROM Address WHERE CustomerID = ? AND Number = ?";

    if (customerID != null && address.getNumber() != null) {
      try (Connection connection = DriverManager.getConnection("jdbc:sqlite:database.sqlite");
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
      sql =
          "INSERT INTO Address (Type, Line1, Line2, Suburb, City, PostCode, Country, IsPrimary,"
              + " IsMailing, CustomerID, Number) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
    }

    try (Connection connection = DriverManager.getConnection("jdbc:sqlite:database.sqlite");
        PreparedStatement statement =
            connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

      statement.setString(1, address.getType());
      statement.setString(2, address.getLine1());
      statement.setString(3, address.getLine2());
      statement.setString(4, address.getSuburb());
      statement.setString(5, address.getCity());
      statement.setString(6, address.getPostCode());
      statement.setString(7, address.getCountry());
      statement.setBoolean(8, address.isPrimary());
      statement.setBoolean(9, address.isMailing());
      statement.setString(10, customerID);
      statement.setInt(11, address.getNumber());

      statement.executeUpdate();
    }
  }
}
