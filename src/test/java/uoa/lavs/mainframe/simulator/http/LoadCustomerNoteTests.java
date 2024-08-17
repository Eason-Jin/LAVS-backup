package uoa.lavs.mainframe.simulator.http;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import uoa.lavs.mainframe.Connection;
import uoa.lavs.mainframe.Request;
import uoa.lavs.mainframe.Response;
import uoa.lavs.mainframe.Status;
import uoa.lavs.mainframe.messages.customer.LoadCustomerNote;
import uoa.lavs.mainframe.simulator.HttpConnection;

import static org.junit.jupiter.api.Assertions.*;
import static uoa.lavs.mainframe.MessageErrorStatus.*;

@EnabledIfEnvironmentVariable(named = "LAVS_ENVIRONMENT", matches = "http_test")
class LoadCustomerNoteTests {
    @Test
    public void handlesMissingCustomer() {
        // Arrange
        Connection connection = new HttpConnection(Constants.BASE_URL);
        LoadCustomerNote message = new LoadCustomerNote();
        message.setCustomerId("000021");
        message.setNumber(1);

        // Act
        Status status = message.send(connection);

        // Assert
        assertAll(
                () -> assertEquals(CUSTOMER_NOT_FOUND.getCode(), status.getErrorCode()),
                () -> assertEquals(CUSTOMER_NOT_FOUND.getMessage(), status.getErrorMessage())
        );
    }

    @ParameterizedTest
    @ValueSource(strings = {"12345678901", "abc"})
    @NullAndEmptySource
    public void handlesInvalidCustomerNumbers(String id) {
        // Arrange - these tests need to be low-level as we are bypassing the validation provided by the message
        Connection connection = new HttpConnection(Constants.BASE_URL);
        Request request = new Request(LoadCustomerNote.REQUEST_CODE);
        request.setValue(LoadCustomerNote.Fields.CUSTOMER_ID, id);

        // Act
        Response response = connection.send(request);

        // Assert
        Status status = response.getStatus();
        assertAll(
                () -> assertEquals(INVALID_REQUEST_CUSTOMER_ID.getCode(), status.getErrorCode())
        );
    }

    @ParameterizedTest
    @ValueSource(strings = {"12345678901", "abc"})
    @NullAndEmptySource
    public void handlesInvalidPageNumbers(String id) {
        // Arrange - these tests need to be low-level as we are bypassing the validation provided by the message
        Connection connection = new HttpConnection(Constants.BASE_URL);
        Request request = new Request(LoadCustomerNote.REQUEST_CODE);
        request.setValue(LoadCustomerNote.Fields.CUSTOMER_ID, "0000022");
        request.setValue(LoadCustomerNote.Fields.NUMBER, id);

        // Act
        Response response = connection.send(request);

        // Assert
        Status status = response.getStatus();
        assertAll(
                () -> assertEquals(INVALID_REQUEST_NUMBER.getCode(), status.getErrorCode())
        );
    }

    @Test
    public void handlesMissingNotes() {
        // Arrange
        Connection connection = new HttpConnection(Constants.BASE_URL);
        LoadCustomerNote message = new LoadCustomerNote();
        message.setCustomerId("0000022");
        message.setNumber(2);

        // Act
        Status status = message.send(connection);

        // Assert
        assertAll(
                () -> assertEquals(CUSTOMER_NOTE_NOT_FOUND.getCode(), status.getErrorCode()),
                () -> assertEquals(CUSTOMER_NOTE_NOT_FOUND.getMessage(), status.getErrorMessage())
        );
    }

    @Test
    public void handlesExistingNotes() {
        // Arrange
        Connection connection = new HttpConnection(Constants.BASE_URL);
        LoadCustomerNote message = new LoadCustomerNote();
        message.setCustomerId("0000022");
        message.setNumber(1);

        // Act
        Status status = message.send(connection);

        // Assert
        assertAll(
                () -> assertTrue(status.getWasSuccessful()),
                () -> assertEquals(0, status.getErrorCode()),
                () -> assertNull(status.getErrorMessage()),
                () -> assertEquals(2, message.getLineCountFromServer()),
                () -> assertEquals("Line #1", message.getLineFromServer(1))
        );
    }

    @Test
    public void handlesNoteZero() {
        // Arrange
        Connection connection = new HttpConnection(Constants.BASE_URL);
        LoadCustomerNote message = new LoadCustomerNote();
        message.setCustomerId("0000022");
        message.setNumber(0);

        // Act
        Status status = message.send(connection);

        // Assert
        assertAll(
                () -> assertTrue(status.getWasSuccessful()),
                () -> assertEquals(0, status.getErrorCode()),
                () -> assertNull(status.getErrorMessage()),
                () -> assertEquals(0, message.getLineCountFromServer()),
                () -> assertEquals(1, message.getPageCountFromServer())
        );
    }

    @Test
    public void retrievesPageCount() {
        // Arrange
        Connection connection = new HttpConnection(Constants.BASE_URL);
        LoadCustomerNote message = new LoadCustomerNote();
        message.setCustomerId("0000022");
        message.setNumber(1);

        // Act
        message.send(connection);

        // Assert
        assertEquals(1, message.getPageCountFromServer());
    }
}