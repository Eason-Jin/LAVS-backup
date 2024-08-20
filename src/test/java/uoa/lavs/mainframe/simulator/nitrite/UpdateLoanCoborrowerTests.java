package uoa.lavs.mainframe.simulator.nitrite;

import org.junit.jupiter.api.Test;
import uoa.lavs.mainframe.Connection;
import uoa.lavs.mainframe.Status;
import uoa.lavs.mainframe.messages.loan.UpdateLoanCoborrower;
import uoa.lavs.mainframe.simulator.NitriteConnection;

import static org.junit.jupiter.api.Assertions.*;
import static uoa.lavs.mainframe.MessageErrorStatus.INVALID_COBORROWER_ID;
import static uoa.lavs.mainframe.MessageErrorStatus.LOAN_NOT_FOUND;

class UpdateLoanCoborrowerTests {

    @Test
    public void handlesMissingLoan() {
        // Arrange
        Connection connection = new NitriteConnection();
        UpdateLoanCoborrower message = new UpdateLoanCoborrower();
        message.setLoanId("123-01");
        message.setCoborrowerId("456");

        // Act
        Status status = message.send(connection);

        // Assert
        assertAll(
                () -> assertEquals(LOAN_NOT_FOUND.getCode(), status.getErrorCode()),
                () -> assertEquals(LOAN_NOT_FOUND.getMessage(), status.getErrorMessage())
        );
    }

    @Test
    public void handlesMissingNumber() {
        // Arrange
        Connection connection = new NitriteConnection(
                DatabaseHelper.generateDefaultDatabase());
        UpdateLoanCoborrower message = new UpdateLoanCoborrower();
        message.setLoanId("123-09");
        message.setCoborrowerId("456");
        message.setNumber(null);

        // Act
        Status status = message.send(connection);

        // Assert
        assertAll(
                () -> assertTrue(status.getWasSuccessful()),
                () -> assertEquals(2, message.getNumberFromServer()),
                () -> assertEquals("123", message.getCustomerIdFromServer()),
                () -> assertEquals("John Doe", message.getCustomerNameFromServer()),
                () -> assertEquals("456", message.getCoborrowerIdFromServer()),
                () -> assertEquals("Jane Doe", message.getCoborrowerNameFromServer())
        );
    }

    @Test
    public void handlesExistingCoborrower() {
        // Arrange
        Connection connection = new NitriteConnection(
                DatabaseHelper.generateDefaultDatabase());
        UpdateLoanCoborrower message = new UpdateLoanCoborrower();
        message.setLoanId("123-09");
        message.setCoborrowerId("456");
        message.setNumber(1);

        // Act
        Status status = message.send(connection);

        // Assert
        assertAll(
                () -> assertTrue(status.getWasSuccessful()),
                () -> assertEquals(1, message.getNumberFromServer()),
                () -> assertEquals("123", message.getCustomerIdFromServer()),
                () -> assertEquals("John Doe", message.getCustomerNameFromServer()),
                () -> assertEquals("456", message.getCoborrowerIdFromServer()),
                () -> assertEquals("Jane Doe", message.getCoborrowerNameFromServer())
        );
    }

    @Test
    public void handlesNewCoborrower() {
        // Arrange
        Connection connection = new NitriteConnection(
                DatabaseHelper.generateDefaultDatabase());
        UpdateLoanCoborrower message = new UpdateLoanCoborrower();
        message.setLoanId("123-09");
        message.setCoborrowerId("456");
        message.setNumber(3);

        // Act
        Status status = message.send(connection);

        // Assert
        assertAll(
                () -> assertTrue(status.getWasSuccessful()),
                () -> assertEquals(2, message.getNumberFromServer()),
                () -> assertEquals("123", message.getCustomerIdFromServer()),
                () -> assertEquals("John Doe", message.getCustomerNameFromServer()),
                () -> assertEquals("456", message.getCoborrowerIdFromServer()),
                () -> assertEquals("Jane Doe", message.getCoborrowerNameFromServer())
        );
    }

    @Test
    public void handlesInvalidCoborrower() {
        // Arrange
        Connection connection = new NitriteConnection(
                DatabaseHelper.generateDefaultDatabase());
        UpdateLoanCoborrower message = new UpdateLoanCoborrower();
        message.setLoanId("123-09");
        message.setCoborrowerId("789");
        message.setNumber(1);

        // Act
        Status status = message.send(connection);

        // Assert
        assertAll(
                () -> assertEquals(INVALID_COBORROWER_ID.getCode(), status.getErrorCode()),
                () -> assertEquals(INVALID_COBORROWER_ID.getMessage(), status.getErrorMessage())
        );
    }

}