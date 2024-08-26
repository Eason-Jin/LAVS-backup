package uoa.lavs.dataoperations.loan;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Test;

import uoa.lavs.LocalInstance;
import uoa.lavs.dataoperations.DataOperationsTestsHelper;
import uoa.lavs.models.Loan;
import uoa.lavs.mainframe.Frequency;
import uoa.lavs.mainframe.RateType;

public class LoanUpdaterTests {

    @Test
    public void addLoanInDatabase() throws SQLException {
        LoanUpdater loanUpdater = new LoanUpdater();
        DataOperationsTestsHelper.createTestingDatabasesForLoans();

        Loan loan = new Loan(
            null,
            "1",
            "John Doe",
            "New",
            5000.00,
            5.5,
            RateType.Fixed,
            LocalDate.of(2023, 8, 25),
            24,
            24,
            220.00,
            Frequency.Monthly,
            Frequency.Yearly
        );
        LoanUpdater.updateData(null, loan);

        try (PreparedStatement selectStmt = LocalInstance.getDatabaseConnection()
                .prepareStatement("SELECT * FROM loan WHERE CustomerID = ? AND LoanID = ?")) {
            selectStmt.setString(1, loan.getCustomerId());
            selectStmt.setString(2, loan.getLoanId());
            try (ResultSet resultSet = selectStmt.executeQuery()) {
                assertTrue(resultSet.next());
                assertAll("loan",
                        () -> assertEquals("1", resultSet.getString("CustomerID")),
                        () -> assertEquals(loan.getLoanId(), resultSet.getString("LoanID")),
                        () -> assertEquals("John Doe", resultSet.getString("CustomerName")),
                        () -> assertEquals("New", resultSet.getString("Status")),
                        () -> assertEquals(5000.00, resultSet.getDouble("Principal")),
                        () -> assertEquals(5.5, resultSet.getDouble("RateValue")),
                        () -> assertEquals(RateType.Fixed.toString(), resultSet.getString("RateType")),
                        () -> assertEquals(LocalDate.of(2023, 8, 25), LocalDate.parse(resultSet.getString("StartDate"))),
                        () -> assertEquals(24, resultSet.getInt("Period")),
                        () -> assertEquals(24, resultSet.getInt("Term")),
                        () -> assertEquals(220.00, resultSet.getDouble("PaymentAmount")),
                        () -> assertEquals(Frequency.Monthly.toString(), resultSet.getString("PaymentFrequency")),
                        () -> assertEquals(Frequency.Yearly.toString(), resultSet.getString("Compounding")));
            }
        }
    }

    @Test
    public void updateLoanInDatabase() throws SQLException {
        DataOperationsTestsHelper.createTestingDatabasesForLoans();
        Loan loan = new Loan(
            "5-01",
            "5",
            "Jane Doe",
            "Active",
            10000.00,
            4.5,
            RateType.Floating,
            LocalDate.of(2023, 8, 25),
            36,
            36,
            300.00,
            Frequency.Quarterly,
            Frequency.HalfYearly
        );
        LoanUpdater.updateData("5-01", loan);
        try (PreparedStatement selectStmt = LocalInstance.getDatabaseConnection()
                .prepareStatement("SELECT * FROM loan WHERE CustomerID = ? AND LoanID = ?")) {
            selectStmt.setString(1, loan.getCustomerId());
            selectStmt.setString(2, loan.getLoanId());
            try (ResultSet resultSet = selectStmt.executeQuery()) {
                assertTrue(resultSet.next());
                assertAll("loan",
                        () -> assertEquals("5", resultSet.getString("CustomerID")),
                        () -> assertEquals("Jane Doe", resultSet.getString("CustomerName")),
                        () -> assertEquals("Active", resultSet.getString("Status")),
                        () -> assertEquals(10000.00, resultSet.getDouble("Principal")),
                        () -> assertEquals(4.5, resultSet.getDouble("RateValue")),
                        () -> assertEquals(RateType.Floating.toString(), resultSet.getString("RateType")),
                        () -> assertEquals(LocalDate.of(2023, 8, 25), LocalDate.parse(resultSet.getString("StartDate"))),
                        () -> assertEquals(36, resultSet.getInt("Period")),
                        () -> assertEquals(36, resultSet.getInt("Term")),
                        () -> assertEquals(300.00, resultSet.getDouble("PaymentAmount")),
                        () -> assertEquals(Frequency.Quarterly.toString(), resultSet.getString("PaymentFrequency")),
                        () -> assertEquals(Frequency.HalfYearly.toString(), resultSet.getString("Compounding")));
            }
        }
    }

