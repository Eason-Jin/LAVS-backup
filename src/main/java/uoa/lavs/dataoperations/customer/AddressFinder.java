package uoa.lavs.dataoperations.customer;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import uoa.lavs.mainframe.Instance;
import uoa.lavs.mainframe.Status;
import uoa.lavs.mainframe.messages.customer.FindCustomerAddress;
import uoa.lavs.mainframe.messages.customer.LoadCustomerAddress;
import uoa.lavs.models.Address;

public class AddressFinder {

  public static List<Address> findData(String customerId) {
    List<Address> addresses = new ArrayList<>();
    try {
      addresses = findFromMainframe(customerId);
    } catch (Exception e) {
      System.out.println("Mainframe find failed: " + e.getMessage());
      System.out.println("Trying to find from database");
      try {
        addresses = findFromDatabase(customerId);
      } catch (Exception e1) {
        System.out.println("Database find failed: " + e1.getMessage());
      }
    }
    return addresses;
  }

  public static List<Address> findFromMainframe(String customerId) throws Exception {
    FindCustomerAddress findCustomerAddress = new FindCustomerAddress();
    LoadCustomerAddress loadCustomerAddress = new LoadCustomerAddress();
    findCustomerAddress.setCustomerId(customerId);
    loadCustomerAddress.setCustomerId(customerId);
    Status status = findCustomerAddress.send(Instance.getConnection());
    if (!status.getWasSuccessful()) {
      System.out.println(
          "Something went wrong - the Mainframe send failed! The code is " + status.getErrorCode());
      throw new Exception("Mainframe send failed");
    }
    Integer addressCount = findCustomerAddress.getCountFromServer();
    if (addressCount == 0) {
      throw new Exception("Address not found in mainframe");
    }
    List<Address> addresses = new ArrayList<>(addressCount);

    for (int i = 1; i <= addressCount; i++) {
      Address address = new Address();
      loadCustomerAddress.setNumber(findCustomerAddress.getNumberFromServer(i));
      loadCustomerAddress.send(Instance.getConnection());
      address.setCustomerId(customerId);
      address.setNumber(findCustomerAddress.getNumberFromServer(i));
      address.setType(findCustomerAddress.getTypeFromServer(i));
      address.setIsPrimary(findCustomerAddress.getIsPrimaryFromServer(i));
      address.setIsMailing(findCustomerAddress.getIsMailingFromServer(i));
      address.setLine1(loadCustomerAddress.getLine1FromServer());
      address.setLine2(loadCustomerAddress.getLine2FromServer());
      address.setSuburb(loadCustomerAddress.getSuburbFromServer());
      address.setCity(loadCustomerAddress.getCityFromServer());
      address.setPostCode(loadCustomerAddress.getPostCodeFromServer());
      address.setCountry(loadCustomerAddress.getCountryFromServer());
      addresses.add(address);
    }
    return addresses;
  }

  public static List<Address> findFromDatabase(String customerId) throws Exception {
    List<Address> addresses = new ArrayList<>();
    String query = "SELECT * FROM Address WHERE CustomerID = ?";
    try (Connection connection = Instance.getDatabaseConnection();
        PreparedStatement statement = connection.prepareStatement(query)) {
      statement.setString(1, customerId);
      try (ResultSet resultSet = statement.executeQuery()) {
        while (resultSet.next()) {
          Address address = new Address();
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
          addresses.add(address);
        }
      }
    }
    if (addresses.isEmpty()) {
      throw new Exception("Address not found in database");
    }
    return addresses;
  }
}
