package uoa.lavs.dataoperations.customer;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import uoa.lavs.LocalInstance;
import uoa.lavs.mainframe.Status;
import uoa.lavs.mainframe.messages.customer.UpdateCustomerEmployer;
import uoa.lavs.models.Employer;

public class EmployerUpdater {

  private static boolean failed = false;

  public static void updateData(String customerID, Employer employer) {
    try {
      updateMainframe(customerID, employer);
    } catch (Exception e) {
      System.out.println("Mainframe update failed: " + e.getMessage());
      failed = true;
    } finally {
      try {
        updateDatabase(customerID, employer);
      } catch (Exception e) {
        System.out.println("Database update failed: " + e.getMessage());
      } finally {
        if (failed) {
          addFailedUpdate(customerID, employer.getNumber());
        } else {
          addInMainframe(customerID, employer.getNumber());
        }
      }
    }
  }

  public static Integer updateMainframe(String customerID, Employer employer) throws Exception {
    UpdateCustomerEmployer updateCustomerEmployer = new UpdateCustomerEmployer();
    updateCustomerEmployer.setCustomerId(customerID);
    Employer existingEmployer = null;

    if (employer.getNumber() != null) {
      List<Employer> existingEmployers = null;
      try {
        existingEmployers = EmployerFinder.findFromMainframe(customerID);
        for (Employer employerOnAccount : existingEmployers) {
          if (employerOnAccount.getNumber().equals(employer.getNumber())) {
            existingEmployer = employerOnAccount;
            break;
          }
          updateCustomerEmployer.setNumber(null);
        }
      } catch (Exception e) {
        updateCustomerEmployer.setNumber(null);
        System.out.println("Employer %s not in mainframe: " + e.getMessage());
      }
    }

    if (existingEmployer != null) {
          updateCustomerEmployer.setNumber(employer.getNumber());
      updateCustomerEmployer.setName(
          employer.getName() != null ? employer.getName() : existingEmployer.getName());
      updateCustomerEmployer.setLine1(
          employer.getLine1() != null ? employer.getLine1() : existingEmployer.getLine1());
      updateCustomerEmployer.setLine2(
          employer.getLine2() != null ? employer.getLine2() : existingEmployer.getLine2());
      updateCustomerEmployer.setSuburb(
          employer.getSuburb() != null ? employer.getSuburb() : existingEmployer.getSuburb());
      updateCustomerEmployer.setCity(
          employer.getCity() != null ? employer.getCity() : existingEmployer.getCity());
      updateCustomerEmployer.setPostCode(
          employer.getPostCode() != null ? employer.getPostCode() : existingEmployer.getPostCode());
      updateCustomerEmployer.setCountry(
          employer.getCountry() != null ? employer.getCountry() : existingEmployer.getCountry());
      updateCustomerEmployer.setPhoneNumber(
          employer.getPhoneNumber() != null
              ? employer.getPhoneNumber()
              : existingEmployer.getPhoneNumber());
      updateCustomerEmployer.setEmailAddress(
          employer.getEmailAddress() != null
              ? employer.getEmailAddress()
              : existingEmployer.getEmailAddress());
      updateCustomerEmployer.setWebsite(
          employer.getWebsite() != null ? employer.getWebsite() : existingEmployer.getWebsite());
      updateCustomerEmployer.setIsOwner(
          employer.getIsOwner() != null ? employer.getIsOwner() : existingEmployer.getIsOwner());
    } else {
      updateCustomerEmployer.setName(employer.getName());
      updateCustomerEmployer.setLine1(employer.getLine1());
      updateCustomerEmployer.setLine2(employer.getLine2());
      updateCustomerEmployer.setSuburb(employer.getSuburb());
      updateCustomerEmployer.setCity(employer.getCity());
      updateCustomerEmployer.setPostCode(employer.getPostCode());
      updateCustomerEmployer.setCountry(employer.getCountry());
      updateCustomerEmployer.setPhoneNumber(employer.getPhoneNumber());
      updateCustomerEmployer.setEmailAddress(employer.getEmailAddress());
      updateCustomerEmployer.setWebsite(employer.getWebsite());
      updateCustomerEmployer.setIsOwner(employer.getIsOwner());
    }

    Status status = updateCustomerEmployer.send(LocalInstance.getConnection());
    if (!status.getWasSuccessful()) {
      failed = true;
      System.out.println(
          "Something went wrong - the Mainframe send failed! The code is " + status.getErrorCode());
      throw new Exception("Mainframe send failed");
    }
    employer.setNumber(updateCustomerEmployer.getNumberFromServer());
    return updateCustomerEmployer.getNumberFromServer();
  }