    @Test
    public void updateLoanInMainframe() throws Exception {
        DataOperationsTestsHelper.createTestingDatabasesForLoans();
        Loan loan = new Loan(
            "123-10",
            "123",
            "John Doe",
            "Pending",
            8000.00,
            4.0,
            RateType.InterestOnly,
            LocalDate.of(2022, 5, 15),
            30,
            30,
            400.00,
            Frequency.Monthly,
            Frequency.Quarterly
        );

        String loanId = LoanUpdater.updateMainframe("123-10", loan);
        assertEquals("123-10", loanId);
    }

    @Test
    public void addLoanInMainframe() throws Exception {
        DataOperationsTestsHelper.createTestingDatabasesForLoans();
        Loan loan = new Loan(
            null,
            "123",
            "John Doe",
            "New",
            7000.00,
            3.5,
            RateType.Fixed,
            LocalDate.of(2023, 4, 20),
            24,
            24,
            200.00,
            Frequency.Weekly,
            Frequency.Monthly
        );
        LoanUpdater.updateDatabase(null, loan);
        assertEquals("123-01 (Temp)", loan.getLoanId());  // Assuming a new loan ID is generated like this
    }

    @Test
    public void updateNonExistingLoanInMainframe() throws Exception {
        DataOperationsTestsHelper.createTestingDatabasesForLoans();
        Loan loan = new Loan();
        loan.setLoanId("9999");
        loan.setCustomerId("1000");
        loan.setPrincipal(15000.00);
        loan.setRateValue(6.0);
        loan.setRateType(RateType.Floating);
        loan.setStartDate(LocalDate.of(2022, 1, 1));
        loan.setPaymentAmount(600.00);
        assertThrows(Exception.class, () -> {
            LoanUpdater.updateData("9999", loan);
        });
    }

    @Test
    public void addInvalidLoan() throws SQLException {
        DataOperationsTestsHelper.createTestingDatabasesForLoans();
        String customerId = null;
        assertThrows(Exception.class, () -> {
            LoanUpdater.updateData(customerId, null);
        });
    }

    @Test
    public void addLoan() throws Exception {
        DataOperationsTestsHelper.createTestingDatabasesForLoans();

        String customerId = "654";
        Loan loan = new Loan(
            null,
            customerId,
            "Jane Doe",
            "Pending",
            12000.00,
            5.0,
            RateType.Fixed,
            LocalDate.of(2023, 1, 10),
            36,
            36,
            350.00,
            Frequency.Monthly,
            Frequency.Yearly
        );

        LoanUpdater.updateData(customerId, loan);

        List<Loan> loans = LoanFinder.findData(customerId);
        for (Loan l : loans) {
            if (l.getLoanId().equals(loan.getLoanId())) {
                assertEquals("654", l.getCustomerId());
                assertEquals("Jane Doe", l.getCustomerName());
                assertEquals("Pending", l.getStatus());
                assertEquals(12000.00, l.getPrincipal());
                assertEquals(5.0, l.getRateValue());
            }
        }
    }

    @Test
    public void retryFailedUpdates() throws Exception {
        DataOperationsTestsHelper.createTestingDatabasesForLoans();
        LoanUpdater.retryFailedUpdates();
        try (PreparedStatement selectStmt = LocalInstance.getDatabaseConnection()
                .prepareStatement("SELECT * FROM loan WHERE InMainframe = false")) {
            try (ResultSet resultSet = selectStmt.executeQuery()) {
                int rowCount = 0;
                while (resultSet.next()) {
                    rowCount++;
                }
                assertEquals(1, rowCount);
            }
        }
    }

    @Test
    public void getFailedUpdates() throws Exception {
        DataOperationsTestsHelper.createTestingDatabasesForLoans();
        List<Loan> failedUpdates = LoanUpdater.getFailedUpdates();
        assertEquals(1, failedUpdates.size());
    }
}
