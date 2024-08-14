package uoa.lavs.dataoperations;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import uoa.lavs.mainframe.Instance;
import uoa.lavs.mainframe.Status;
import uoa.lavs.mainframe.messages.customer.UpdateCustomer;
import uoa.lavs.models.Customer;

public class CustomerUpdater {
  private static List<FailedCall> failedCalls = new ArrayList<>();

  public static void updateData(String customerID, Customer customer) {
    String id = customerID;
    try {
      id = updateMainframe(customerID, customer);
      if (id == null) {
        id = customerID;
      }
    } catch (Exception e) {
      System.out.println("Mainframe update failed: " + e.getMessage());
    } finally {
      try {
        updateDatabase(id, customer);
      } catch (SQLException e) {
        System.out.println("Database update failed: " + e.getMessage());
      }
    }
  }

  private static void updateDatabase(String customerID, Customer customer) throws SQLException {
    boolean exists = false;
    String CHECK_SQL = "SELECT COUNT(*) FROM customer WHERE CustomerID = ?";

    // Check if CustomerID exists
    if (customerID != null) {
      try (Connection connection = DriverManager.getConnection("jdbc:sqlite:database.sqlite");
          PreparedStatement checkStatement = connection.prepareStatement(CHECK_SQL)) {
        checkStatement.setString(1, customerID);
        try (ResultSet resultSet = checkStatement.executeQuery()) {
          if (resultSet.next()) {
            exists = resultSet.getInt(1) > 0;
          }
        }
      }
    } else { // If new customer and mainframe add failed, set status to pending
      customer.setStatus("Pending");
    }

    String sql;
    if (exists) {
      sql =
          "UPDATE Customer SET "
              + "Title = COALESCE(?, Title), "
              + "Name = COALESCE(?, Name), "
              + "Dob = COALESCE(?, Dob), "
              + "Occupation = COALESCE(?, Occupation), "
              + "Citizenship = COALESCE(?, Citizenship), "
              + "VisaType = COALESCE(?, VisaType), "
              + "Status = COALESCE(?, Status) "
              + "WHERE CustomerID = ?";
    } else {
      sql =
          "INSERT INTO Customer (Title, Name, Dob, Occupation, Citizenship, VisaType, Status) "
              + "VALUES (?, ?, ?, ?, ?, ?, ?)";
    }

    try (Connection connection = DriverManager.getConnection("jdbc:sqlite:database.sqlite");
        PreparedStatement statement =
            connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

      statement.setString(1, customer.getTitle());
      statement.setString(2, customer.getName());
      statement.setObject(3, customer.getDob());
      statement.setString(4, customer.getOccupation());
      statement.setString(5, customer.getCitizenship());
      statement.setString(6, customer.getVisaType());
      statement.setString(7, customer.getStatus());
      if (exists) {
        statement.setString(8, customerID);
      }

      statement.executeUpdate();

      if (!exists) {
        try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
          if (generatedKeys.next()) {
            customerID = generatedKeys.getString(1);
          }
        }
      }
      customer.setId(customerID);
    }
  }

  private static String updateMainframe(String customerID, Customer customer) throws Exception {
    UpdateCustomer updateCustomer = new UpdateCustomer();
    updateCustomer.setCustomerId(customerID);

    if (customerID != null) {
      Customer existingCustomer = CustomerLoader.loadData(customerID);

      updateCustomer.setTitle(
          customer.getTitle() != null ? customer.getTitle() : existingCustomer.getTitle());
      updateCustomer.setName(
          customer.getName() != null ? customer.getName() : existingCustomer.getName());
      updateCustomer.setDateofBirth(
          customer.getDob() != null ? customer.getDob() : existingCustomer.getDob());
      updateCustomer.setOccupation(
          customer.getOccupation() != null
              ? customer.getOccupation()
              : existingCustomer.getOccupation());
      updateCustomer.setCitizenship(
          customer.getCitizenship() != null
              ? customer.getCitizenship()
              : existingCustomer.getCitizenship());
      updateCustomer.setVisa(
          customer.getVisaType() != null ? customer.getVisaType() : existingCustomer.getVisaType());
    } else {
      updateCustomer.setTitle(customer.getTitle());
      updateCustomer.setName(customer.getName());
      updateCustomer.setDateofBirth(customer.getDob());
      updateCustomer.setOccupation(customer.getOccupation());
      updateCustomer.setCitizenship(customer.getCitizenship());
      updateCustomer.setVisa(customer.getVisaType());
    }

    Status status = updateCustomer.send(Instance.getConnection());
    if (!status.getWasSuccessful()) {
      recordFailedCall(customerID, customer);
      System.out.println(
          "Something went wrong - the Mainframe send failed! The code is " + status.getErrorCode());
      throw new Exception("Mainframe send failed");
    }
    customer.setId(updateCustomer.getCustomerIdFromServer());
    customer.setStatus(updateCustomer.getStatusFromServer());
    return updateCustomer.getCustomerIdFromServer();
  }

  private static void recordFailedCall(String customerID, Customer customer) {
    failedCalls.add(new FailedCall(customerID, customer));
  }

  public static void retryFailedCalls() {
    List<FailedCall> retryList = new ArrayList<>(failedCalls);
    failedCalls.clear();
    for (FailedCall failedCall : retryList) {
      updateData(failedCall.getCustomerID(), failedCall.getCustomer());
    }
  }

  private static class FailedCall {
    private String customerID;
    private Customer customer;

    public FailedCall(String customerID, Customer customer) {
      this.customerID = customerID;
      this.customer = customer;
    }

    public String getCustomerID() {
      return customerID;
    }

    public Customer getCustomer() {
      return customer;
    }
  }
}
