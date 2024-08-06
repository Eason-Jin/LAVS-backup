package uoa.lavs.mainframe.simulator.nitrite;

import org.junit.jupiter.api.Test;
import uoa.lavs.mainframe.Connection;
import uoa.lavs.mainframe.Frequency;
import uoa.lavs.mainframe.RateType;
import uoa.lavs.mainframe.Status;
import uoa.lavs.mainframe.messages.loan.LoadLoan;
import uoa.lavs.mainframe.simulator.NitriteConnection;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static uoa.lavs.mainframe.MessageErrorStatus.LOAN_NOT_FOUND;

class LoadLoanTests {
    @Test
    public void handlesMissingLoan() {
        // Arrange
        Connection connection = new NitriteConnection();
        LoadLoan message = new LoadLoan();
        message.setLoanId("123-9");

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
        LoadLoan message = new LoadLoan();
        message.setLoanId("123-09");

        // Act
        Status status = message.send(connection);

        // Assert
        assertAll(
                () -> assertTrue(status.getWasSuccessful()),
                () -> assertEquals("123", message.getCustomerIdFromServer()),
                () -> assertEquals("John Doe", message.getCustomerNameFromServer()),
                () -> assertEquals("Active", message.getStatusFromServer()),
                () -> assertEquals(Frequency.Weekly, message.getCompoundingFromServer()),
                () -> assertEquals(573.00, message.getPaymentAmountFromServer()),
                () -> assertEquals(LocalDate.of(2024, 8, 3), message.getStartDateFromServer()),
                () -> assertEquals(10000.00, message.getPrincipalFromServer()),
                () -> assertEquals(24, message.getPeriodFromServer()),
                () -> assertEquals(30, message.getTermFromServer()),
                () -> assertEquals(Frequency.Monthly, message.getPaymentFrequencyFromServer()),
                () -> assertEquals(RateType.Fixed, message.getRateTypeFromServer()),
                () -> assertEquals(7.65, message.getRateValueFromServer())
        );
    }
}