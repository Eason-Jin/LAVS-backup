package uoa.lavs.dataoperations.customer;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import uoa.lavs.LocalInstance;
import uoa.lavs.mainframe.Status;
import uoa.lavs.mainframe.messages.customer.LoadCustomerEmployer;
import uoa.lavs.mainframe.messages.customer.LoadCustomerEmployers;
import uoa.lavs.models.Employer;

public class EmployerFinder {

  public static List<Employer> findData(String customerId) {
    List<Employer> employers = new ArrayList<>();
    try {
      employers = findFromMainframe(customerId);
    } catch (Exception e) {
      System.out.println("Mainframe find failed: " + e.getMessage());
      System.out.println("Trying to find from database");
      try {
        employers = findFromDatabase(customerId);
      } catch (Exception e1) {
        System.out.println("Database find failed: " + e1.getMessage());
      }
    }
    return employers;
  }

  public static List<Employer> findFromMainframe(String customerId) throws Exception {
    LoadCustomerEmployers loadEmployers = new LoadCustomerEmployers();
    LoadCustomerEmployer loadEmployer = new LoadCustomerEmployer();
    loadEmployers.setCustomerId(customerId);
    loadEmployer.setCustomerId(customerId);
    Status status = loadEmployers.send(LocalInstance.getConnection());
    if (!status.getWasSuccessful()) {
      System.out.println(
          "Something went wrong - the Mainframe send failed! The code is " + status.getErrorCode());
      throw new Exception("Mainframe send failed");
    }
    Integer employerCount = loadEmployers.getCountFromServer();
    if (employerCount == 0) {
      throw new Exception("Employer not found in mainframe");
    }
    List<Employer> employers = new ArrayList<>(employerCount);

    for (int i = 1; i <= employerCount; i++) {
      Employer employer = new Employer();
      loadEmployer.setNumber(loadEmployers.getNumberFromServer(i));
      loadEmployer.send(LocalInstance.getConnection());
      employer.setCustomerId(customerId);
      employer.setNumber(loadEmployers.getNumberFromServer(i));
      employer.setName(loadEmployers.getNameFromServer(i));
      employer.setLine1(loadEmployer.getLine1FromServer());
      employer.setLine2(loadEmployer.getLine2FromServer());
      employer.setSuburb(loadEmployer.getSuburbFromServer());
      employer.setCity(loadEmployer.getCityFromServer());
      employer.setPostCode(loadEmployer.getPostCodeFromServer());
      employer.setCountry(loadEmployer.getCountryFromServer());
      employer.setPhoneNumber(loadEmployer.getPhoneNumberFromServer());
      employer.setEmailAddress(loadEmployer.getEmailAddressFromServer());
      employer.setWebsite(loadEmployer.getWebsiteFromServer());
      employer.setIsOwner(loadEmployer.getIsOwnerFromServer());
      employers.add(employer);
    }
    return employers;
  }

  public static List<Employer> findFromDatabase(String customerId) throws Exception {
    List<Employer> employers = new ArrayList<>();
    String query = "SELECT * FROM Employer WHERE CustomerID = ?";
    try (Connection connection = LocalInstance.getDatabaseConnection();
        PreparedStatement statement = connection.prepareStatement(query)) {
      statement.setString(1, customerId);
      try (ResultSet resultSet = statement.executeQuery()) {
        while (resultSet.next()) {
          Employer employer = new Employer();
          employer.setCustomerId(resultSet.getString("CustomerID"));
          employer.setNumber(resultSet.getInt("Number"));
          employer.setName(resultSet.getString("Name"));
          employer.setLine1(resultSet.getString("Line1"));
          employer.setLine2(resultSet.getString("Line2"));
          employer.setSuburb(resultSet.getString("Suburb"));
          employer.setCity(resultSet.getString("City"));
          employer.setPostCode(resultSet.getString("PostCode"));
          employer.setCountry(resultSet.getString("Country"));
          employer.setPhoneNumber(resultSet.getString("PhoneNumber"));
          employer.setEmailAddress(resultSet.getString("EmailAddress"));
          employer.setWebsite(resultSet.getString("Website"));
          employer.setIsOwner(resultSet.getBoolean("IsOwner"));
          employers.add(employer);
        }
      }
    }
    if (employers.isEmpty()) {
      throw new Exception("Employer not found in database");
    }
    return employers;
  }
}
