package uoa.lavs.dataoperations.customer;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import uoa.lavs.mainframe.Instance;
import uoa.lavs.mainframe.Status;
import uoa.lavs.mainframe.messages.customer.UpdateCustomer;
import uoa.lavs.mainframe.messages.customer.UpdateCustomerNote;
import uoa.lavs.models.Customer;

public class CustomerUpdater {

  private static boolean failed = false;
  public static StringBuilder message = new StringBuilder();

  public static void updateData(String customerID, Customer customer) {
    String id = customerID;
    try {
      id = updateMainframe(customerID, customer);
      if (id == null) {
        id = customerID;
      }
      if (message.indexOf("Mainframe update successful") != -1) {
        message.append("Mainframe update successful\n");
      }
    } catch (Exception e) {
      System.out.println("Mainframe update failed: " + e.getMessage());
      failed = true;
    } finally {
      try {
        updateDatabase(id, customer);
        if (message.indexOf("Mainframe update successful") != -1) {
          message.append("Database update successful\n");
        }
      } catch (SQLException e) {
        System.out.println("Database update failed: " + e.getMessage());
      } finally {
        if (failed) {
          addFailedUpdate(customer.getId());
        } else {
          addInMainframe(customer.getId(), customer.getId());
        }
      }
    }
  }

