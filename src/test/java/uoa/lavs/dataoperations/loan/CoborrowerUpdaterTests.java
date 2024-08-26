package uoa.lavs.dataoperations.loan;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import uoa.lavs.LocalInstance;
import uoa.lavs.dataoperations.DataOperationsTestsHelper;

import static org.junit.jupiter.api.Assertions.*;

public class CoborrowerUpdaterTests {

    @BeforeEach
    public void setup() throws SQLException {
        DataOperationsTestsHelper.createTestingDatabasesForLoans();
    }

    @Test
    public void addCoborrowerInDatabase() throws SQLException {
        String loanId = "1-01";
        String coborrowerId = "2";
        CoborrowerUpdater coborrowerUpdater = new CoborrowerUpdater();

        coborrowerUpdater.updateDatabase(loanId, coborrowerId);

        try (PreparedStatement selectStmt = LocalInstance.getDatabaseConnection()
                .prepareStatement("SELECT * FROM Coborrower WHERE LoanID = ? AND CoborrowerID = ?")) {
            selectStmt.setString(1, loanId);
            selectStmt.setString(2, coborrowerId);
            try (ResultSet resultSet = selectStmt.executeQuery()) {
                assertTrue(resultSet.next());
                assertAll("coborrower",
                        () -> assertEquals(loanId, resultSet.getString("LoanID")),
                        () -> assertEquals(coborrowerId, resultSet.getString("CoborrowerID")));
            }
        }
    }

    @Test
    public void updateCoborrowerInMainframe() throws Exception {
        String loanId = "123-09";
        String coborrowerId = "123";
        Integer number = 1;

        int returnedNumber = CoborrowerUpdater.updateMainframe(loanId, coborrowerId, number);

        assertEquals(number, returnedNumber);
    }

    @Test
    public void addCoborrowerWithMainframeFailure() throws SQLException {
        String loanId = "123-09";
        String coborrowerId = "123";

        CoborrowerUpdater.updateData(loanId, coborrowerId, null);
        try (PreparedStatement selectStmt = LocalInstance.getDatabaseConnection()
                .prepareStatement("SELECT * FROM Coborrower WHERE LoanID = ? AND CoborrowerID = ?")) {
            selectStmt.setString(1, loanId);
            selectStmt.setString(2, coborrowerId);
            try (ResultSet resultSet = selectStmt.executeQuery()) {
                assertTrue(resultSet.next());
                assertFalse(resultSet.getBoolean("InMainframe"));
            }
        }
    }

    @Test
    public void retryFailedCoborrowerUpdates() throws Exception {
        String loanId = "1-01";
        String coborrowerId = "2";

        CoborrowerUpdater.retryFailedUpdates();

        try (PreparedStatement selectStmt = LocalInstance.getDatabaseConnection()
                .prepareStatement("SELECT * FROM Coborrower WHERE LoanID = ? AND CoborrowerID = ?")) {
            selectStmt.setString(1, loanId);
            selectStmt.setString(2, coborrowerId);
            try (ResultSet resultSet = selectStmt.executeQuery()) {
                assertTrue(resultSet.next());
                assertTrue(resultSet.getBoolean("InMainframe"));
            }
        }
    }

    @Test
    public void getFailedCoborrowerUpdates() throws SQLException {
        String loanId = "1-01";
        String coborrowerId = "2";

        List<String> failedUpdates = CoborrowerUpdater.getFailedUpdates();

        assertEquals(1, failedUpdates.size());
        assertEquals(loanId + "," + coborrowerId, failedUpdates.get(0));
    }

    @Test
    public void updateCoborrowerWithInvalidData() {
        String loanId = null;
        String coborrowerId = "2";

        assertDoesNotThrow(() -> {
        CoborrowerUpdater.updateData(loanId, coborrowerId, null);
    });
}
}
