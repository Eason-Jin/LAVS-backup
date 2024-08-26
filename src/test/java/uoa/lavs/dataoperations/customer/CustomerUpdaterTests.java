package uoa.lavs.dataoperations.customer;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import org.junit.jupiter.api.Test;

import uoa.lavs.LocalInstance;
import uoa.lavs.dataoperations.DataOperationsTestsHelper;
import uoa.lavs.models.Customer;

public class CustomerUpdaterTests {

  @Test
  public void createCustomerInDatabase() throws SQLException {
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
    CustomerUpdater customerUpdater = new CustomerUpdater();
    CustomerUpdater.updateDatabase(null, customer);

    try (PreparedStatement selectStmt = LocalInstance.getDatabaseConnection()
        .prepareStatement("SELECT * FROM customer WHERE CustomerID = ?")) {
      selectStmt.setString(1, customer.getId());
      try (ResultSet resultSet = selectStmt.executeQuery()) {
        assertTrue(resultSet.next());
        assertAll("customer",
            () -> assertEquals("Ms", resultSet.getString("Title")),
            () -> assertEquals("Jessica", resultSet.getString("Name")),
            () -> {
              String dobString = resultSet.getString("Dob");
              LocalDate dob = LocalDate.parse(dobString);
              assertEquals(LocalDate.of(2001, 4, 10), dob);
            },
            () -> assertEquals("Student", resultSet.getString("Occupation")),
            () -> assertEquals("NZ", resultSet.getString("Citizenship")),
            () -> assertEquals("Work", resultSet.getString("VisaType")),
            () -> assertEquals("Pending", resultSet.getString("Status")));
      }
    }
  }

