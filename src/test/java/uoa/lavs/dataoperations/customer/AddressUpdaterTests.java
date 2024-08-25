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
import uoa.lavs.models.Address;

public class AddressUpdaterTests {

    @Test
    public void addAddressInDatabase() throws SQLException {
        DataOperationsTestsHelper.createTestingDatabases();
        Address address = new Address("1", "Business", "36", "Main Street", "Springfield", "IL", "62701", "USA", true,
                false, null);
        AddressUpdater.updateDatabase("1", address);

        try (PreparedStatement selectStmt = LocalInstance.getDatabaseConnection()
                .prepareStatement("SELECT * FROM address WHERE CustomerID = ? AND Number = ?")) {
            selectStmt.setString(1, address.getCustomerId());
            selectStmt.setInt(2, 2);
            try (ResultSet resultSet = selectStmt.executeQuery()) {
                assertTrue(resultSet.next());
                assertAll("address",
                        () -> assertEquals("1", resultSet.getString("CustomerID")),
                        () -> assertEquals(2, resultSet.getInt("Number")),
                        () -> assertEquals("36", resultSet.getString("Line1")),
                        () -> assertEquals("Main Street", resultSet.getString("Line2")),
                        () -> assertEquals("Springfield", resultSet.getString("Suburb")),
                        () -> assertEquals("IL", resultSet.getString("City")),
                        () -> assertEquals("62701", resultSet.getString("Postcode")),
                        () -> assertEquals("USA", resultSet.getString("Country")),
                        () -> assertEquals(true, resultSet.getBoolean("IsPrimary")),
                        () -> assertEquals(false, resultSet.getBoolean("IsMailing")));
            }
        }
    }

    @Test
    public void updateAddressInDatabase() throws SQLException {
        DataOperationsTestsHelper.createTestingDatabases();
        Address address = new Address();
        address.setCustomerId("1");
        address.setNumber(1);
        address.setType("Business");
        address.setLine1("Elm St");
        address.setIsPrimary(false);
        address.setIsMailing(false);
        AddressUpdater.updateDatabase("1", address);
        try (PreparedStatement selectStmt = LocalInstance.getDatabaseConnection()
                .prepareStatement("SELECT * FROM address WHERE CustomerID = ? AND Number = ?")) {
            selectStmt.setString(1, address.getCustomerId());
            selectStmt.setInt(2, address.getNumber());
            try (ResultSet resultSet = selectStmt.executeQuery()) {
                assertTrue(resultSet.next());
                assertAll("address",
                        () -> assertEquals("1", resultSet.getString("CustomerID")),
                        () -> assertEquals("Elm St", resultSet.getString("Line1")),
                        () -> assertEquals("Auckland", resultSet.getString("City")),
                        () -> assertEquals(false, resultSet.getBoolean("IsPrimary")));
            }
        }
    }
}