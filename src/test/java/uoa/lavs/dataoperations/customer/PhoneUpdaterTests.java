package uoa.lavs.dataoperations.customer;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.junit.jupiter.api.Test;

import uoa.lavs.LocalInstance;
import uoa.lavs.dataoperations.DataOperationsTestsHelper;
import uoa.lavs.models.Phone;

public class PhoneUpdaterTests {

    @Test
    public void addPhone() throws SQLException {
        DataOperationsTestsHelper.createTestingDatabases();
        Phone phone = new Phone("1", "Landline", "09", "12345678", true, false, null);
        PhoneUpdater.updateDatabase("1", phone);

        try (PreparedStatement selectStmt = LocalInstance.getDatabaseConnection()
                .prepareStatement("SELECT * FROM phone WHERE CustomerID = ? AND Number = ?")) {
            selectStmt.setString(1, phone.getCustomerId());
            selectStmt.setInt(2, 2); // Assuming the number is 2 for this test case
            try (ResultSet resultSet = selectStmt.executeQuery()) {
                assertTrue(resultSet.next());
                assertAll("phone",
                        () -> assertEquals("1", resultSet.getString("CustomerID")),
                        () -> assertEquals("12345678", resultSet.getString("PhoneNumber")),
                        () -> assertEquals(true, resultSet.getBoolean("IsPrimary")),
                        () -> assertEquals(false, resultSet.getBoolean("InMainframe")),
                        () -> assertEquals(2, resultSet.getInt("Number")));
            }
        }
    }

    @Test
    public void updatePhone() throws SQLException {
        DataOperationsTestsHelper.createTestingDatabases();
        Phone phone = new Phone("1", "Mobile", "64", "987654", true, false, 1);
        PhoneUpdater.updateDatabase("1", phone);

        try (PreparedStatement selectStmt = LocalInstance.getDatabaseConnection()
                .prepareStatement("SELECT * FROM phone WHERE CustomerID = ? AND Number = ?")) {
            selectStmt.setString(1, phone.getCustomerId());
            selectStmt.setInt(2, phone.getNumber());
            try (ResultSet resultSet = selectStmt.executeQuery()) {
                assertTrue(resultSet.next());
                assertAll("phone",
                        () -> assertEquals("1", resultSet.getString("CustomerID")),
                        () -> assertEquals("987654", resultSet.getString("PhoneNumber")),
                        () -> assertEquals(true, resultSet.getBoolean("IsPrimary")),
                        () -> assertEquals(false, resultSet.getBoolean("CanSendText")));
            }
        }
    }
}