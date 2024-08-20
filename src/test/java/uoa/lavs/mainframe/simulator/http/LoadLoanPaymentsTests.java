package uoa.lavs.mainframe.simulator.http;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import uoa.lavs.mainframe.Connection;
import uoa.lavs.mainframe.Status;
import uoa.lavs.mainframe.messages.loan.LoadLoanPayments;
import uoa.lavs.mainframe.simulator.HttpConnection;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static uoa.lavs.mainframe.MessageErrorStatus.LOAN_NOT_FOUND;

@EnabledIfEnvironmentVariable(named = "LAVS_ENVIRONMENT", matches = "http_test")
class LoadLoanPaymentsTests {
    private static String formatLine(LoadLoanPayments msg, Integer number) {
        String output = String.format("[%02d] %.2f + %.2f -> %.2f",
                msg.getPaymentNumberFromServer(number),
                msg.getPaymentInterestFromServer(number),
                msg.getPaymentPrincipalFromServer(number),
                msg.getPaymentRemainingFromServer(number));
        return output;
    }

    @Test
    public void handlesMissingLoan() {
        // Arrange
        Connection connection = new HttpConnection(Constants.BASE_URL);
        LoadLoanPayments message = new LoadLoanPayments();
        message.setLoanId("0000022-999");
        message.setNumber(1);

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
        LoadLoanPayments message = new LoadLoanPayments();
        message.setLoanId("0000022-001");
        message.setNumber(1);

        // Act
        Status status = message.send(connection);

        // Assert
        assertAll(
                () -> assertTrue(status.getWasSuccessful()),
                () -> assertEquals("0000022", message.getCustomerIdFromServer()),
                () -> assertEquals("John Doe", message.getCustomerNameFromServer()),
                () -> assertEquals(2, message.getPageCountFromServer()),
                () -> assertEquals(18, message.getPaymentCountFromServer()),
                () -> assertEquals(12.54, message.getPaymentInterestFromServer(1)),
                () -> assertEquals(1, message.getPaymentNumberFromServer(1)),
                () -> assertEquals(487.46, message.getPaymentPrincipalFromServer(1)),
                () -> assertEquals(9512.54, message.getPaymentRemainingFromServer(1)),
                () -> assertEquals(LocalDate.of(2024, 8, 12), message.getPaymentDateFromServer(1))
        );
    }

    @Test
    public void handlesNonFullPage() {
        // Arrange
        Connection connection = new HttpConnection(Constants.BASE_URL);
        LoadLoanPayments message = new LoadLoanPayments();
        message.setLoanId("0000022-001");
        message.setNumber(2);

        // Act
        Status status = message.send(connection);

        // Assert
        assertAll(
                () -> assertTrue(status.getWasSuccessful()),
                () -> assertEquals(0, status.getErrorCode()),
                () -> assertNull(status.getErrorMessage()),
                () -> assertEquals("0000022", message.getCustomerIdFromServer()),
                () -> assertEquals("John Doe", message.getCustomerNameFromServer()),
                () -> assertEquals(2, message.getPageCountFromServer()),
                () -> assertEquals(3, message.getPaymentCountFromServer()),
                () -> assertEquals("[19] 1.42 + 498.58 -> 633.01", formatLine(message, 1)),
                () -> assertEquals("[20] 0.79 + 499.21 -> 133.80", formatLine(message, 2)),
                () -> assertEquals("[21] 0.17 + 133.80 -> 0.00", formatLine(message, 3))
        );
    }
}