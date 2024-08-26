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
import uoa.lavs.models.Phone;

public class PhoneUpdaterTests {

    @Test
    public void addPhoneInDatabase() throws SQLException {
        DataOperationsTestsHelper.createTestingDatabases();
        Phone phone = new Phone("1", "Landline", "09", "12345678", true, false, null);

        PhoneUpdater phoneUpdater = new PhoneUpdater();
        phoneUpdater.updateDatabase("1", phone);

        try (PreparedStatement selectStmt = LocalInstance.getDatabaseConnection()
                .prepareStatement("SELECT * FROM phone WHERE CustomerID = ? AND Number = ?")) {
            selectStmt.setString(1, phone.getCustomerId());
            selectStmt.setInt(2, phone.getNumber());
            try (ResultSet resultSet = selectStmt.executeQuery()) {
                assertTrue(resultSet.next());
                assertAll("phone",
                        () -> assertEquals("1", resultSet.getString("CustomerID")),
                        () -> assertEquals("12345678", resultSet.getString("PhoneNumber")),
                        () -> assertEquals(true, resultSet.getBoolean("IsPrimary")),
                        () -> assertEquals(false, resultSet.getBoolean("InMainframe")),
                        () -> assertEquals(3, resultSet.getInt("Number")));
            }
        }
    }

    @Test
    public void updatePhoneInDatabase() throws SQLException {
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

    @Test
    public void updatePhoneInMainframe() throws Exception {
        DataOperationsTestsHelper.createTestingDatabases();
        Phone phone = new Phone("123", "Mobile", "64", "987654", true, false, 1);
        PhoneUpdater.updateMainframe("123", phone);

        List<Phone> phones = PhoneFinder.findFromMainframe("123");
        assertAll("phone",
                () -> assertEquals("123", phones.get(0).getCustomerId()),
                () -> assertEquals("987654", phones.get(0).getPhoneNumber()),
                () -> assertEquals(true, phones.get(0).getIsPrimary()),
                () -> assertEquals(false, phones.get(0).getCanSendText()));
    }

    @Test
    public void updatePhoneWithNoChangesInMainframe() throws Exception {
        DataOperationsTestsHelper.createTestingDatabases();
        Phone phone = new Phone();
        phone.setCustomerId("123");
        phone.setNumber(1);
        PhoneUpdater.updateMainframe("123", phone);
        List<Phone> phones = PhoneFinder.findFromMainframe("123");
        assertAll("phone",
                () -> assertEquals("123", phones.get(0).getCustomerId()),
                () -> assertEquals("123-4567", phones.get(0).getPhoneNumber()),
                () -> assertEquals(true, phones.get(0).getIsPrimary()),
                () -> assertEquals(true, phones.get(0).getCanSendText()));
    }

    @Test
    public void addPhoneInMainframe() throws Exception {
        DataOperationsTestsHelper.createTestingDatabases();
        Phone phone = new Phone();
        phone.setCustomerId("123");
        phone.setPhoneNumber("987654");
        phone.setIsPrimary(true);
        phone.setCanSendText(false);
        PhoneUpdater.updateMainframe("123", phone);
        List<Phone> phones = PhoneFinder.findFromMainframe("123");
        for (Phone p : phones) {
            if (p.getPhoneNumber().equals(phone.getPhoneNumber())) {
                assertEquals("123", p.getCustomerId());
                assertEquals("987654", p.getPhoneNumber());
                assertEquals(true, p.getIsPrimary());
                assertEquals(false, p.getCanSendText());
            }
        }
    }

    @Test
    public void updateNonExistingPhoneInMainframe() throws Exception {
        DataOperationsTestsHelper.createTestingDatabases();
        Phone phone = new Phone();
        phone.setCustomerId("1000");
        phone.setPhoneNumber("000000");
        phone.setIsPrimary(false);
        phone.setCanSendText(false);
        assertThrows(Exception.class, () -> {
            PhoneUpdater.updateMainframe("1000", phone);
        });
    }

    @Test
    public void addInvalidPhone() throws SQLException {
        DataOperationsTestsHelper.createTestingDatabases();
        String customerId = null;
        assertThrows(Exception.class, () -> {
            PhoneUpdater.updateData(customerId, null);
        });
    }

    @Test
    public void addPhone() throws Exception {
        DataOperationsTestsHelper.createTestingDatabases();

        String customerId = "654";
        Phone phone = new Phone("654", "Mobile", "64", "987654", true, false, 1);

        PhoneUpdater.updateData(customerId, phone);

        List<Phone> phones = PhoneFinder.findData(customerId);
        for (Phone p : phones) {
            if (p.getPhoneNumber().equals(phone.getPhoneNumber())) {
                assertEquals("654", p.getCustomerId());
                assertEquals("987654", p.getPhoneNumber());
                assertEquals(true, p.getIsPrimary());
                assertEquals(false, p.getCanSendText());
            }
        }
    }

    @Test
    public void retryFailedUpdates() throws Exception {
        DataOperationsTestsHelper.createTestingDatabases();
        CustomerUpdater.retryFailedUpdates();
        PhoneUpdater.retryFailedUpdates();
        try (PreparedStatement selectStmt = LocalInstance.getDatabaseConnection()
                .prepareStatement("SELECT * FROM phone WHERE InMainframe = false")) {
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
        List<Phone> failedUpdates = PhoneUpdater.getFailedUpdates();
        assertEquals(2, failedUpdates.size());
    }
}