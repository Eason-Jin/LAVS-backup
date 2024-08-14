package uoa.lavs.mainframe.simulator.http;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import uoa.lavs.mainframe.Connection;
import uoa.lavs.mainframe.Frequency;
import uoa.lavs.mainframe.RateType;
import uoa.lavs.mainframe.Status;
import uoa.lavs.mainframe.messages.loan.UpdateLoan;
import uoa.lavs.mainframe.simulator.HttpConnection;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static uoa.lavs.mainframe.MessageErrorStatus.CUSTOMER_NOT_FOUND;
import static uoa.lavs.mainframe.MessageErrorStatus.LOAN_NOT_FOUND;

@EnabledIfEnvironmentVariable(named = "LAVS_ENVIRONMENT", matches = "http_test")
class UpdateLoanTests {
    private static void setMessageParameters(UpdateLoan message, String id) {
        message.setLoanId(id);
        message.setCustomerId("0000038");
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

    @Test
    public void handlesMissingCustomer() {
        // Arrange
        Connection connection = new HttpConnection(Constants.BASE_URL);
        UpdateLoan message = new UpdateLoan();
        setMessageParameters(message, null);
        message.setCustomerId("0000011");

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
        Connection connection = new HttpConnection(Constants.BASE_URL);
        UpdateLoan message = new UpdateLoan();
        setMessageParameters(message, "0000038-999");

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
        Connection connection = new HttpConnection(Constants.BASE_URL);
        UpdateLoan message = new UpdateLoan();
        setMessageParameters(message, null);

        // Act
        Status status = message.send(connection);

        // Assert
        assertAll(
                () -> assertTrue(status.getWasSuccessful()),
                () -> assertEquals(0, status.getErrorCode()),
                () -> assertNull(status.getErrorMessage()),
                () -> assertNotNull(message.getLoanIdFromServer()),
                () -> assertEquals(Frequency.Yearly, message.getCompoundingFromServer()),
                () -> assertEquals(20, message.getTermFromServer()),
                () -> assertEquals("0000038", message.getCustomerIdFromServer()),
                () -> assertNotNull(message.getCustomerNameFromServer()),
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
        Connection connection = new HttpConnection(Constants.BASE_URL);
        UpdateLoan message = new UpdateLoan();
        setMessageParameters(message, "0000038-001");

        // Act
        Status status = message.send(connection);

        // Assert
        assertAll(
                () -> assertTrue(status.getWasSuccessful()),
                () -> assertEquals(0, status.getErrorCode()),
                () -> assertNull(status.getErrorMessage()),
                () -> assertEquals("0000038-001", message.getLoanIdFromServer()),
                () -> assertEquals(Frequency.Yearly, message.getCompoundingFromServer()),
                () -> assertEquals(20, message.getTermFromServer()),
                () -> assertEquals("0000038", message.getCustomerIdFromServer()),
                () -> assertNotNull(message.getCustomerNameFromServer()),
                () -> assertNotNull(message.getStatusFromServer()),
                () -> assertEquals(LocalDate.of(1998, 7, 6), message.getStartDateFromServer()),
                () -> assertEquals(18, message.getPeriodFromServer()),
                () -> assertEquals(963.36, message.getPaymentAmountFromServer()),
                () -> assertEquals(Frequency.Monthly, message.getPaymentFrequencyFromServer()),
                () -> assertEquals(650000.00, message.getPrincipalFromServer()),
                () -> assertEquals(RateType.Floating, message.getRateTypeFromServer()),
                () -> assertEquals(8.75, message.getRateValueFromServer())
        );
    }

}