package uoa.lavs.mainframe.simulator.http;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import uoa.lavs.mainframe.Connection;
import uoa.lavs.mainframe.LoanStatus;
import uoa.lavs.mainframe.Status;
import uoa.lavs.mainframe.messages.loan.UpdateLoanStatus;
import uoa.lavs.mainframe.simulator.HttpConnection;

import static org.junit.jupiter.api.Assertions.*;
import static uoa.lavs.mainframe.MessageErrorStatus.INVALID_REQUEST_LOAN_STATUS;
import static uoa.lavs.mainframe.MessageErrorStatus.LOAN_NOT_FOUND;

@EnabledIfEnvironmentVariable(named = "LAVS_ENVIRONMENT", matches = "http_test")
class UpdateLoanStatusTests {
    @Test
    public void handlesMissingLoan() {
        // Arrange
        Connection connection = new HttpConnection(Constants.BASE_URL);
        UpdateLoanStatus message = new UpdateLoanStatus();
        message.setLoanId("0000038-999");
        message.setStatus(LoanStatus.Cancelled);

        // Act
        Status status = message.send(connection);

        // Assert
        assertAll(
                () -> assertEquals(LOAN_NOT_FOUND.getCode(), status.getErrorCode()),
                () -> assertEquals(LOAN_NOT_FOUND.getMessage(), status.getErrorMessage())
        );
    }

    @Test
    public void handlesInvalidStatusUpdate() {
        // Arrange
        Connection connection = new HttpConnection(Constants.BASE_URL);
        UpdateLoanStatus message = new UpdateLoanStatus();
        message.setLoanId("0000038-001");
        message.setStatus(LoanStatus.Active);

        // Act
        Status status = message.send(connection);

        // Assert
        assertAll(
                () -> assertEquals(INVALID_REQUEST_LOAN_STATUS.getCode(), status.getErrorCode()),
                () -> assertEquals(INVALID_REQUEST_LOAN_STATUS.getMessage(), status.getErrorMessage())
        );
    }

    @Test
    public void handlesExistingLoan() {
        // Arrange
        Connection connection = new HttpConnection(Constants.BASE_URL);
        UpdateLoanStatus message = new UpdateLoanStatus();
        message.setLoanId("9999999-001");
        message.setStatus(LoanStatus.Cancelled);

        // Act
        Status status = message.send(connection);

        // Assert
        assertAll(
                () -> assertTrue(status.getWasSuccessful()),
                () -> assertEquals(0, status.getErrorCode()),
                () -> assertNull(status.getErrorMessage()),
                () -> assertEquals("9999999", message.getCustomerIdFromServer()),
                () -> assertEquals("Janet Doe", message.getCustomerNameFromServer()),
                () -> assertEquals(LoanStatus.Cancelled, message.getStatusFromServer())
        );
    }

}