package uoa.lavs.dataoperations.customer;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.junit.jupiter.api.Test;

import uoa.lavs.LocalInstance;
import uoa.lavs.dataoperations.DataOperationsTestsHelper;
import uoa.lavs.models.Address;

public class AddressUpdaterTests {

    @Test
    public void addAddressInDatabase() throws SQLException {
        DataOperationsTestsHelper.createTestingDatabases();
        AddressUpdater addressUpdater = new AddressUpdater();
        Address address = new Address("1", "Business", "36", "Main Street", "Springfield", "IL", "62701", "USA", true,
                false, null);
        addressUpdater.updateDatabase("1", address);

        try (PreparedStatement selectStmt = LocalInstance.getDatabaseConnection()
                .prepareStatement("SELECT * FROM address WHERE CustomerID = ? AND Number = ?")) {
            selectStmt.setString(1, address.getCustomerId());
            selectStmt.setInt(2, 3);
            try (ResultSet resultSet = selectStmt.executeQuery()) {
                assertTrue(resultSet.next());
                assertAll("address",
                        () -> assertEquals("1", resultSet.getString("CustomerID")),
                        () -> assertEquals(3, resultSet.getInt("Number")),
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

    @Test
    public void updateAddressInMainframe() throws Exception {
        DataOperationsTestsHelper.createTestingDatabases();
        Address address = new Address("123", "Business", "36", "Main Street", "Springfield", "IL", "62701", "USA", true,
                false, 1);

        Integer number = AddressUpdater.updateMainframe("123", address);
        assertEquals(1, number);
    }

    @Test
    public void updateAddressWithNoChangesInMainframe() throws Exception {
        DataOperationsTestsHelper.createTestingDatabases();
        Address address = new Address();
        address.setCustomerId("123");
        address.setNumber(1);
        Integer number = AddressUpdater.updateMainframe("123", address);
        assertEquals(1, number);
    }

    @Test
    public void addAddressInMainframe() throws Exception {
        DataOperationsTestsHelper.createTestingDatabases();
        Address address = new Address();
        address.setCustomerId("123");
        address.setType("Business");
        address.setLine1("Elm St");
        address.setIsPrimary(false);
        address.setIsMailing(false);
        AddressUpdater.updateMainframe("123", address);
        assertEquals(2, address.getNumber());
    }

    @Test
    public void updateNonExistingAddressInMainframe() throws Exception {
        DataOperationsTestsHelper.createTestingDatabases();
        Address address = new Address();
        address.setCustomerId("1000");
        address.setNumber(10);
        address.setType("Business");
        address.setLine1("Elm St");
        address.setIsPrimary(false);
        address.setIsMailing(false);
        assertThrows(Exception.class, () -> {
            AddressUpdater.updateMainframe("1000", address);
        });
    }

    @Test
    public void addInvalidAddress() throws SQLException {
        DataOperationsTestsHelper.createTestingDatabases();
        String customerId = null;
        assertThrows(Exception.class, () -> {
            AddressUpdater.updateData(customerId, null);
        });
    }

    @Test
    public void addAddress() throws Exception {
        DataOperationsTestsHelper.createTestingDatabases();

        String customerId = "654";
        Address address = new Address("654", "Business", "36", "Main Street", "Springfield", "IL", "62701",
                "USA", true,
                false, null);

        AddressUpdater.updateData(customerId, address);

        List<Address> addresses = AddressFinder.findData(customerId);
        for (Address a : addresses) {
            if (a.getNumber() == address.getNumber()) {
                assertEquals("654", a.getCustomerId());
                assertEquals("36", a.getLine1());
                assertEquals("IL", a.getCity());
                assertEquals(true, a.getIsPrimary());
            }
        }
    }

    @Test
    public void retryFailedUpdates() throws Exception {
        DataOperationsTestsHelper.createTestingDatabases();
        CustomerUpdater.retryFailedUpdates();
        AddressUpdater.retryFailedUpdates();
        try (PreparedStatement selectStmt = LocalInstance.getDatabaseConnection()
                .prepareStatement("SELECT * FROM address WHERE InMainframe = false")) {
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
        List<Address> failedUpdates = AddressUpdater.getFailedUpdates();
        assertEquals(2, failedUpdates.size());
    }
}