  public static void updateDatabase(String customerID, Employer employer) throws SQLException {
    boolean exists = false;
    String CHECK_SQL = "SELECT COUNT(*) FROM Employer WHERE CustomerID = ? AND Number = ?";

    if (customerID != null && employer.getNumber() != null) {
      try (Connection connection = LocalInstance.getDatabaseConnection();
          PreparedStatement checkStatement = connection.prepareStatement(CHECK_SQL)) {
        checkStatement.setString(1, customerID);
        checkStatement.setInt(2, employer.getNumber());
        try (ResultSet resultSet = checkStatement.executeQuery()) {
          if (resultSet.next()) {
            exists = resultSet.getInt(1) > 0;
          }
        }
      }
    }

    String sql;
    if (exists) {
      sql = "UPDATE Employer SET "
          + "Name = COALESCE(?, Name), "
          + "Line1 = COALESCE(?, Line1), "
          + "Line2 = COALESCE(?, Line2), "
          + "Suburb = COALESCE(?, Suburb), "
          + "City = COALESCE(?, City), "
          + "PostCode = COALESCE(?, PostCode), "
          + "Country = COALESCE(?, Country), "
          + "PhoneNumber = COALESCE(?, PhoneNumber), "
          + "EmailAddress = COALESCE(?, EmailAddress), "
          + "Website = COALESCE(?, Website), "
          + "IsOwner = COALESCE(?, IsOwner) "
          + "WHERE CustomerID = ? AND Number = ?";
    } else {
      if (employer.getNumber() == null) {
        String GET_MAX_NUMBER_SQL = "SELECT COALESCE(MAX(Number), 0) + 1 FROM Employer WHERE CustomerID = ?";
        try (Connection connection = LocalInstance.getDatabaseConnection();
            PreparedStatement getMaxNumberStatement = connection.prepareStatement(GET_MAX_NUMBER_SQL)) {
          getMaxNumberStatement.setString(1, customerID);
          try (ResultSet resultSet = getMaxNumberStatement.executeQuery()) {
            if (resultSet.next()) {
              employer.setNumber(resultSet.getInt(1));
            }
          }
        }
      }
      sql = "INSERT INTO Employer (Name, Line1, Line2, Suburb, City, PostCode, Country, PhoneNumber,"
          + " EmailAddress, Website, IsOwner, CustomerID, Number) VALUES (?, ?, ?, ?, ?, ?, ?,"
          + " ?, ?, ?, ?, ?, ?)";
    }

    try (Connection connection = LocalInstance.getDatabaseConnection();
        PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

      statement.setString(1, employer.getName());
      statement.setString(2, employer.getLine1());
      statement.setString(3, employer.getLine2());
      statement.setString(4, employer.getSuburb());
      statement.setString(5, employer.getCity());
      statement.setString(6, employer.getPostCode());
      statement.setString(7, employer.getCountry());
      statement.setString(8, employer.getPhoneNumber());
      statement.setString(9, employer.getEmailAddress());
      statement.setString(10, employer.getWebsite());
      statement.setBoolean(11, employer.getIsOwner());
      statement.setString(12, customerID);
      statement.setInt(13, employer.getNumber());

      statement.executeUpdate();
    }
  }

  private static void addFailedUpdate(String customerID, Integer number) {
    String sql = "UPDATE Employer SET InMainframe = false WHERE CustomerID = ? AND Number = ?";
    try (Connection connection = LocalInstance.getDatabaseConnection();
        PreparedStatement statement = connection.prepareStatement(sql)) {
      statement.setString(1, customerID);
      statement.setInt(2, number);
      statement.executeUpdate();
    } catch (SQLException e) {
      System.out.println("Failed to update database: " + e.getMessage());
    }
  }

  private static void addInMainframe(String customerID, Integer number) {
    String sql = "UPDATE Employer SET InMainframe = true WHERE CustomerID = ? AND Number = ?";
    try (Connection connection = LocalInstance.getDatabaseConnection();
        PreparedStatement statement = connection.prepareStatement(sql)) {
      statement.setString(1, customerID);
      statement.setInt(2, number);
      statement.executeUpdate();
    } catch (SQLException e) {
      System.out.println("Failed to update database: " + e.getMessage());
    }
  }

  public static List<Employer> getFailedUpdates() {
    List<Employer> failedUpdates = new ArrayList<>();
    String sql = "SELECT CustomerID, Number FROM Employer WHERE InMainframe = false";
    try (Connection connection = LocalInstance.getDatabaseConnection();
        PreparedStatement statement = connection.prepareStatement(sql);
        ResultSet resultSet = statement.executeQuery()) {
      while (resultSet.next()) {
        String customerID = resultSet.getString(1);
        Integer number = resultSet.getInt(2);
        List<Employer> employers;
        employers = EmployerFinder.findFromDatabase(customerID);
        for (Employer employerOnAccount : employers) {
          if (employerOnAccount.getNumber().equals(number)) {
            failedUpdates.add(employerOnAccount);
          }
        }
      }
    } catch (Exception e) {
      System.out.println("Failed to get failed updates: " + e.getMessage());
    }
    return failedUpdates;
  }

  public static void retryFailedUpdates() throws Exception {
    List<Employer> failedUpdates = getFailedUpdates();
    for (Employer employer : failedUpdates) {
      String customerID = employer.getCustomerId();
      Integer number = employer.getNumber();
      updateMainframe(customerID, employer);
      addInMainframe(customerID, number);
    }
  }
}
