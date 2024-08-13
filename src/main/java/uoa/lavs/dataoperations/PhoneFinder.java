package uoa.lavs.dataoperations;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import uoa.lavs.mainframe.Instance;
import uoa.lavs.mainframe.Status;
import uoa.lavs.mainframe.messages.customer.FindCustomerPhoneNumber;
import uoa.lavs.models.Phone;

public class PhoneFinder implements Finder<Phone> {

  @Override
  public List<Phone> findData(String customerId) {
    List<Phone> phones = new ArrayList<>();
    try {
      phones = findFromDatabase(customerId);
    } catch (Exception e) {
      System.out.println("Database find failed: " + e.getMessage());
      System.out.println("Trying to find from mainframe");
      try {
        phones = findFromMainframe(customerId);
      } catch (Exception e1) {
        System.out.println("Mainframe find failed: " + e1.getMessage());
      }
    }
    return phones;
  }

  private List<Phone> findFromMainframe(String customerId) throws Exception {
    FindCustomerPhoneNumber findCustomerPhone = new FindCustomerPhoneNumber();
    findCustomerPhone.setCustomerId(customerId);
    Status status = findCustomerPhone.send(Instance.getConnection());
    if (!status.getWasSuccessful()) {
      System.out.println(
          "Something went wrong - the Mainframe send failed! The code is " + status.getErrorCode());
      throw new Exception("Mainframe send failed");
    }
    Integer phoneCount = findCustomerPhone.getCountFromServer();
    if (phoneCount == 0) {
      throw new Exception("Phone number not found in mainframe");
    }
    List<Phone> phones = new ArrayList<>(phoneCount);

    for (int i = 1; i <= phoneCount; i++) {
      Phone phone = new Phone();
      phone.setCustomerId(customerId);
      phone.setNumber(findCustomerPhone.getNumberFromServer(i));
      phone.setType(findCustomerPhone.getTypeFromServer(i));
      phone.setIsPrimary(findCustomerPhone.getIsPrimaryFromServer(i));
      phone.setCanSendTxt(findCustomerPhone.getCanSendTxtFromServer(i));
      phones.add(phone);
    }
    return phones;
  }

  private List<Phone> findFromDatabase(String customerId) throws Exception {
    List<Phone> phones = new ArrayList<>();
    Connection connection = DriverManager.getConnection("jdbc:sqlite:database.sqlite");
    Statement statement = connection.createStatement();
    String query = "SELECT * FROM Phone WHERE CustomerID LIKE '%" + customerId + "%'";
    ResultSet resultSet = statement.executeQuery(query);
    while (resultSet.next()) {
      Phone phone = new Phone();
      phone.setCustomerId(resultSet.getString("CustomerID"));
      phone.setNumber(resultSet.getInt("Number"));
      phone.setType(resultSet.getString("Type"));
      phone.setPrefix(resultSet.getString("Prefix"));
      phone.setPhoneNumber(resultSet.getString("PhoneNumber"));
      phone.setIsPrimary(resultSet.getBoolean("IsPrimary"));
      phone.setCanSendTxt(resultSet.getBoolean("CanSendText"));
      phones.add(phone);
    }
    connection.close();
    if (phones.isEmpty()) {
      throw new Exception("Phone number not found in database");
    }
    return phones;
  }
}
