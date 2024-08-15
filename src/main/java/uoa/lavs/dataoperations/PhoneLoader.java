package uoa.lavs.dataoperations;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import uoa.lavs.mainframe.Instance;
import uoa.lavs.mainframe.Status;
import uoa.lavs.mainframe.messages.customer.LoadCustomerPhoneNumber;
import uoa.lavs.models.Phone;

public class PhoneLoader {

  public static Phone loadData(String customerId, int number) {
    Phone phone = new Phone();
    try {
      phone = loadFromMainframe(customerId, number);
      if (phone.getPhoneNumber() == null) {
        throw new Exception("Phone number not in mainframe");
      }
    } catch (Exception e) {
      System.out.println("Mainframe load failed: " + e.getMessage());
      System.out.println("Trying to load from database");
      try {
        phone = loadFromDatabase(customerId, number);
      } catch (Exception e1) {
        System.out.println("Database load failed: " + e1.getMessage());
      }
    }
    return phone;
  }

  private static Phone loadFromDatabase(String customerId, int number) throws Exception {
    Phone phone = new Phone();
    String query = "SELECT * FROM Phone WHERE CustomerID = ? AND Number = ?";
    try (Connection connection = Instance.getDatabaseConnection();
        PreparedStatement statement = connection.prepareStatement(query)) {
      statement.setString(1, customerId);
      statement.setInt(2, number);
      try (ResultSet resultSet = statement.executeQuery()) {
        if (!resultSet.next()) {
          throw new Exception("Phone number not in database");
        }
        phone.setCustomerId(resultSet.getString("CustomerID"));
        phone.setNumber(resultSet.getInt("Number"));
        phone.setType(resultSet.getString("Type"));
        phone.setPrefix(resultSet.getString("Prefix"));
        phone.setPhoneNumber(resultSet.getString("PhoneNumber"));
        phone.setIsPrimary(resultSet.getBoolean("IsPrimary"));
        phone.setCanSendTxt(resultSet.getBoolean("CanSendText"));
      }
    }
    return phone;
  }

  private static Phone loadFromMainframe(String customerId, int number) throws Exception {
    LoadCustomerPhoneNumber loadCustomerPhone = new LoadCustomerPhoneNumber();
    loadCustomerPhone.setCustomerId(customerId);
    loadCustomerPhone.setNumber(number);
    Status status = loadCustomerPhone.send(Instance.getConnection());
    if (!status.getWasSuccessful()) {
      System.out.println(
          "Something went wrong - the Mainframe send failed! The code is " + status.getErrorCode());
      throw new Exception("Mainframe send failed");
    }

    Phone phone = new Phone();
    phone.setCustomerId(customerId);
    phone.setNumber(number);
    phone.setType(loadCustomerPhone.getTypeFromServer());
    phone.setPrefix(loadCustomerPhone.getPrefixFromServer());
    phone.setPhoneNumber(loadCustomerPhone.getPhoneNumberFromServer());
    phone.setIsPrimary(loadCustomerPhone.getIsPrimaryFromServer());
    phone.setCanSendTxt(loadCustomerPhone.getCanSendTxtFromServer());
    return phone;
  }
}
