package uoa.lavs.dataoperations;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import uoa.lavs.mainframe.Instance;
import uoa.lavs.mainframe.Status;
import uoa.lavs.mainframe.messages.customer.LoadCustomerEmployer;
import uoa.lavs.models.Employer;

public class EmployerLoader {

  public static Employer loadData(String customerId, int number) {
    Employer employer = new Employer();
    try {
      employer = loadFromMainframe(customerId, number);
      if (employer.getName() == null) {
        throw new Exception("Employer not in mainframe");
      }
    } catch (Exception e) {
      System.out.println("Mainframe load failed: " + e.getMessage());
      System.out.println("Trying to load from database");
      try {
        employer = loadFromDatabase(customerId, number);
      } catch (Exception e1) {
        System.out.println("Database load failed: " + e1.getMessage());
      }
    }
    return employer;
  }

  private static Employer loadFromDatabase(String customerId, int number) throws Exception {
    Employer employer = new Employer();
    String query = "SELECT * FROM Employer WHERE CustomerID = ? AND Number = ?";
    try (Connection connection = Instance.getDatabaseConnection();
        PreparedStatement statement = connection.prepareStatement(query)) {
      statement.setString(1, customerId);
      statement.setInt(2, number);
      try (ResultSet resultSet = statement.executeQuery()) {
        if (!resultSet.next()) {
          throw new Exception("Employer not in database");
        }
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
      }
    }
    return employer;
  }

  private static Employer loadFromMainframe(String customerId, int number) throws Exception {
    LoadCustomerEmployer loadCustomerEmployer = new LoadCustomerEmployer();
    loadCustomerEmployer.setCustomerId(customerId);
    loadCustomerEmployer.setNumber(number);
    Status status = loadCustomerEmployer.send(Instance.getConnection());
    if (!status.getWasSuccessful()) {
      System.out.println(
          "Something went wrong - the Mainframe send failed! The code is " + status.getErrorCode());
      throw new Exception("Mainframe send failed");
    }

    Employer employer = new Employer();
    employer.setCustomerId(customerId);
    employer.setNumber(number);
    employer.setName(loadCustomerEmployer.getNameFromServer());
    employer.setLine1(loadCustomerEmployer.getLine1FromServer());
    employer.setLine2(loadCustomerEmployer.getLine2FromServer());
    employer.setSuburb(loadCustomerEmployer.getSuburbFromServer());
    employer.setCity(loadCustomerEmployer.getCityFromServer());
    employer.setPostCode(loadCustomerEmployer.getPostCodeFromServer());
    employer.setCountry(loadCustomerEmployer.getCountryFromServer());
    employer.setPhoneNumber(loadCustomerEmployer.getPhoneNumberFromServer());
    employer.setEmailAddress(loadCustomerEmployer.getEmailAddressFromServer());
    employer.setWebsite(loadCustomerEmployer.getWebsiteFromServer());
    employer.setIsOwner(loadCustomerEmployer.getIsOwnerFromServer());
    return employer;
  }
}
