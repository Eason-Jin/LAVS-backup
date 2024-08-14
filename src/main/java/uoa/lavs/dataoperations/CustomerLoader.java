package uoa.lavs.dataoperations;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import uoa.lavs.mainframe.Instance;
import uoa.lavs.mainframe.Status;
import uoa.lavs.mainframe.messages.customer.LoadCustomer;
import uoa.lavs.models.Customer;

public class CustomerLoader {

  public static Customer loadData(String customerId) {
    Customer customer = new Customer();
    try {
      customer = loadFromMainframe(customerId);
      if(customer.getName() == null) {
        throw new Exception("Customer not in mainframe");
      }
    } catch (Exception e) {
      System.out.println("Mainframe load failed: " + e.getMessage());
      System.out.println("Trying to load from database");
      customer = loadFromDatabase(customerId);
    }
    return customer;
  }

  private static Customer loadFromDatabase(String customerId) {
    Customer customer = new Customer();
    try {
      Connection connection = DriverManager.getConnection("jdbc:sqlite:database.sqlite");
      Statement statement = connection.createStatement();
      String query = "SELECT * FROM customer WHERE CustomerID = " + customerId + ";";
      ResultSet resultSet = statement.executeQuery(query);
      customer.setId(resultSet.getString("CustomerID"));
      customer.setName(resultSet.getString("Name"));
      String dobString = resultSet.getString("Dob");
      LocalDate dob = LocalDate.parse(dobString, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
      customer.setDob(dob);
      customer.setCitizenship(resultSet.getString("Citizenship"));
      customer.setOccupation(resultSet.getString("Occupation"));
      customer.setTitle(resultSet.getString("Title"));
      customer.setVisaType(resultSet.getString("VisaType"));
      customer.setStatus(resultSet.getString("Status"));
      connection.close();
    } catch (Exception e) {
      e.printStackTrace();
    }
    return customer;
  }

  private static Customer loadFromMainframe(String customerId) throws Exception {
    LoadCustomer loadCustomer = new LoadCustomer();
    loadCustomer.setCustomerId(customerId);
    Status status = loadCustomer.send(Instance.getConnection());
    if (!status.getWasSuccessful()) {
      System.out.println(
          "Something went wrong - the Mainframe send failed! The code is " + status.getErrorCode());
      throw new Exception("Mainframe send failed");
    }

    Customer customer = new Customer();
    customer.setId(customerId);
    customer.setName(loadCustomer.getNameFromServer());
    customer.setDob(loadCustomer.getDateofBirthFromServer());
    customer.setCitizenship(loadCustomer.getCitizenshipFromServer());
    customer.setOccupation(loadCustomer.getOccupationFromServer());
    customer.setTitle(loadCustomer.getTitleFromServer());
    customer.setVisaType(loadCustomer.getVisaFromServer());
    customer.setStatus(loadCustomer.getStatusFromServer());
    return customer;
  }
}
