package uoa.lavs.mainframe.simulator.nitrite;

import org.junit.jupiter.api.Test;
import uoa.lavs.mainframe.Connection;
import uoa.lavs.mainframe.Status;
import uoa.lavs.mainframe.messages.customer.UpdateCustomerNote;
import uoa.lavs.mainframe.simulator.NitriteConnection;

import static org.junit.jupiter.api.Assertions.*;
import static uoa.lavs.mainframe.MessageErrorStatus.CUSTOMER_NOT_FOUND;

class UpdateCustomerNoteTests {
    private static void assertExpectedProperties(Status status, UpdateCustomerNote message, Integer pageCount) {
        assertAll(
                () -> assertTrue(status.getWasSuccessful()),
                () -> assertEquals("Line #1", message.getLineFromServer(1)),
                () -> assertEquals("Line #2", message.getLineFromServer(2)),
                () -> assertEquals("Line #3", message.getLineFromServer(3)),
                () -> assertEquals("Line #4", message.getLineFromServer(4)),
                () -> assertEquals("Line #5", message.getLineFromServer(5)),
                () -> assertEquals(pageCount, message.getPageCountFromServer()),
                () -> assertEquals(5, message.getLineCountFromServer())
        );
    }

    private static void setMessageParameters(UpdateCustomerNote message) {
        message.setCustomerId("123");
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
        Connection connection = new NitriteConnection();
        UpdateCustomerNote message = new UpdateCustomerNote();
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
    public void handlesNewNote() {
        // Arrange
        Connection connection = new NitriteConnection(
                DatabaseHelper.generateDefaultDatabase());
        UpdateCustomerNote message = new UpdateCustomerNote();
        setMessageParameters(message);
        message.setNumber(2);

        // Act
        Status status = message.send(connection);

        // Assert
        assertExpectedProperties(status, message, 2);
    }

    @Test
    public void handlesNoteUpdate() {
        // Arrange
        Connection connection = new NitriteConnection(
                DatabaseHelper.generateDefaultDatabase());
        UpdateCustomerNote message = new UpdateCustomerNote();
        setMessageParameters(message);

        // Act
        Status status = message.send(connection);

        // Assert
        assertExpectedProperties(status, message, 1);
    }
}