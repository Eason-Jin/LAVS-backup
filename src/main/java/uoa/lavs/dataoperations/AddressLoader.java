package uoa.lavs.dataoperations;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import uoa.lavs.mainframe.Instance;
import uoa.lavs.mainframe.Status;
import uoa.lavs.mainframe.messages.customer.LoadCustomerAddress;
import uoa.lavs.models.Address;

public class AddressLoader {

  public static Address loadData(String customerId, int number) {
    Address address = new Address();
    try {
      address = loadFromMainframe(customerId, number);
      if (address.getLine1() == null) {
        throw new Exception("Address not in mainframe");
      }
    } catch (Exception e) {
      System.out.println("Mainframe load failed: " + e.getMessage());
      System.out.println("Trying to load from database");
      try {
        address = loadFromDatabase(customerId, number);
      } catch (Exception e1) {
        System.out.println("Database load failed: " + e1.getMessage());
      }
    }
    return address;
  }

  private static Address loadFromDatabase(String customerId, int number) throws Exception {
    Address address = new Address();
    String query = "SELECT * FROM Address WHERE CustomerID = ? AND Number = ?";
    try (Connection connection = Instance.getDatabaseConnection();
        PreparedStatement statement = connection.prepareStatement(query)) {
      statement.setString(1, customerId);
      statement.setInt(2, number);
      try (ResultSet resultSet = statement.executeQuery()) {
        if (!resultSet.next()) {
          throw new Exception("Address not in database");
        }
        address.setCustomerId(resultSet.getString("CustomerID"));
        address.setNumber(resultSet.getInt("Number"));
        address.setType(resultSet.getString("Type"));
        address.setLine1(resultSet.getString("Line1"));
        address.setLine2(resultSet.getString("Line2"));
        address.setSuburb(resultSet.getString("Suburb"));
        address.setCity(resultSet.getString("City"));
        address.setPostCode(resultSet.getString("PostCode"));
        address.setCountry(resultSet.getString("Country"));
        address.setIsPrimary(resultSet.getBoolean("IsPrimary"));
        address.setIsMailing(resultSet.getBoolean("IsMailing"));
      }
    }
    return address;
  }

  private static Address loadFromMainframe(String customerId, int number) throws Exception {
    LoadCustomerAddress loadCustomerAddress = new LoadCustomerAddress();
    loadCustomerAddress.setCustomerId(customerId);
    loadCustomerAddress.setNumber(number);
    Status status = loadCustomerAddress.send(Instance.getConnection());
    if (!status.getWasSuccessful()) {
      System.out.println(
          "Something went wrong - the Mainframe send failed! The code is " + status.getErrorCode());
      throw new Exception("Mainframe send failed");
    }

    Address address = new Address();
    address.setCustomerId(customerId);
    address.setNumber(number);
    address.setType(loadCustomerAddress.getTypeFromServer());
    address.setLine1(loadCustomerAddress.getLine1FromServer());
    address.setLine2(loadCustomerAddress.getLine2FromServer());
    address.setSuburb(loadCustomerAddress.getSuburbFromServer());
    address.setCity(loadCustomerAddress.getCityFromServer());
    address.setPostCode(loadCustomerAddress.getPostCodeFromServer());
    address.setCountry(loadCustomerAddress.getCountryFromServer());
    address.setIsPrimary(loadCustomerAddress.getIsPrimaryFromServer());
    address.setIsMailing(loadCustomerAddress.getIsMailingFromServer());
    return address;
  }
}
