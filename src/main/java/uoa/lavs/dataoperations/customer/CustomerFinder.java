package uoa.lavs.dataoperations.customer;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import uoa.lavs.LocalInstance;
import uoa.lavs.mainframe.Status;
import uoa.lavs.mainframe.messages.customer.FindCustomer;
import uoa.lavs.mainframe.messages.customer.FindCustomerAdvanced;
import uoa.lavs.models.Customer;

public class CustomerFinder {

  public static List<Customer> findData(String customerId) {
    List<Customer> customers = new ArrayList<>();
    try {
      customers = findFromMainframe(customerId);
    } catch (Exception e) {
      System.out.println("Mainframe find failed: " + e.getMessage());
      System.out.println("Trying to find from database");
      try {
        customers = findFromDatabase(customerId);
      } catch (Exception e1) {
        System.out.println("Database find failed: " + e1.getMessage());
      }
    }
    return customers;
  }

  public static List<Customer> findCustomerByName(String customerName) {
    List<Customer> customers = new ArrayList<>();
    try {
      customers = findFromMainframeByName(customerName);
    } catch (Exception e) {
      System.out.println("Mainframe find failed: " + e.getMessage());
      System.out.println("Trying to find from database");
      try {
        customers = findFromDatabaseByName(customerName);
      } catch (Exception e1) {
        System.out.println("Database find failed: " + e1.getMessage());
      }
    }
    return customers;
  }

  private static List<Customer> findFromMainframe(String customerId) throws Exception {
    FindCustomer findCustomer = new FindCustomer();
    findCustomer.setCustomerId(customerId);
    Status status = findCustomer.send(LocalInstance.getConnection());
    if (!status.getWasSuccessful()) {
      System.out.println(
          "Something went wrong - the Mainframe send failed! The code is " + status.getErrorCode());
      throw new Exception("Mainframe send failed");
    }
    Integer customerCount = findCustomer.getCustomerCountFromServer();
    if (customerCount == 0) {
      throw new Exception("Customer not found in mainframe");
    }
    List<Customer> customers = new ArrayList<>(customerCount);

    for (int i = 1; i <= customerCount; i++) {
      Customer customer = new Customer();
      customer.setId(findCustomer.getIdFromServer(i));
      customer.setName(findCustomer.getNameFromServer(i));
      customer.setDob(findCustomer.getDateofBirthFromServer(i));
      customers.add(customer);
    }
    return customers;
  }

  private static List<Customer> findFromDatabase(String customerId) throws Exception {
    List<Customer> customers = new ArrayList<>();
    Connection connection = LocalInstance.getDatabaseConnection();
    Statement statement = connection.createStatement();
    String query = "SELECT * FROM customer WHERE CustomerID LIKE '%" + customerId + "%'";
    ResultSet resultSet = statement.executeQuery(query);
    while (resultSet.next()) {
      Customer customer = new Customer();
      customer.setId(resultSet.getString("CustomerID"));
      customer.setName(resultSet.getString("Name"));
      String dobString = resultSet.getString("Dob");
      LocalDate dob = LocalDate.parse(dobString, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
      customer.setDob(dob);
      customers.add(customer);
    }
    connection.close();
    if (customers.isEmpty()) {
      throw new Exception("Customer not found in database");
    }
    return customers;
  }

  private static List<Customer> findFromMainframeByName(String customerName) throws Exception {
    FindCustomerAdvanced findCustomer = new FindCustomerAdvanced();
    findCustomer.setSearchName(customerName);
    Status status = findCustomer.send(LocalInstance.getConnection());
    if (!status.getWasSuccessful()) {
      System.out.println(
          "Something went wrong - the Mainframe send failed! The code is " + status.getErrorCode());
      throw new Exception("Mainframe send failed");
    }
    Integer customerCount = findCustomer.getCustomerCountFromServer();
    if (customerCount == 0) {
      throw new Exception("Customer not found in mainframe");
    }
    List<Customer> customers = new ArrayList<>(customerCount);

    for (int i = 1; i <= customerCount; i++) {
      Customer customer = new Customer();
      customer.setId(findCustomer.getIdFromServer(i));
      customer.setName(findCustomer.getNameFromServer(i));
      customer.setDob(findCustomer.getDateofBirthFromServer(i));
      customers.add(customer);
    }
    return customers;
  }

  private static List<Customer> findFromDatabaseByName(String customerName) throws Exception {
    List<Customer> customers = new ArrayList<>();
    Connection connection = LocalInstance.getDatabaseConnection();
    String query = "SELECT * FROM customer WHERE Name LIKE ?";
    PreparedStatement preparedStatement = connection.prepareStatement(query);
    preparedStatement.setString(1, customerName);
    ResultSet resultSet = preparedStatement.executeQuery();
    while (resultSet.next()) {
      Customer customer = new Customer();
      customer.setId(resultSet.getString("CustomerID"));
      customer.setName(resultSet.getString("Name"));
      String dobString = resultSet.getString("Dob");
      LocalDate dob = LocalDate.parse(dobString, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
      customer.setDob(dob);
      customers.add(customer);
    }
    resultSet.close();
    preparedStatement.close();
    connection.close();
    if (customers.isEmpty()) {
      throw new Exception("Customer not found in database");
    }
    return customers;
  }
}
