package uoa.lavs.mainframe.simulator.http;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import uoa.lavs.mainframe.Connection;
import uoa.lavs.mainframe.Frequency;
import uoa.lavs.mainframe.RateType;
import uoa.lavs.mainframe.Status;
import uoa.lavs.mainframe.messages.loan.LoadLoan;
import uoa.lavs.mainframe.simulator.HttpConnection;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static uoa.lavs.mainframe.MessageErrorStatus.LOAN_NOT_FOUND;

@EnabledIfEnvironmentVariable(named = "LAVS_ENVIRONMENT", matches = "http_test")
class LoadLoanTests {
    @Test
    public void handlesMissingLoan() {
        // Arrange
        Connection connection = new HttpConnection(Constants.BASE_URL);
        LoadLoan message = new LoadLoan();
        message.setLoanId("0000022-999");

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
        Connection connection = new HttpConnection(Constants.BASE_URL);
        LoadLoan message = new LoadLoan();
        message.setLoanId("0000022-001");

        // Act
        Status status = message.send(connection);

        // Assert
        assertAll(
                () -> assertTrue(status.getWasSuccessful()),
                () -> assertEquals(0, status.getErrorCode()),
                () -> assertNull(status.getErrorMessage()),
                () -> assertEquals("0000022", message.getCustomerIdFromServer()),
                () -> assertEquals("John Doe", message.getCustomerNameFromServer()),
                () -> assertEquals("Pending", message.getStatusFromServer()),
                () -> assertEquals(Frequency.Weekly, message.getCompoundingFromServer()),
                () -> assertEquals(500.00, message.getPaymentAmountFromServer()),
                () -> assertEquals(LocalDate.of(2024, 8, 5), message.getStartDateFromServer()),
                () -> assertEquals(10000.00, message.getPrincipalFromServer()),
                () -> assertEquals(24, message.getPeriodFromServer()),
                () -> assertEquals(30, message.getTermFromServer()),
                () -> assertEquals(Frequency.Weekly, message.getPaymentFrequencyFromServer()),
                () -> assertEquals(RateType.Fixed, message.getRateTypeFromServer()),
                () -> assertEquals(6.54, message.getRateValueFromServer())
        );
    }
}