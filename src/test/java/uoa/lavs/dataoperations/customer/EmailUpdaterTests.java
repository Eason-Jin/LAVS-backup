package uoa.lavs.dataoperations.customer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.junit.jupiter.api.Test;

import uoa.lavs.LocalInstance;
import uoa.lavs.dataoperations.DataOperationsTestsHelper;
import uoa.lavs.models.Email;

public class EmailUpdaterTests {

    @Test
    public void addEmail() throws SQLException {
        DataOperationsTestsHelper.createTestingDatabases();
        Email email = new Email("1", "bob@builder.com", true, null);
        EmailUpdater.updateDatabase("1", email);

        try (PreparedStatement selectStmt = LocalInstance.getDatabaseConnection()
                .prepareStatement("SELECT * FROM email WHERE CustomerID = ? AND Number = ?")) { 
            selectStmt.setString(1, email.getCustomerId());
            selectStmt.setInt(2, 2);
            try (ResultSet resultSet = selectStmt.executeQuery()) {
                assertTrue(resultSet.next());
                assertEquals("1", resultSet.getString("CustomerID"));
                assertEquals("bob@builder.com", resultSet.getString("Address"));
                assertEquals(true, resultSet.getBoolean("IsPrimary"));
                assertEquals(false, resultSet.getBoolean("InMainframe"));
            }
        }

    }

    @Test
    public void updateEmail() throws SQLException {
        DataOperationsTestsHelper.createTestingDatabases();
        Email email = new Email("1", "johndoe@outlook.com", false, 1);
        EmailUpdater.updateDatabase("1", email);
        try (PreparedStatement selectStmt = LocalInstance.getDatabaseConnection()
                .prepareStatement("SELECT * FROM email WHERE CustomerID = ? AND Number = ?")) {
            selectStmt.setString(1, email.getCustomerId());
            selectStmt.setInt(2, email.getNumber());
            try (ResultSet resultSet = selectStmt.executeQuery()) {
                assertTrue(resultSet.next());
                assertEquals("1", resultSet.getString("CustomerID"));
                assertEquals("johndoe@outlook.com", resultSet.getString("Address"));
                assertEquals(false, resultSet.getBoolean("IsPrimary"));
            }
        }
    }
}