  public static void updateDatabase(String customerID, Customer customer) throws SQLException {
    boolean exists = false;
    String CHECK_SQL = "SELECT COUNT(*) FROM customer WHERE CustomerID = ?";

    // Check if CustomerID exists
    if (customerID != null) {
      try (Connection connection = Instance.getDatabaseConnection();
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
              + "Status = COALESCE(?, Status), "
              + "Note = COALESCE(?, Note) "
              + "WHERE CustomerID = ?";
    } else {
      sql =
          "INSERT INTO Customer (Title, Name, Dob, Occupation, Citizenship, VisaType, Status, Note,"
              + "  CustomerID) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
    }

    try (Connection connection = Instance.getDatabaseConnection();
        PreparedStatement statement =
            connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
      statement.setString(1, customer.getTitle());
      statement.setString(2, customer.getName());
      statement.setObject(3, customer.getDob());
      statement.setString(4, customer.getOccupation());
      statement.setString(5, customer.getCitizenship());
      statement.setString(6, customer.getVisaType());
      statement.setString(7, customer.getStatus());
      statement.setString(8, customer.getNotes());
      if (customerID != null) {
        statement.setString(9, customerID);
      } else {
        customerID = generateNewId();
        statement.setString(9, customerID);
      }

      statement.executeUpdate();

      customer.setId(customerID);
    }
  }

  public static String updateMainframe(String customerID, Customer customer) throws Exception {
    UpdateCustomer updateCustomer = new UpdateCustomer();
    UpdateCustomerNote updateCustomerNote = new UpdateCustomerNote();
    if (customerID != null && !customerID.contains("Temporary")) {
      updateCustomer.setCustomerId(customerID);
    } else {
      customerID = null;
      updateCustomer.setCustomerId(null);
    }
    Customer existingCustomer = null;

    try {
      if (customerID != null) {
        existingCustomer = CustomerLoader.loadFromMainframe(customerID);
      }
    } catch (Exception e) {
      System.out.println(String.format("Customer %s not in mainframe", customerID));
    }

    if (existingCustomer != null) {
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
      ArrayList<String> notes = customer.splitNotes();
      for (int i = 0; i < notes.size(); i++) {
        updateCustomerNote.setLine(i + 1, notes.get(i));
      }
      updateCustomerNote.setNumber(1);
    } else {
      updateCustomer.setCustomerId(null);
      updateCustomer.setTitle(customer.getTitle());
      updateCustomer.setName(customer.getName());
      updateCustomer.setDateofBirth(customer.getDob());
      updateCustomer.setOccupation(customer.getOccupation());
      updateCustomer.setCitizenship(customer.getCitizenship());
      updateCustomer.setVisa(customer.getVisaType());
      ArrayList<String> notes = customer.splitNotes();
      for (int i = 0; i < notes.size(); i++) {
        updateCustomerNote.setLine(i + 1, notes.get(i));
      }
    }

    Status status = updateCustomer.send(Instance.getConnection());
    updateCustomerNote.setCustomerId(updateCustomer.getCustomerIdFromServer());
    if (!status.getWasSuccessful()) {
      failed = true;
      System.out.println(
          "Something went wrong - the Mainframe send failed! The code is " + status.getErrorCode());
      throw new Exception("Mainframe send failed");
    }
    updateCustomerNote.send(Instance.getConnection());
    customer.setId(updateCustomer.getCustomerIdFromServer());
    customer.setStatus(updateCustomer.getStatusFromServer());
    return updateCustomer.getCustomerIdFromServer();
  }

  private static void addFailedUpdate(String customerID) {
    String sql = "UPDATE Customer SET InMainframe = false WHERE CustomerID = ?";
    try (Connection connection = Instance.getDatabaseConnection();
        PreparedStatement statement = connection.prepareStatement(sql)) {
      statement.setString(1, customerID);
      statement.executeUpdate();
    } catch (SQLException e) {
      System.out.println("Failed to record failed call: " + e.getMessage());
    }
  }

  public static List<Customer> getFailedUpdates() {
    List<Customer> failedUpdates = new ArrayList<>();
    String sql = "SELECT * FROM Customer WHERE InMainframe = false";
    try (Connection connection = Instance.getDatabaseConnection();
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery(sql)) {
      while (resultSet.next()) {
        String customerID = resultSet.getString("CustomerID");
        Customer customer = CustomerLoader.loadData(customerID);
        failedUpdates.add(customer);
      }
    } catch (SQLException e) {
      System.out.println("Failed to get failed updates: " + e.getMessage());
    }
    return failedUpdates;
  }

  public static void retryFailedUpdates() throws Exception {
    List<Customer> failedUpdates = getFailedUpdates();
    for (Customer customer : failedUpdates) {
      String customerID = customer.getId();
      String id = updateMainframe(customerID, customer);
      customer.setId(id);
      addInMainframe(customerID, id);
      String sql = "UPDATE Customer SET Status = 'Active' WHERE CustomerId = ?";
      try (Connection connection = Instance.getDatabaseConnection();
          PreparedStatement statement = connection.prepareStatement(sql)) {
        statement.setString(1, id);
        statement.executeUpdate();
      } catch (SQLException e) {
        e.printStackTrace();
      }
    }
  }

  private static void addInMainframe(String customerID, String mainframeId) {
    String sql = "UPDATE Customer SET CustomerID = ?, InMainframe = ? WHERE CustomerID = ?";
    try (Connection connection = Instance.getDatabaseConnection();
        Statement pragmaStatement = connection.createStatement();
        PreparedStatement statement = connection.prepareStatement(sql)) {

      pragmaStatement.execute("PRAGMA foreign_keys = ON");

      statement.setString(1, mainframeId);
      statement.setBoolean(2, true);
      statement.setString(3, customerID);
      statement.executeUpdate();

    } catch (SQLException e) {
      System.out.println("Failed to update CustomerID and InMainframe: " + e.getMessage());
    }
  }

  private static String generateNewId() throws SQLException {
    String newId;

    String selectLastId =
        "SELECT CustomerID FROM Customer ORDER BY CAST(CustomerID AS INTEGER) DESC";
    try (Statement stmt = Instance.getDatabaseConnection().createStatement();
        ResultSet rs = stmt.executeQuery(selectLastId)) {
      if (rs.next()) {
        String lastIdStr = rs.getString("CustomerID");
        // Remove "(Temporary)" if it exists
        lastIdStr = lastIdStr.replace(" (Temporary)", "");
        int lastId = Integer.parseInt(lastIdStr);
        newId = Integer.toString(lastId + 1);
      } else {
        newId = "1";
      }
    }

    return newId + " (Temporary)";
  }
}
