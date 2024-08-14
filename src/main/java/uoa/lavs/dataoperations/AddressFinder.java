package uoa.lavs.dataoperations;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import uoa.lavs.mainframe.Instance;
import uoa.lavs.mainframe.Status;
import uoa.lavs.mainframe.messages.customer.FindCustomerAddress;
import uoa.lavs.models.Address;

public class AddressFinder implements Finder<Address> {

  @Override
  public List<Address> findData(String customerId) {
    List<Address> addresses = new ArrayList<>();
    try {
      addresses = findFromDatabase(customerId);
    } catch (Exception e) {
      System.out.println("Database find failed: " + e.getMessage());
      System.out.println("Trying to find from mainframe");
      try {
        addresses = findFromMainframe(customerId);
      } catch (Exception e1) {
        System.out.println("Mainframe find failed: " + e1.getMessage());
      }
    }
    return addresses;
  }

  private List<Address> findFromMainframe(String customerId) throws Exception {
    FindCustomerAddress findCustomerAddress = new FindCustomerAddress();
    findCustomerAddress.setCustomerId(customerId);
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
      address.setCustomerId(customerId);
      address.setNumber(findCustomerAddress.getNumberFromServer(i));
      address.setType(findCustomerAddress.getTypeFromServer(i));
      address.setIsPrimary(findCustomerAddress.getIsPrimaryFromServer(i));
      address.setIsMailing(findCustomerAddress.getIsMailingFromServer(i));
      addresses.add(address);
    }
    return addresses;
  }

  private List<Address> findFromDatabase(String customerId) throws Exception {
    List<Address> addresses = new ArrayList<>();
    Connection connection = DriverManager.getConnection("jdbc:sqlite:database.sqlite");
    Statement statement = connection.createStatement();
    String query = "SELECT * FROM Address WHERE CustomerID LIKE '%" + customerId + "%'";
    ResultSet resultSet = statement.executeQuery(query);
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
    connection.close();
    if (addresses.isEmpty()) {
      throw new Exception("Address not found in database");
    }
    return addresses;
  }
}
