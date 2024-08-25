package uoa.lavs.dataoperations.customer;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import uoa.lavs.LocalInstance;
import uoa.lavs.mainframe.Status;
import uoa.lavs.mainframe.messages.customer.LoadCustomerPhoneNumbers;
import uoa.lavs.models.Phone;

public class PhoneFinder {

  public static List<Phone> findData(String customerId) {
    List<Phone> phones = new ArrayList<>();
    try {
      phones = findFromMainframe(customerId);
    } catch (Exception e) {
      System.out.println("Mainframe find failed: " + e.getMessage());
      System.out.println("Trying to find from database");
      try {
        phones = findFromDatabase(customerId);
      } catch (Exception e1) {
        System.out.println("Database find failed: " + e1.getMessage());
      }
    }
    return phones;
  }

  public static List<Phone> findFromMainframe(String customerId) throws Exception {
    LoadCustomerPhoneNumbers loadCustomerPhoneNumbers = new LoadCustomerPhoneNumbers();
    loadCustomerPhoneNumbers.setCustomerId(customerId);
    Status status = loadCustomerPhoneNumbers.send(LocalInstance.getConnection());
    if (!status.getWasSuccessful()) {
      System.out.println(
          "Something went wrong - the Mainframe send failed! The code is " + status.getErrorCode());
      throw new Exception("Mainframe send failed");
    }
    Integer phoneCount = loadCustomerPhoneNumbers.getCountFromServer();
    if (phoneCount == 0) {
      throw new Exception("Phone not found in mainframe");
    }
    List<Phone> phones = new ArrayList<>(phoneCount);

    for (int i = 1; i <= phoneCount; i++) {
      Phone phone = new Phone();
      phone.setCustomerId(customerId);
      phone.setNumber(loadCustomerPhoneNumbers.getNumberFromServer(i));
      phone.setType(loadCustomerPhoneNumbers.getTypeFromServer(i));
      phone.setIsPrimary(loadCustomerPhoneNumbers.getIsPrimaryFromServer(i));
      phone.setCanSendText(loadCustomerPhoneNumbers.getCanSendTxtFromServer(i));
      phone.setPhoneNumber(loadCustomerPhoneNumbers.getPhoneNumberFromServer(i));
      phone.setPrefix(loadCustomerPhoneNumbers.getPrefixFromServer(i));
      phone.setType(loadCustomerPhoneNumbers.getTypeFromServer(i));
      phones.add(phone);
    }
    return phones;
  }

  public static List<Phone> findFromDatabase(String customerId) throws Exception {
    List<Phone> phones = new ArrayList<>();
    Connection connection = null;
    PreparedStatement preparedStatement = null;
    ResultSet resultSet = null;

    try {
      connection = LocalInstance.getDatabaseConnection();
      String query = "SELECT * FROM Phone WHERE CustomerID = ?";
      preparedStatement = connection.prepareStatement(query);
      preparedStatement.setString(1, customerId);
      resultSet = preparedStatement.executeQuery();

      while (resultSet.next()) {
        Phone phone = new Phone();
        phone.setCustomerId(resultSet.getString("CustomerID"));
        phone.setNumber(resultSet.getInt("Number"));
        phone.setType(resultSet.getString("Type"));
        phone.setPrefix(resultSet.getString("Prefix"));
        phone.setPhoneNumber(resultSet.getString("PhoneNumber"));
        phone.setIsPrimary(resultSet.getBoolean("IsPrimary"));
        phone.setCanSendText(resultSet.getBoolean("CanSendText"));
        phones.add(phone);
      }
    } finally {
      connection.close();
    }
    if (phones.isEmpty()) {
      throw new Exception("Phone not found in database");
    }
    return phones;
  }
}
