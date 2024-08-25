package uoa.lavs.dataoperations.customer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.junit.jupiter.api.Test;

import uoa.lavs.LocalInstance;
import uoa.lavs.dataoperations.DataOperationsTestsHelper;
import uoa.lavs.models.Employer;

public class EmployerUpdaterTests {

    @Test
    public void addEmployer() throws SQLException {
        DataOperationsTestsHelper.createTestingDatabases();
        Employer employer = new Employer("1", "UoA", "35 Symonds Street", null, "Auckland", "Auckland", "1010",
                "New Zealand", "0934534345", "engineering@uoa.ac.nz", "www.uoa.ac.nz", false, null);
        EmployerUpdater.updateDatabase("1", employer);

        try (PreparedStatement selectStmt = LocalInstance.getDatabaseConnection()
                .prepareStatement("SELECT * FROM Employer WHERE CustomerID = ? AND Number = ?")) {
            selectStmt.setString(1, employer.getCustomerId());
            selectStmt.setInt(2, 2);
            try (ResultSet resultSet = selectStmt.executeQuery()) {
                assertTrue(resultSet.next());
                assertEquals("1", resultSet.getString("CustomerID"));
                assertEquals("UoA", resultSet.getString("Name"));
                assertEquals("35 Symonds Street", resultSet.getString("Line1"));
                assertEquals("Auckland", resultSet.getString("Suburb"));
                assertEquals("Auckland", resultSet.getString("City"));
                assertEquals("1010", resultSet.getString("Postcode"));
                assertEquals("New Zealand", resultSet.getString("Country"));
                assertEquals("0934534345", resultSet.getString("PhoneNumber"));
                assertEquals("engineering@uoa.ac.nz", resultSet.getString("EmailAddress"));
                assertEquals("www.uoa.ac.nz", resultSet.getString("Website"));
                assertEquals(false, resultSet.getBoolean("IsOwner"));
            }
        }
    }

    @Test
    public void updateEmployer() throws SQLException {
        DataOperationsTestsHelper.createTestingDatabases();
        Employer employer = new Employer();
        employer.setCustomerId("1");
        employer.setName("Updated Tech Corp");
        employer.setLine1("456 Updated Street");
        employer.setSuburb("Updated Suburb");
        employer.setCity("Updated City");
        employer.setPostCode("67890");
        employer.setCountry("Updated Country");
        employer.setPhoneNumber("0987654321");
        employer.setEmailAddress("updated@tech.com");
        employer.setWebsite("www.updatedtech.com");
        employer.setIsOwner(false);
        EmployerUpdater.updateDatabase("1", employer);

        try (PreparedStatement selectStmt = LocalInstance.getDatabaseConnection()
                .prepareStatement("SELECT * FROM Employer WHERE CustomerID = ? AND Number = ?")) {
            selectStmt.setString(1, employer.getCustomerId());
            selectStmt.setInt(2, employer.getNumber());
            try (ResultSet resultSet = selectStmt.executeQuery()) {
                assertTrue(resultSet.next());
                assertEquals("1", resultSet.getString("CustomerID"));
                assertEquals("Updated Tech Corp", resultSet.getString("Name"));
                assertEquals("456 Updated Street", resultSet.getString("Line1"));
                assertEquals("Updated Suburb", resultSet.getString("Suburb"));
                assertEquals("Updated City", resultSet.getString("City"));
                assertEquals("67890", resultSet.getString("Postcode"));
                assertEquals("Updated Country", resultSet.getString("Country"));
                assertEquals("0987654321", resultSet.getString("PhoneNumber"));
                assertEquals("updated@tech.com", resultSet.getString("EmailAddress"));
                assertEquals("www.updatedtech.com", resultSet.getString("Website"));
                assertEquals(false, resultSet.getBoolean("IsOwner"));
                assertEquals(false, resultSet.getBoolean("InMainframe"));
            }
        }
    }
}