  @Test
  public void updateCustomerInDatabase() throws SQLException {

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
        assertAll("customer",
            () -> assertEquals("Dr", resultSet.getString("Title")),
            () -> assertEquals("Jessie", resultSet.getString("Name")),
            () -> {
              String dobString = resultSet.getString("Dob");
              LocalDate dob = LocalDate.parse(dobString);
              assertEquals(LocalDate.of(1995, 8, 15), dob);
            },
            () -> assertEquals("Engineer", resultSet.getString("Occupation")),
            () -> assertEquals("NZ", resultSet.getString("Citizenship")),
            () -> assertEquals("Permanent", resultSet.getString("VisaType")),
            () -> assertEquals("Active", resultSet.getString("Status")));
      }
    }
  }

  @Test
  public void updateCustomerInMainframe() throws Exception {
    // Arrange
    DataOperationsTestsHelper.createTestingDatabases();
    Customer customer = new Customer(
        "123",
        "Dr",
        "Jessie",
        LocalDate.of(1995, 8, 15),
        "Engineer",
        "NZ",
        "Permanent",
        "Active",
        "This customer has not paid bills for 3 months");

    // Act
    CustomerUpdater.updateMainframe("123", customer);

    CustomerLoader.loadData("123");
    assertAll("customer",
        () -> assertEquals("Dr", customer.getTitle()),
        () -> assertEquals("Jessie", customer.getName()),
        () -> assertEquals(LocalDate.of(1995, 8, 15), customer.getDob()),
        () -> assertEquals("Engineer", customer.getOccupation()),
        () -> assertEquals("NZ", customer.getCitizenship()),
        () -> assertEquals("Permanent", customer.getVisaType()),
        () -> assertEquals("Active", customer.getStatus()));
  }

  @Test
  public void updateCustomerWithNoChangesInMainframe() throws Exception {
    // Arrange
    DataOperationsTestsHelper.createTestingDatabases();
    Customer customer = new Customer();
    customer.setId("123");
    CustomerUpdater.updateMainframe("123", customer);
    Customer loadedCustomer = CustomerLoader.loadData("123");
    assertAll("customer",
        () -> assertEquals("Mr", loadedCustomer.getTitle()),
        () -> assertEquals("John Doe", loadedCustomer.getName()),
        () -> assertEquals(LocalDate.of(1945, 3, 12), loadedCustomer.getDob()),
        () -> assertEquals("Test dummy", loadedCustomer.getOccupation()),
        () -> assertEquals("New Zealand", loadedCustomer.getCitizenship()),
        () -> assertEquals("n/a", loadedCustomer.getVisaType()),
        () -> assertEquals("Active", loadedCustomer.getStatus()));
  }

  @Test
  public void addCustomerInMainframe() throws Exception {
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
        "Customer called to inquire about the status of their recent order. They expressed concern over a potential delay in delivery and requested an update on the expected arrival date. After reviewing the account details, I confirmed that the order is currently being processed and assured the customer that it is on track for delivery by the promised date. I also provided them with a tracking number and informed them that they would receive an email notification once the order has been shipped. The customer was satisfied with the information and thanked us for the prompt assistance.");

    CustomerUpdater.updateMainframe(null, customer);
    Customer loadedCustomer = CustomerLoader.loadData(customer.getId());
    assertAll("customer",
        () -> assertEquals("Ms", loadedCustomer.getTitle()),
        () -> assertEquals("Jessica", loadedCustomer.getName()),
        () -> assertEquals(LocalDate.of(2001, 4, 10), loadedCustomer.getDob()),
        () -> assertEquals("Student", loadedCustomer.getOccupation()),
        () -> assertEquals("NZ", loadedCustomer.getCitizenship()),
        () -> assertEquals("Work", loadedCustomer.getVisaType()),
        () -> assertEquals("Active", loadedCustomer.getStatus()),
        () -> assertEquals(
            "Customer called to inquire about the status of their recent order. They expressed concern over a potential delay in delivery and requested an update on the expected arrival date. After reviewing the account details, I confirmed that the order is currently being processed and assured the customer that it is on track for delivery by the promised date. I also provided them with a tracking number and informed them that they would receive an email notification once the order has been shipped. The customer was satisfied with the information and thanked us for the prompt assistance.",
            loadedCustomer.getNotes()));
  }

  @Test
  public void updateNonExistingCustomerInMainframe() throws Exception {
    DataOperationsTestsHelper.createTestingDatabases();

    assertThrows(Exception.class, () -> {
      CustomerUpdater.updateMainframe("1000", null);
    });
  }

  @Test
  public void addCustomer() throws SQLException {
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

    CustomerUpdater.updateData(null, customer);

    CustomerLoader.loadData(customer.getId());
    assertAll("customer",
        () -> assertEquals("Ms", customer.getTitle()),
        () -> assertEquals("Jessica", customer.getName()),
        () -> assertEquals(LocalDate.of(2001, 4, 10), customer.getDob()),
        () -> assertEquals("Student", customer.getOccupation()),
        () -> assertEquals("NZ", customer.getCitizenship()),
        () -> assertEquals("Work", customer.getVisaType()),
        () -> assertEquals("Active", customer.getStatus()),
        () -> assertEquals("Note", customer.getNotes()));
  }

  @Test
  public void retryFailedUpdates() throws Exception {
    DataOperationsTestsHelper.createTestingDatabases();
    CustomerUpdater.retryFailedUpdates();
    try (PreparedStatement selectStmt = LocalInstance.getDatabaseConnection()
        .prepareStatement("SELECT * FROM customer WHERE InMainframe = false")) {
      try (ResultSet resultSet = selectStmt.executeQuery()) {
        int rowCount = 0;
        while (resultSet.next()) {
          rowCount++;
        }
        assertEquals(0, rowCount);
      }
    }
  }

  @Test
  public void getFailedCustomerUpdates() throws SQLException {
    DataOperationsTestsHelper.createTestingDatabases();
    CustomerUpdater.getFailedUpdates();

    assertEquals(2, CustomerUpdater.getFailedUpdates().size());
  }

  @Test
  public void updateCustomerWithInvalidData() throws Exception {
    DataOperationsTestsHelper.createTestingDatabases();

    assertThrows(Exception.class, () -> {
      CustomerUpdater.updateData("100000000000", null);
    });
  }
}