package uoa.lavs.mainframe.simulator.nitrite;

import org.junit.jupiter.api.Test;
import uoa.lavs.mainframe.Connection;
import uoa.lavs.mainframe.Status;
import uoa.lavs.mainframe.messages.loan.LoadLoanPayments;
import uoa.lavs.mainframe.simulator.NitriteConnection;

import static org.junit.jupiter.api.Assertions.*;
import static uoa.lavs.mainframe.MessageErrorStatus.LOAN_NOT_FOUND;

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
        Connection connection = new NitriteConnection();
        LoadLoanPayments message = new LoadLoanPayments();
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
        LoadLoanPayments message = new LoadLoanPayments();
        message.setLoanId("123-09");

        // Act
        Status status = message.send(connection);

        // Assert
        assertAll(
                () -> assertTrue(status.getWasSuccessful()),
                () -> assertEquals("123", message.getCustomerIdFromServer()),
                () -> assertEquals("John Doe", message.getCustomerNameFromServer()),
                () -> assertEquals(2, message.getPageCountFromServer()),
                () -> assertEquals(17, message.getPaymentCountFromServer()),
                () -> assertEquals(63.75, message.getPaymentInterestFromServer(1)),
                () -> assertEquals(1, message.getPaymentNumberFromServer(1)),
                () -> assertEquals(509.25, message.getPaymentPrincipalFromServer(1)),
                () -> assertEquals(9490.75, message.getPaymentRemainingFromServer(1))
        );
    }

    @Test
    public void handlesLowNumbers() {
        // Arrange
        Connection connection = new NitriteConnection(
                new DatabaseBuilder()
                        .addCustomer("1234567", "John Doe", null)
                        .addLoan("1234567", 30_000.00, 2.0, 2_000.00)
                        .build());
        LoadLoanPayments message = new LoadLoanPayments();
        message.setLoanId("1234567-1");

        // Act
        Status status = message.send(connection);

        // Assert
        assertAll(
                () -> assertTrue(status.getWasSuccessful()),
                () -> assertEquals(0, status.getErrorCode()),
                () -> assertNull(status.getErrorMessage()),
                () -> assertEquals("1234567", message.getCustomerIdFromServer()),
                () -> assertEquals("John Doe", message.getCustomerNameFromServer()),
                () -> assertEquals(1, message.getPageCountFromServer()),
                () -> assertEquals(16, message.getPaymentCountFromServer()),
                () -> assertEquals("[01] 50.00 + 1950.00 -> 28050.00", formatLine(message, 1)),
                () -> assertEquals("[02] 46.75 + 1953.25 -> 26096.75", formatLine(message, 2)),
                () -> assertEquals("[03] 43.49 + 1956.51 -> 24140.24", formatLine(message, 3))
        );
    }
}