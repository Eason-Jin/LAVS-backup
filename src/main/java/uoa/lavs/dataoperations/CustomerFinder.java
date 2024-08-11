package uoa.lavs.dataoperations;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import uoa.lavs.mainframe.Instance;
import uoa.lavs.mainframe.Status;
import uoa.lavs.mainframe.messages.customer.FindCustomer;
import uoa.lavs.models.Customer;

public class CustomerFinder implements Finder<Customer> {

  @Override
  public List<Customer> findData(String customerId) {
    List<Customer> customers = new ArrayList<>();
    try {
      customers = findFromMainframe(customerId);
    } catch (Exception e) {
      System.out.println("Mainframe find failed: " + e.getMessage());
      System.out.println("Trying to find from database");
      customers = findFromDatabase(customerId);
    }
    return customers;
  }

  private List<Customer> findFromMainframe(String customerId) throws Exception {
    FindCustomer findCustomer = new FindCustomer();
    findCustomer.setCustomerId(customerId);
    Status status = findCustomer.send(Instance.getConnection());
    if (!status.getWasSuccessful()) {
      System.out.println(
          "Something went wrong - the Mainframe send failed! The code is " + status.getErrorCode());
      throw new Exception("Mainframe send failed");
    }
    Integer customerCount = findCustomer.getCustomerCountFromServer();
    if (customerCount == null) {
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

  private List<Customer> findFromDatabase(String customerId) {
    List<Customer> customers = new ArrayList<>();
    try {
      Connection connection = DriverManager.getConnection("jdbc:sqlite:database.sqlite");
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
    } catch (Exception e) {
      e.printStackTrace();
    }
    return customers;
  }
}
