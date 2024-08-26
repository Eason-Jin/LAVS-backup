package uoa.lavs.dataoperations.customer;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import uoa.lavs.LocalInstance;
import uoa.lavs.mainframe.Status;
import uoa.lavs.mainframe.messages.customer.LoadCustomer;
import uoa.lavs.mainframe.messages.customer.LoadCustomerNote;
import uoa.lavs.models.Customer;

public class CustomerLoader {

  public static Customer loadData(String customerId) {
    Customer customer = new Customer();
    try {
      customer = loadFromMainframe(customerId);
    } catch (Exception e) {
      System.out.println("Mainframe load failed: " + e.getMessage());
      System.out.println("Trying to load from database");
      try {
        customer = loadFromDatabase(customerId);
      } catch (Exception e1) {
        System.out.println("Database load failed: " + e1.getMessage());
      }
    }
    return customer;
  }

  public static Customer loadFromDatabase(String customerId) throws Exception {
    Customer customer = new Customer();
    Connection connection = LocalInstance.getDatabaseConnection();
    String query = "SELECT * FROM customer WHERE CustomerID = ?";
    try (PreparedStatement statement = connection.prepareStatement(query)) {
      statement.setString(1, customerId);
      try (ResultSet resultSet = statement.executeQuery()) {
        if (!resultSet.next()) {
          throw new Exception("Customer not in database");
        }
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
        customer.setNotes(resultSet.getString("Note"));
      }
    } finally {
      connection.close();
    }
    return customer;
  }

  public static Customer loadFromMainframe(String customerId) throws Exception {
    LoadCustomer loadCustomer = new LoadCustomer();
    LoadCustomerNote loadCustomerNote = new LoadCustomerNote();
    loadCustomer.setCustomerId(customerId);
    loadCustomerNote.setCustomerId(customerId);
    loadCustomerNote.setNumber(1);
    Status status = loadCustomer.send(LocalInstance.getConnection());
    if (!status.getWasSuccessful()) {
      System.out.println(
          "Something went wrong - the Mainframe send failed! The code is " + status.getErrorCode());
      throw new Exception("Mainframe send failed");
    }
    loadCustomerNote.send(LocalInstance.getConnection());

    Customer customer = new Customer();
    customer.setId(customerId);
    customer.setName(loadCustomer.getNameFromServer());
    customer.setDob(loadCustomer.getDateofBirthFromServer());
    customer.setCitizenship(loadCustomer.getCitizenshipFromServer());
    customer.setOccupation(loadCustomer.getOccupationFromServer());
    customer.setTitle(loadCustomer.getTitleFromServer());
    customer.setVisaType(loadCustomer.getVisaFromServer());
    customer.setStatus(loadCustomer.getStatusFromServer());
    StringBuilder noteBuilder = new StringBuilder();
    Integer lineCount = loadCustomerNote.getLineCountFromServer();
    if (lineCount != null) {
      for (int i = 1; i <= lineCount; i++) {
        String line = loadCustomerNote.getLineFromServer(i);
        if (line != null) {
          noteBuilder.append(line);
        }
      }
    }
    customer.setNotes(noteBuilder.toString());
    return customer;
  }
}
