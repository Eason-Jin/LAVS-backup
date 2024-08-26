package uoa.lavs.dataoperations.customer;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.junit.jupiter.api.Test;

import uoa.lavs.LocalInstance;
import uoa.lavs.dataoperations.DataOperationsTestsHelper;
import uoa.lavs.models.Employer;

public class EmployerUpdaterTests {

    @Test
    public void addEmployerToDatabase() throws SQLException {
        DataOperationsTestsHelper.createTestingDatabases();
        Employer employer = new Employer("1", "UoA", "35 Symonds Street", null, "Auckland", "Auckland", "1010",
                "New Zealand", "0934534345", "engineering@uoa.ac.nz", "www.uoa.ac.nz", false, null);

        EmployerUpdater employerUpdater = new EmployerUpdater();
        EmployerUpdater.updateDatabase("1", employer);

        try (PreparedStatement selectStmt = LocalInstance.getDatabaseConnection()
                .prepareStatement("SELECT * FROM Employer WHERE CustomerID = ? AND Number = ?")) {
            selectStmt.setString(1, employer.getCustomerId());
            selectStmt.setInt(2, 3);
            try (ResultSet resultSet = selectStmt.executeQuery()) {
                assertTrue(resultSet.next());
                assertAll("employer",
                        () -> assertEquals("1", resultSet.getString("CustomerID")),
                        () -> assertEquals("UoA", resultSet.getString("Name")),
                        () -> assertEquals("35 Symonds Street", resultSet.getString("Line1")),
                        () -> assertEquals("Auckland", resultSet.getString("Suburb")),
                        () -> assertEquals("Auckland", resultSet.getString("City")),
                        () -> assertEquals("1010", resultSet.getString("Postcode")),
                        () -> assertEquals("New Zealand", resultSet.getString("Country")),
                        () -> assertEquals("0934534345", resultSet.getString("PhoneNumber")),
                        () -> assertEquals("engineering@uoa.ac.nz", resultSet.getString("EmailAddress")),
                        () -> assertEquals("www.uoa.ac.nz", resultSet.getString("Website")),
                        () -> assertEquals(false, resultSet.getBoolean("IsOwner")));
            }
        }
    }

    @Test
    public void updateEmployerInDatabase() throws SQLException {
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
        employer.setNumber(1);
        EmployerUpdater.updateDatabase("1", employer);

        try (PreparedStatement selectStmt = LocalInstance.getDatabaseConnection()
                .prepareStatement("SELECT * FROM Employer WHERE CustomerID = ? AND Number = ?")) {
            selectStmt.setString(1, employer.getCustomerId());
            selectStmt.setInt(2, employer.getNumber());
            try (ResultSet resultSet = selectStmt.executeQuery()) {
                assertTrue(resultSet.next());
                assertAll("employer",
                        () -> assertEquals("1", resultSet.getString("CustomerID")),
                        () -> assertEquals("Updated Tech Corp", resultSet.getString("Name")),
                        () -> assertEquals("456 Updated Street", resultSet.getString("Line1")),
                        () -> assertEquals("Updated Suburb", resultSet.getString("Suburb")),
                        () -> assertEquals("Updated City", resultSet.getString("City")),
                        () -> assertEquals("67890", resultSet.getString("Postcode")),
                        () -> assertEquals("Updated Country", resultSet.getString("Country")),
                        () -> assertEquals("0987654321", resultSet.getString("PhoneNumber")),
                        () -> assertEquals("updated@tech.com", resultSet.getString("EmailAddress")),
                        () -> assertEquals("www.updatedtech.com", resultSet.getString("Website")),
                        () -> assertEquals(false, resultSet.getBoolean("IsOwner")),
                        () -> assertEquals(false, resultSet.getBoolean("InMainframe")));
            }
        }
    }

    @Test
    public void updateEmployerInMainframe() throws Exception {
        DataOperationsTestsHelper.createTestingDatabases();
        Employer employer = new Employer("123", "Tech Corp", "32 Grafton Road", "UoA", "Auckland", "Auckland", "1010",
                "New Zealand", "0934534345", "techcorp@tech.com", "www.techcorp.com", true, 1);
        EmployerUpdater.updateMainframe("123", employer);

        List<Employer> employers = EmployerFinder.findFromMainframe("123");
        assertAll("employer",
                () -> assertEquals("123", employers.get(0).getCustomerId()),
                () -> assertEquals("Tech Corp", employers.get(0).getName()),
                () -> assertEquals("32 Grafton Road", employers.get(0).getLine1()),
                () -> assertEquals("UoA", employers.get(0).getLine2()),
                () -> assertEquals("Auckland", employers.get(0).getSuburb()),
                () -> assertEquals("Auckland", employers.get(0).getCity()),
                () -> assertEquals("1010", employers.get(0).getPostCode()),
                () -> assertEquals("New Zealand", employers.get(0).getCountry()),
                () -> assertEquals("0934534345", employers.get(0).getPhoneNumber()),
                () -> assertEquals("techcorp@tech.com", employers.get(0).getEmailAddress()),
                () -> assertEquals("www.techcorp.com", employers.get(0).getWebsite()),
                () -> assertEquals(true, employers.get(0).getIsOwner()));
    }

