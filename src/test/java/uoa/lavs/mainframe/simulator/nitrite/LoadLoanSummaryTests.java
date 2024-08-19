package uoa.lavs.mainframe.simulator.nitrite;

import org.junit.jupiter.api.Test;
import uoa.lavs.mainframe.Connection;
import uoa.lavs.mainframe.Frequency;
import uoa.lavs.mainframe.Status;
import uoa.lavs.mainframe.messages.loan.LoadLoanSummary;
import uoa.lavs.mainframe.simulator.NitriteConnection;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static uoa.lavs.mainframe.MessageErrorStatus.LOAN_NOT_FOUND;

class LoadLoanSummaryTests {
    @Test
    public void handlesMissingLoan() {
        // Arrange
        Connection connection = new NitriteConnection();
        LoadLoanSummary message = new LoadLoanSummary();
        message.setLoanId("123-08");

        // Act
        Status status = message.send(connection);

        // Assert
        assertAll(
                () -> assertEquals(LOAN_NOT_FOUND.getCode(), status.getErrorCode()),
                () -> assertEquals(LOAN_NOT_FOUND.getMessage(), status.getErrorMessage())
        );
    }

    @Test
    public void handlesExistingLoan() {
        // Arrange
        Connection connection = new NitriteConnection(
                DatabaseHelper.generateDefaultDatabase());
        LoadLoanSummary message = new LoadLoanSummary();
        message.setLoanId("123-09");

        // Act
        Status status = message.send(connection);

        // Assert
        assertAll(
                () -> assertTrue(status.getWasSuccessful()),
                () -> assertEquals("123", message.getCustomerIdFromServer()),
                () -> assertEquals("John Doe", message.getCustomerNameFromServer()),
                () -> assertEquals(30, message.getTermFromServer()),
                () -> assertEquals(573.00, message.getPaymentAmountFromServer()),
                () -> assertEquals(Frequency.Monthly, message.getPaymentFrequencyFromServer()),
                () -> assertEquals(7.65, message.getRateValueFromServer()),
                () -> assertEquals(10000.00, message.getPrincipalFromServer()),
                () -> assertEquals(635.55, message.getTotalInterestFromServer()),
                () -> assertEquals(10635.55, message.getTotalLoanCostFromServer())
        );
    }
}