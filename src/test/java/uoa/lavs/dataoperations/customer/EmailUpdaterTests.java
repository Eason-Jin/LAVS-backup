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
import uoa.lavs.models.Email;

public class EmailUpdaterTests {

    @Test
    public void addEmailToDatabase() throws SQLException {
        DataOperationsTestsHelper.createTestingDatabases();
        Email email = new Email("1", "bob@builder.com", true, null);

        EmailUpdater emailUpdater = new EmailUpdater();
        EmailUpdater.updateDatabase("1", email);

        try (PreparedStatement selectStmt = LocalInstance.getDatabaseConnection()
                .prepareStatement("SELECT * FROM email WHERE CustomerID = ? AND Number = ?")) {
            selectStmt.setString(1, email.getCustomerId());
            selectStmt.setInt(2, 2);
            try (ResultSet resultSet = selectStmt.executeQuery()) {
                assertTrue(resultSet.next());
                assertAll("email",
                        () -> assertEquals("1", resultSet.getString("CustomerID")),
                        () -> assertEquals("john@gmail.com", resultSet.getString("Address")),
                        () -> assertEquals(true, resultSet.getBoolean("IsPrimary")),
                        () -> assertEquals(false, resultSet.getBoolean("InMainframe")));
            }
        }
    }

    @Test
    public void updateEmailInDatabase() throws SQLException {
        DataOperationsTestsHelper.createTestingDatabases();
        Email email = new Email("1", "johndoe@outlook.com", false, 1);
        EmailUpdater.updateDatabase("1", email);

        try (PreparedStatement selectStmt = LocalInstance.getDatabaseConnection()
                .prepareStatement("SELECT * FROM email WHERE CustomerID = ? AND Number = ?")) {
            selectStmt.setString(1, email.getCustomerId());
            selectStmt.setInt(2, email.getNumber());
            try (ResultSet resultSet = selectStmt.executeQuery()) {
                assertTrue(resultSet.next());
                assertAll("email",
                        () -> assertEquals("1", resultSet.getString("CustomerID")),
                        () -> assertEquals("johndoe@outlook.com", resultSet.getString("Address")),
                        () -> assertEquals(false, resultSet.getBoolean("IsPrimary")));
            }
        }
    }

    @Test
    public void updateEmailInMainframe() throws Exception {
        DataOperationsTestsHelper.createTestingDatabases();
        Email email = new Email("123", "test@example.com", true, 1);
        EmailUpdater.updateMainframe("123", email);

        List<Email> emails = EmailFinder.findFromMainframe("123");
        assertAll("email",
                () -> assertEquals("123", emails.get(0).getCustomerId()),
                () -> assertEquals("test@example.com", emails.get(0).getAddress()),
                () -> assertEquals(true, emails.get(0).getIsPrimary()));
    }

    @Test
    public void updateEmailWithNoChangesInMainframe() throws Exception {
        DataOperationsTestsHelper.createTestingDatabases();
        Email email = new Email();
        email.setCustomerId("123");
        email.setNumber(1);
        EmailUpdater.updateMainframe("123", email);
        List<Email> emails = EmailFinder.findFromMainframe("123");
        assertAll("email",
                () -> assertEquals("123", emails.get(0).getCustomerId()),
                () -> assertEquals("noone@nowhere.co.no", emails.get(0).getAddress()),
                () -> assertEquals(true, emails.get(0).getIsPrimary()));
    }

    @Test
    public void addEmailInMainframe() throws Exception {
        DataOperationsTestsHelper.createTestingDatabases();
        Email email = new Email();
        email.setCustomerId("123");
        email.setAddress("test@example.com");
        email.setIsPrimary(false);
        EmailUpdater.updateMainframe("123", email);
        List<Email> emails = EmailFinder.findFromMainframe("123");
        for (Email e : emails) {
            if (e.getAddress().equals(email.getAddress())) {
                assertEquals("123", e.getCustomerId());
                assertEquals("test@example.com", e.getAddress());
                assertEquals(false, e.getIsPrimary());
            }
        }
    }

    @Test
    public void updateNonExistingEmailInMainframe() throws Exception {
        DataOperationsTestsHelper.createTestingDatabases();
        Email email = new Email();
        email.setCustomerId("1000");
        email.setAddress("nonexistent@example.com");
        email.setIsPrimary(false);
        assertThrows(Exception.class, () -> {
            EmailUpdater.updateMainframe("1000", email);
        });
    }

    @Test
    public void addInvalidEmail() throws SQLException {
        DataOperationsTestsHelper.createTestingDatabases();
        String customerId = null;
        assertThrows(Exception.class, () -> {
            EmailUpdater.updateData(customerId, null);
        });
    }

    @Test
    public void addEmail() throws Exception {
        DataOperationsTestsHelper.createTestingDatabases();

        String customerId = "654";
        Email email = new Email("654", "test@example.com", true, null);

        EmailUpdater.updateData(customerId, email);

        List<Email> emails = EmailFinder.findData(customerId);
        for (Email e : emails) {
            if (e.getAddress().equals(email.getAddress())) {
                assertEquals("654", e.getCustomerId());
                assertEquals("test@example.com", e.getAddress());
                assertEquals(true, e.getIsPrimary());
            }
        }
    }

    @Test
    public void retryFailedUpdates() throws Exception {
        DataOperationsTestsHelper.createTestingDatabases();
        CustomerUpdater.retryFailedUpdates();
        EmailUpdater.retryFailedUpdates();
        try (PreparedStatement selectStmt = LocalInstance.getDatabaseConnection()
                .prepareStatement("SELECT * FROM email WHERE InMainframe = false")) {
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
        List<Email> failedUpdates = EmailUpdater.getFailedUpdates();
        assertEquals(2, failedUpdates.size());
    }
}