    @Test
    public void updateEmployerWithNoChangesInMainframe() throws Exception {
        DataOperationsTestsHelper.createTestingDatabases();
        Employer employer = new Employer();
        employer.setCustomerId("123");
        employer.setNumber(1);
        EmployerUpdater.updateMainframe("123", employer);
        List<Employer> employers = EmployerFinder.findFromMainframe("123");
        assertAll("employer",
                () -> assertEquals("123", employers.get(0).getCustomerId()),
                () -> assertEquals("The Best Employer", employers.get(0).getName()),
                () -> assertEquals("5 Somewhere Lane", employers.get(0).getLine1()),
                () -> assertEquals("Nowhere", employers.get(0).getLine2()),
                () -> assertEquals("bigboss@nowhere.co.no", employers.get(0).getEmailAddress()),
                () -> assertEquals("http://nowhere.co.no", employers.get(0).getWebsite()),
                () -> assertEquals(true, employers.get(0).getIsOwner()));
    }

    @Test
    public void addEmployerInMainframe() throws Exception {
        DataOperationsTestsHelper.createTestingDatabases();
        Employer employer = new Employer("123", "Tech Corp", "32 Grafton Road", null, "Auckland", "Auckland", "1010",
                "New Zealand", "0934534345", "techcorp@tech.com", "www.techcorp.com", true, 1);
        EmployerUpdater.updateMainframe("123", employer);

        EmployerUpdater.updateMainframe("123", employer);
        List<Employer> employers = EmployerFinder.findFromMainframe("123");
        for (Employer e : employers) {
            if (e.getNumber().equals(employer.getNumber())) {
                assertAll("employer",
                        () -> assertEquals("123", employers.get(0).getCustomerId()),
                        () -> assertEquals("Tech Corp", employers.get(0).getName()),
                        () -> assertEquals("32 Grafton Road", employers.get(0).getLine1()),
                        () -> assertEquals("Auckland", employers.get(0).getSuburb()),
                        () -> assertEquals("Auckland", employers.get(0).getCity()),
                        () -> assertEquals("1010", employers.get(0).getPostCode()),
                        () -> assertEquals("New Zealand", employers.get(0).getCountry()),
                        () -> assertEquals("0934534345", employers.get(0).getPhoneNumber()),
                        () -> assertEquals("techcorp@tech.com", employers.get(0).getEmailAddress()),
                        () -> assertEquals("www.techcorp.com", employers.get(0).getWebsite()),
                        () -> assertEquals(true, employers.get(0).getIsOwner()));
            }
        }
    }

    @Test
    public void updateNonExistingEmployerInMainframe() throws Exception {
        DataOperationsTestsHelper.createTestingDatabases();
        Employer employer = new Employer();
        employer.setCustomerId("1000");
        employer.setIsOwner(false);
        assertThrows(Exception.class, () -> {
            EmployerUpdater.updateMainframe("1000", employer);
        });
    }

    @Test
    public void addInvalidEmployer() throws SQLException {
        DataOperationsTestsHelper.createTestingDatabases();
        String customerId = null;
        assertThrows(Exception.class, () -> {
            EmployerUpdater.updateData(customerId, null);
        });
    }

    @Test
    public void addEmployer() throws Exception {
        DataOperationsTestsHelper.createTestingDatabases();

        String customerId = "123";
        Employer employer = new Employer("123", "Tech Corp", "32 Grafton Road", null, "Auckland", "Auckland", "1010",
                "New Zealand", "0934534345", "techcorp@tech.com", "www.techcorp.com", true, 1);

        EmployerUpdater.updateData(customerId, employer);

        List<Employer> employers = EmployerFinder.findData(customerId);
        for (Employer e : employers) {
            if (e.getNumber().equals(employer.getNumber())) {
                assertAll("employer",
                        () -> assertEquals("123", employers.get(0).getCustomerId()),
                        () -> assertEquals("Tech Corp", employers.get(0).getName()),
                        () -> assertEquals("32 Grafton Road", employers.get(0).getLine1()),
                        () -> assertEquals("Auckland", employers.get(0).getSuburb()),
                        () -> assertEquals("Auckland", employers.get(0).getCity()),
                        () -> assertEquals("1010", employers.get(0).getPostCode()),
                        () -> assertEquals("New Zealand", employers.get(0).getCountry()),
                        () -> assertEquals("0934534345", employers.get(0).getPhoneNumber()),
                        () -> assertEquals("techcorp@tech.com", employers.get(0).getEmailAddress()),
                        () -> assertEquals("www.techcorp.com", employers.get(0).getWebsite()),
                        () -> assertEquals(true, employers.get(0).getIsOwner()));
            }
        }
    }

    @Test
    public void retryFailedUpdates() throws Exception {
        DataOperationsTestsHelper.createTestingDatabases();
        CustomerUpdater.retryFailedUpdates();
        EmployerUpdater.retryFailedUpdates();
        try (PreparedStatement selectStmt = LocalInstance.getDatabaseConnection()
                .prepareStatement("SELECT * FROM employer WHERE InMainframe = false")) {
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
    public void getFailedUpdates() throws Exception {
        DataOperationsTestsHelper.createTestingDatabases();
        List<Employer> failedUpdates = EmployerUpdater.getFailedUpdates();
        assertEquals(2, failedUpdates.size());
    }
}