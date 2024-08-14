package uoa.lavs.dataoperations;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import uoa.lavs.mainframe.Instance;
import uoa.lavs.mainframe.Status;
import uoa.lavs.mainframe.messages.customer.LoadCustomerPhoneNumber;
import uoa.lavs.models.Phone;

public class PhoneLoader {

  public Phone loadData(String customerId, int number) {
    Phone phone = new Phone();
    try {
      phone = loadFromMainframe(customerId, number);
      if (phone.getPhoneNumber() == null) {
        throw new Exception("Phone number not in mainframe");
      }
    } catch (Exception e) {
      System.out.println("Mainframe load failed: " + e.getMessage());
      System.out.println("Trying to load from database");
      phone = loadFromDatabase(customerId, number);
    }
    return phone;
  }

  private Phone loadFromDatabase(String customerId, int number) {
    Phone phone = new Phone();
    String query = "SELECT * FROM Phone WHERE CustomerID = ? AND Number = ?";
    try (Connection connection = DriverManager.getConnection("jdbc:sqlite:database.sqlite");
        PreparedStatement statement = connection.prepareStatement(query)) {
      statement.setString(1, customerId);
      statement.setInt(2, number);
      try (ResultSet resultSet = statement.executeQuery()) {
        if (resultSet.next()) {
          phone.setCustomerId(resultSet.getString("CustomerID"));
          phone.setNumber(resultSet.getInt("Number"));
          phone.setType(resultSet.getString("Type"));
          phone.setPrefix(resultSet.getString("Prefix"));
          phone.setPhoneNumber(resultSet.getString("PhoneNumber"));
          phone.setIsPrimary(resultSet.getBoolean("IsPrimary"));
          phone.setCanSendTxt(resultSet.getBoolean("CanSendText"));
        }
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return phone;
  }

  private Phone loadFromMainframe(String customerId, int number) throws Exception {
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