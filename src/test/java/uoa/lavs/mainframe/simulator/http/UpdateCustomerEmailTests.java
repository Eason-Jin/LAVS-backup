package uoa.lavs.mainframe.simulator.http;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import uoa.lavs.mainframe.Connection;
import uoa.lavs.mainframe.Status;
import uoa.lavs.mainframe.messages.customer.UpdateCustomerEmail;
import uoa.lavs.mainframe.simulator.HttpConnection;

import static org.junit.jupiter.api.Assertions.*;
import static uoa.lavs.mainframe.MessageErrorStatus.CUSTOMER_NOT_FOUND;

@EnabledIfEnvironmentVariable(named = "LAVS_ENVIRONMENT", matches = "http_test")
class UpdateCustomerEmailTests {
    private static void assertExpectedProperties(Status status, UpdateCustomerEmail message) {
        assertAll(
                () -> assertTrue(status.getWasSuccessful()),
                () -> assertEquals(0, status.getErrorCode()),
                () -> assertNull(status.getErrorMessage()),
                () -> assertEquals("me@nowhere.com", message.getAddressFromServer()),
                () -> assertFalse(message.getIsPrimaryFromServer())
        );
    }

    private static void setMessageParameters(UpdateCustomerEmail message) {
        message.setCustomerId("0000038");
        message.setNumber(1);
        message.setAddress("me@nowhere.com");
        message.setIsPrimary(false);
    }

    @Test
    public void handlesMissingCustomer() {
        // Arrange
        Connection connection = new HttpConnection(Constants.BASE_URL);
        UpdateCustomerEmail message = new UpdateCustomerEmail();
        message.setCustomerId("0000021");
        message.setNumber(1);
        message.setAddress("me@nowhere.com");

        // Act
        Status status = message.send(connection);

        // Assert
        assertAll(
                () -> assertEquals(CUSTOMER_NOT_FOUND.getCode(), status.getErrorCode()),
                () -> assertEquals(CUSTOMER_NOT_FOUND.getMessage(), status.getErrorMessage())
        );
    }

    @Test
    public void handlesNewEmail() {
        // Arrange
        Connection connection = new HttpConnection(Constants.BASE_URL);
        UpdateCustomerEmail message = new UpdateCustomerEmail();
        setMessageParameters(message);
        message.setNumber(null);

        // Act
        Status status = message.send(connection);

        // Assert
        assertExpectedProperties(status, message);
    }

    @Test
    public void handlesEmailUpdate() {
        // Arrange
        Connection connection = new HttpConnection(Constants.BASE_URL);
        UpdateCustomerEmail message = new UpdateCustomerEmail();
        setMessageParameters(message);

        // Act
        Status status = message.send(connection);

        // Assert
        assertExpectedProperties(status, message);
    }
}