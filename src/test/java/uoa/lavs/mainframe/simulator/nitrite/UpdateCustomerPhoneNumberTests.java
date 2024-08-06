package uoa.lavs.mainframe.simulator.nitrite;

import org.junit.jupiter.api.Test;
import uoa.lavs.mainframe.Connection;
import uoa.lavs.mainframe.Status;
import uoa.lavs.mainframe.messages.customer.UpdateCustomerPhoneNumber;
import uoa.lavs.mainframe.simulator.NitriteConnection;

import static org.junit.jupiter.api.Assertions.*;
import static uoa.lavs.mainframe.MessageErrorStatus.CUSTOMER_NOT_FOUND;

class UpdateCustomerPhoneNumberTests {
    @Test
    public void handlesMissingCustomer() {
        // Arrange
        Connection connection = new NitriteConnection();
        UpdateCustomerPhoneNumber message = new UpdateCustomerPhoneNumber();
        message.setCustomerId("123");

        // Act
        Status status = message.send(connection);

        // Assert
        assertAll(
                () -> assertEquals(CUSTOMER_NOT_FOUND.getCode(), status.getErrorCode()),
                () -> assertEquals(CUSTOMER_NOT_FOUND.getMessage(), status.getErrorMessage())
        );
    }

    @Test
    public void handlesNewPhoneNumber() {
        // Arrange
        Connection connection = new NitriteConnection(
                DatabaseHelper.generateDefaultDatabase());
        UpdateCustomerPhoneNumber message = new UpdateCustomerPhoneNumber();
        setMessageParameters(message);
        message.setNumber(2);

        // Act
        Status status = message.send(connection);

        // Assert
        assertExpectedProperties(status, message);
    }

    @Test
    public void handlesPhoneNumberUpdate() {
        // Arrange
        Connection connection = new NitriteConnection(
                DatabaseHelper.generateDefaultDatabase());
        UpdateCustomerPhoneNumber message = new UpdateCustomerPhoneNumber();
        setMessageParameters(message);

        // Act
        Status status = message.send(connection);

        // Assert
        assertExpectedProperties(status, message);
    }

    private static void assertExpectedProperties(Status status, UpdateCustomerPhoneNumber message) {
        assertAll(
                () -> assertTrue(status.getWasSuccessful()),
                () -> assertEquals("New", message.getTypeFromServer()),
                () -> assertEquals("321-7654", message.getPhoneNumberFromServer()),
                () -> assertEquals("+77", message.getPrefixFromServer()),
                () -> assertFalse( message.getIsPrimaryFromServer()),
                () -> assertFalse( message.getCanSendTxtFromServer())
        );
    }

    private static void setMessageParameters(UpdateCustomerPhoneNumber message) {
        message.setCustomerId("123");
        message.setNumber(1);
        message.setType("New");
        message.setPhoneNumber("321-7654");
        message.setPrefix("+77");
        message.setIsPrimary(false);
        message.setCanSendTxt(false);
    }
}