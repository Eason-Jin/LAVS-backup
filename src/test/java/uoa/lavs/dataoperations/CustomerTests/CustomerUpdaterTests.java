package uoa.lavs.dataoperations.customer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import uoa.lavs.LocalInstance;
import uoa.lavs.dataoperations.DataOperationsTestsHelper;
import uoa.lavs.models.Customer;

public class CustomerUpdaterTests {

  @BeforeEach
  public void setup() throws SQLException {
    DataOperationsTestsHelper.createTestingDatabases();
  } 

  @Test
  public void createCustomer() throws SQLException {
    // Arrange
    DataOperationsTestsHelper.createTestingDatabases();
    Customer customer = new Customer(
        null,
        "Ms",
        "Jessica",
        LocalDate.of(2001, 4, 10),
        "Student",
        "NZ",
        "Work",
        null,
        "Note");

    // Act
    CustomerUpdater.updateDatabase(null, customer);

    try (PreparedStatement selectStmt = LocalInstance.getDatabaseConnection()
        .prepareStatement("SELECT * FROM customer WHERE CustomerID = ?")) {
      selectStmt.setString(1, customer.getId());
      try (ResultSet resultSet = selectStmt.executeQuery()) {
        assertTrue(resultSet.next());
        assertEquals("Ms", resultSet.getString("Title"));
        assertEquals("Jessica", resultSet.getString("Name"));
        String dobString = resultSet.getString("Dob");
        LocalDate dob = LocalDate.parse(dobString);
        assertEquals(LocalDate.of(2001, 4, 10), dob);
        assertEquals("Student", resultSet.getString("Occupation"));
        assertEquals("NZ", resultSet.getString("Citizenship"));
        assertEquals("Work", resultSet.getString("VisaType"));
        assertEquals("Pending", resultSet.getString("Status"));
      }
    }
  }

  @Test
  public void updateCustomer() throws SQLException {

    DataOperationsTestsHelper.createTestingDatabases();
    // Arrange
    Customer updatedCustomer = new Customer(
        "1",
        "Dr",
        "Jessie",
        LocalDate.of(1995, 8, 15),
        "Engineer",
        "NZ",
        "Permanent",
        "Active",
        "This customer has not paid bills for 3 months");

    // Act
    CustomerUpdater.updateDatabase("1", updatedCustomer);

    // Assert
    try (PreparedStatement selectStmt = LocalInstance.getDatabaseConnection()
        .prepareStatement("SELECT * FROM customer WHERE CustomerID = ?")) {
      selectStmt.setString(1, "1");
      try (ResultSet resultSet = selectStmt.executeQuery()) {
        assertTrue(resultSet.next());
        assertEquals("Dr", resultSet.getString("Title"));
        assertEquals("Jessie", resultSet.getString("Name"));
        String dobString = resultSet.getString("Dob");
        LocalDate dob = LocalDate.parse(dobString);
        assertEquals(LocalDate.of(1995, 8, 15), dob);
        assertEquals("Engineer", resultSet.getString("Occupation"));
        assertEquals("NZ", resultSet.getString("Citizenship"));
        assertEquals("Permanent", resultSet.getString("VisaType"));
        assertEquals("Active", resultSet.getString("Status"));
      }
    }
  }

  @Test
  public void getFailedCustomerUpdates() throws SQLException {
    DataOperationsTestsHelper.createTestingDatabases();
    CustomerUpdater.getFailedUpdates();

    assertEquals(1, CustomerUpdater.getFailedUpdates().size());
  }
}
