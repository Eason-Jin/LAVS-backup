package uoa.lavs.mainframe.simulator.http;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import uoa.lavs.mainframe.Connection;
import uoa.lavs.mainframe.Status;
import uoa.lavs.mainframe.messages.loan.UpdateLoanCoborrower;
import uoa.lavs.mainframe.simulator.HttpConnection;
import uoa.lavs.mainframe.simulator.NitriteConnection;

import static org.junit.jupiter.api.Assertions.*;
import static uoa.lavs.mainframe.MessageErrorStatus.INVALID_COBORROWER_ID;
import static uoa.lavs.mainframe.MessageErrorStatus.LOAN_NOT_FOUND;

@EnabledIfEnvironmentVariable(named = "LAVS_ENVIRONMENT", matches = "http_test")
class UpdateLoanCoborrowerTests {

    @Test
    public void handlesMissingLoan() {
        // Arrange
        Connection connection = new NitriteConnection();
        UpdateLoanCoborrower message = new UpdateLoanCoborrower();
        message.setLoanId("9999999-999");
        message.setCoborrowerId("9999987");
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
    public void handlesExistingCoborrower() {
        // Arrange
        Connection connection = new HttpConnection(Constants.BASE_URL);
        UpdateLoanCoborrower message = new UpdateLoanCoborrower();
        message.setLoanId("9999999-001");
        message.setCoborrowerId("9999987");
        message.setNumber(1);

        // Act
        Status status = message.send(connection);

        // Assert
        assertAll(
                () -> assertTrue(status.getWasSuccessful()),
                () -> assertEquals(0, status.getErrorCode()),
                () -> assertNull(status.getErrorMessage()),
                () -> assertEquals(1, message.getNumberFromServer()),
                () -> assertEquals("9999999", message.getCustomerIdFromServer()),
                () -> assertEquals("Janet Doe", message.getCustomerNameFromServer()),
                () -> assertEquals("9999987", message.getCoborrowerIdFromServer()),
                () -> assertEquals("Joseph Doe", message.getCoborrowerNameFromServer())
        );
    }

    @Test
    public void handlesNewCoborrower() {
        // Arrange
        Connection connection = new HttpConnection(Constants.BASE_URL);
        UpdateLoanCoborrower message = new UpdateLoanCoborrower();
        message.setLoanId("9999999-001");
        message.setCoborrowerId("9999987");
        message.setNumber(null);

        // Act
        Status status = message.send(connection);

        // Assert
        assertAll(
                () -> assertTrue(status.getWasSuccessful()),
                () -> assertEquals(0, status.getErrorCode()),
                () -> assertNull(status.getErrorMessage()),
                () -> assertTrue(message.getNumberFromServer() > 1),
                () -> assertEquals("9999999", message.getCustomerIdFromServer()),
                () -> assertEquals("Janet Doe", message.getCustomerNameFromServer()),
                () -> assertEquals("9999987", message.getCoborrowerIdFromServer()),
                () -> assertEquals("Joseph Doe", message.getCoborrowerNameFromServer())
        );
    }

    @Test
    public void handlesInvalidCoborrower() {
        // Arrange
        Connection connection = new HttpConnection(Constants.BASE_URL);
        UpdateLoanCoborrower message = new UpdateLoanCoborrower();
        message.setLoanId("9999999-001");
        message.setCoborrowerId("0000011");
        message.setNumber(1);

        // Act
        Status status = message.send(connection);

        // Assert
        assertAll(
                () -> assertEquals(INVALID_COBORROWER_ID.getCode(), status.getErrorCode())
        );
    }

}