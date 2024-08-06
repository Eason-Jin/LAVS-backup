package uoa.lavs.mainframe.simulator.nitrite;

import org.junit.jupiter.api.Test;
import uoa.lavs.mainframe.Connection;
import uoa.lavs.mainframe.Frequency;
import uoa.lavs.mainframe.RateType;
import uoa.lavs.mainframe.Status;
import uoa.lavs.mainframe.messages.loan.UpdateLoan;
import uoa.lavs.mainframe.simulator.NitriteConnection;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static uoa.lavs.mainframe.MessageErrorStatus.CUSTOMER_NOT_FOUND;
import static uoa.lavs.mainframe.MessageErrorStatus.LOAN_NOT_FOUND;

class UpdateLoanTests {
    @Test
    public void handlesMissingCustomer() {
        // Arrange
        Connection connection = new NitriteConnection(
                DatabaseHelper.generateDefaultDatabase());
        UpdateLoan message = new UpdateLoan();
        setMessageParameters(message, null);
        message.setCustomerId("789");

        // Act
        Status status = message.send(connection);

        // Assert
        assertAll(
                () -> assertEquals(CUSTOMER_NOT_FOUND.getCode(), status.getErrorCode()),
                () -> assertEquals(CUSTOMER_NOT_FOUND.getMessage(), status.getErrorMessage())
        );
    }

    @Test
    public void handlesMissingLoan() {
        // Arrange
        Connection connection = new NitriteConnection(
                DatabaseHelper.generateDefaultDatabase());
        UpdateLoan message = new UpdateLoan();
        setMessageParameters(message, "123-12");

        // Act
        Status status = message.send(connection);

        // Assert
        assertAll(
                () -> assertEquals(LOAN_NOT_FOUND.getCode(), status.getErrorCode()),
                () -> assertEquals(LOAN_NOT_FOUND.getMessage(), status.getErrorMessage())
        );
    }

    @Test
    public void handlesNewLoan() {
        // Arrange
        Connection connection = new NitriteConnection(
                DatabaseHelper.generateDefaultDatabase());
        UpdateLoan message = new UpdateLoan();
        setMessageParameters(message, null);

        // Act
        Status status = message.send(connection);

        // Assert
        assertAll(
                () -> assertTrue(status.getWasSuccessful()),
                () -> assertEquals("123-10", message.getLoanIdFromServer()),
                () -> assertEquals(Frequency.Yearly, message.getCompoundingFromServer()),
                () -> assertEquals(20, message.getTermFromServer()),
                () -> assertEquals("123", message.getCustomerIdFromServer()),
                () -> assertEquals("John Doe", message.getCustomerNameFromServer()),
                () -> assertEquals("New", message.getStatusFromServer()),
                () -> assertEquals(LocalDate.of(1998, 7, 6), message.getStartDateFromServer()),
                () -> assertEquals(18, message.getPeriodFromServer()),
                () -> assertEquals(963.36, message.getPaymentAmountFromServer()),
                () -> assertEquals(Frequency.Monthly, message.getPaymentFrequencyFromServer()),
                () -> assertEquals(650000.00, message.getPrincipalFromServer()),
                () -> assertEquals(RateType.Floating, message.getRateTypeFromServer()),
                () -> assertEquals(8.75, message.getRateValueFromServer())
        );
    }

    @Test
    public void handlesExistingLoan() {
        // Arrange
        Connection connection = new NitriteConnection(
                DatabaseHelper.generateDefaultDatabase());
        UpdateLoan message = new UpdateLoan();
        setMessageParameters(message, "123-09");

        // Act
        Status status = message.send(connection);

        // Assert
        assertAll(
                () -> assertTrue(status.getWasSuccessful()),
                () -> assertEquals("123-09", message.getLoanIdFromServer()),
                () -> assertEquals(Frequency.Yearly, message.getCompoundingFromServer()),
                () -> assertEquals(20, message.getTermFromServer()),
                () -> assertEquals("123", message.getCustomerIdFromServer()),
                () -> assertEquals("John Doe", message.getCustomerNameFromServer()),
                () -> assertEquals("Active", message.getStatusFromServer()),
                () -> assertEquals(LocalDate.of(1998, 7, 6), message.getStartDateFromServer()),
                () -> assertEquals(18, message.getPeriodFromServer()),
                () -> assertEquals(963.36, message.getPaymentAmountFromServer()),
                () -> assertEquals(Frequency.Monthly, message.getPaymentFrequencyFromServer()),
                () -> assertEquals(650000.00, message.getPrincipalFromServer()),
                () -> assertEquals(RateType.Floating, message.getRateTypeFromServer()),
                () -> assertEquals(8.75, message.getRateValueFromServer())
        );
    }

    private static void setMessageParameters(UpdateLoan message, String id) {
        message.setLoanId(id);
        message.setCustomerId("123");
        message.setCompounding(Frequency.Yearly);
        message.setPeriod(18);
        message.setPaymentAmount(963.36);
        message.setPaymentFrequency(Frequency.Monthly);
        message.setPrincipal(650000.00);
        message.setRateType(RateType.Floating);
        message.setRateValue(8.75);
        message.setTerm(20);
        message.setStartDate(LocalDate.of(1998, 7, 6));
    }

}