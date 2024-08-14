package uoa.lavs.mainframe.simulator.http;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import uoa.lavs.mainframe.Connection;
import uoa.lavs.mainframe.Status;
import uoa.lavs.mainframe.messages.customer.UpdateCustomerPhoneNumber;
import uoa.lavs.mainframe.simulator.HttpConnection;

import static org.junit.jupiter.api.Assertions.*;
import static uoa.lavs.mainframe.MessageErrorStatus.CUSTOMER_NOT_FOUND;

@EnabledIfEnvironmentVariable(named = "LAVS_ENVIRONMENT", matches = "http_test")
class UpdateCustomerPhoneNumberTests {
    private static void assertExpectedProperties(Status status, UpdateCustomerPhoneNumber message) {
        assertAll(
                () -> assertTrue(status.getWasSuccessful()),
                () -> assertEquals("New", message.getTypeFromServer()),
                () -> assertEquals("321-7654", message.getPhoneNumberFromServer()),
                () -> assertEquals("+77", message.getPrefixFromServer()),
                () -> assertFalse(message.getIsPrimaryFromServer()),
                () -> assertFalse(message.getCanSendTxtFromServer())
        );
    }

    private static void setMessageParameters(UpdateCustomerPhoneNumber message) {
        message.setCustomerId("0000038");
        message.setNumber(1);
        message.setType("New");
        message.setPhoneNumber("321-7654");
        message.setPrefix("+77");
        message.setIsPrimary(false);
        message.setCanSendTxt(false);
    }

    @Test
    public void handlesMissingCustomer() {
        // Arrange
        Connection connection = new HttpConnection(Constants.BASE_URL);
        UpdateCustomerPhoneNumber message = new UpdateCustomerPhoneNumber();
        message.setCustomerId("0000021");
        message.setType("Mobile");
        message.setPhoneNumber("321-7654");
        message.setPrefix("+77");

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
        Connection connection = new HttpConnection(Constants.BASE_URL);
        UpdateCustomerPhoneNumber message = new UpdateCustomerPhoneNumber();
        setMessageParameters(message);
        message.setNumber(2);

        // Act
        Status status = message.send(connection);

        // Assert
        assertExpectedProperties(status, message);
    }

    @Test
    public void handlesNewPhoneNumberViaNull() {
        // Arrange
        Connection connection = new HttpConnection(Constants.BASE_URL);
        UpdateCustomerPhoneNumber message = new UpdateCustomerPhoneNumber();
        setMessageParameters(message);
        message.setNumber(null);

        // Act
        Status status = message.send(connection);

        // Assert
        assertExpectedProperties(status, message);
    }

    @Test
    public void handlesPhoneNumberUpdate() {
        // Arrange
        Connection connection = new HttpConnection(Constants.BASE_URL);
        UpdateCustomerPhoneNumber message = new UpdateCustomerPhoneNumber();
        setMessageParameters(message);

        // Act
        Status status = message.send(connection);

        // Assert
        assertExpectedProperties(status, message);
    }
}