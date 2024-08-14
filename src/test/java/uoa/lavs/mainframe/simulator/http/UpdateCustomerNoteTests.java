package uoa.lavs.mainframe.simulator.http;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import uoa.lavs.mainframe.Connection;
import uoa.lavs.mainframe.Status;
import uoa.lavs.mainframe.messages.customer.UpdateCustomerNote;
import uoa.lavs.mainframe.simulator.HttpConnection;

import static org.junit.jupiter.api.Assertions.*;
import static uoa.lavs.mainframe.MessageErrorStatus.CUSTOMER_NOT_FOUND;

@EnabledIfEnvironmentVariable(named = "LAVS_ENVIRONMENT", matches = "http_test")
class UpdateCustomerNoteTests {
    private static void assertExpectedProperties(Status status, UpdateCustomerNote message, Integer pageCount) {
        assertAll(
                () -> assertTrue(status.getWasSuccessful()),
                () -> assertEquals(0, status.getErrorCode()),
                () -> assertNull(status.getErrorMessage()),
                () -> assertEquals("Line #1", message.getLineFromServer(1)),
                () -> assertEquals("Line #2", message.getLineFromServer(2)),
                () -> assertEquals("Line #3", message.getLineFromServer(3)),
                () -> assertEquals("Line #4", message.getLineFromServer(4)),
                () -> assertEquals("Line #5", message.getLineFromServer(5)),
                () -> assertEquals(5, message.getLineCountFromServer())
        );
    }

    private static void setMessageParameters(UpdateCustomerNote message) {
        message.setCustomerId("0000038");
        message.setNumber(1);
        message.setLine(1, "Line #1");
        message.setLine(2, "Line #2");
        message.setLine(3, "Line #3");
        message.setLine(4, "Line #4");
        message.setLine(5, "Line #5");
    }

    @Test
    public void handlesMissingCustomer() {
        // Arrange
        Connection connection = new HttpConnection(Constants.BASE_URL);
        UpdateCustomerNote message = new UpdateCustomerNote();
        message.setCustomerId("0000037");
        message.setNumber(1);

        // Act
        Status status = message.send(connection);

        // Assert
        assertAll(
                () -> assertEquals(CUSTOMER_NOT_FOUND.getCode(), status.getErrorCode()),
                () -> assertEquals(CUSTOMER_NOT_FOUND.getMessage(), status.getErrorMessage())
        );
    }

    @Test
    public void handlesNewNote() {
        // Arrange
        Connection connection = new HttpConnection(Constants.BASE_URL);
        UpdateCustomerNote message = new UpdateCustomerNote();
        setMessageParameters(message);
        message.setNumber(null);

        // Act
        Status status = message.send(connection);

        // Assert
        assertExpectedProperties(status, message, 2);
        assertTrue(message.getPageCountFromServer() > 1);
    }

    @Test
    public void handlesNoteUpdate() {
        // Arrange
        Connection connection = new HttpConnection(Constants.BASE_URL);
        UpdateCustomerNote message = new UpdateCustomerNote();
        setMessageParameters(message);

        // Act
        Status status = message.send(connection);

        // Assert
        assertExpectedProperties(status, message, 1);
    }
}