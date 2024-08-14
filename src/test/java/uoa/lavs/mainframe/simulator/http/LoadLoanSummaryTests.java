package uoa.lavs.mainframe.simulator.http;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import uoa.lavs.mainframe.Connection;
import uoa.lavs.mainframe.Frequency;
import uoa.lavs.mainframe.Status;
import uoa.lavs.mainframe.messages.loan.LoadLoanSummary;
import uoa.lavs.mainframe.simulator.HttpConnection;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static uoa.lavs.mainframe.MessageErrorStatus.LOAN_NOT_FOUND;

@EnabledIfEnvironmentVariable(named = "LAVS_ENVIRONMENT", matches = "http_test")
class LoadLoanSummaryTests {
    @Test
    public void handlesMissingLoan() {
        // Arrange
        Connection connection = new HttpConnection(Constants.BASE_URL);
        LoadLoanSummary message = new LoadLoanSummary();
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
        LoadLoanSummary message = new LoadLoanSummary();
        message.setLoanId("0000022-001");

        // Act
        Status status = message.send(connection);

        // Assert
        assertAll(
                () -> assertTrue(status.getWasSuccessful()),
                () -> assertEquals("0000022", message.getCustomerIdFromServer()),
                () -> assertEquals("John Doe", message.getCustomerNameFromServer()),
                () -> assertEquals(30, message.getTermFromServer()),
                () -> assertEquals(500, message.getPaymentAmountFromServer()),
                () -> assertEquals(Frequency.Weekly, message.getPaymentFrequencyFromServer()),
                () -> assertEquals(6.54, message.getRateValueFromServer()),
                () -> assertEquals(LocalDate.of(2024, 12, 30), message.getPayoffDateFromServer()),
                () -> assertEquals(10000.00, message.getPrincipalFromServer()),
                () -> assertEquals(133.97, message.getTotalInterestFromServer()),
                () -> assertEquals(10133.97, message.getTotalLoanCostFromServer())
        );
    }
}