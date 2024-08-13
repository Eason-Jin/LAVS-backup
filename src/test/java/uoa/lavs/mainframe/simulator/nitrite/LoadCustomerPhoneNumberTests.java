package uoa.lavs.mainframe.simulator.nitrite;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import uoa.lavs.mainframe.Connection;
import uoa.lavs.mainframe.Request;
import uoa.lavs.mainframe.Response;
import uoa.lavs.mainframe.Status;
import uoa.lavs.mainframe.messages.customer.LoadCustomerPhoneNumber;
import uoa.lavs.mainframe.simulator.NitriteConnection;

import static org.junit.jupiter.api.Assertions.*;
import static uoa.lavs.mainframe.MessageErrorStatus.*;

class LoadCustomerPhoneNumberTests {
    @Test
    public void handlesMissingCustomer() {
        // Arrange
        Connection connection = new NitriteConnection();
        LoadCustomerPhoneNumber message = new LoadCustomerPhoneNumber();
        message.setCustomerId("123");

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
        Connection connection = new NitriteConnection();
        Request request = new Request(LoadCustomerPhoneNumber.REQUEST_CODE);
        request.setValue(LoadCustomerPhoneNumber.Fields.CUSTOMER_ID, id);

        // Act
        Response response = connection.send(request);

        // Assert
        Status status = response.getStatus();
        assertAll(
                () -> assertEquals(INVALID_REQUEST_CUSTOMER_ID.getCode(), status.getErrorCode()),
                () -> assertEquals(INVALID_REQUEST_CUSTOMER_ID.getMessage(), status.getErrorMessage())
        );
    }

    @ParameterizedTest
    @ValueSource(strings = {"12345678901", "abc"})
    @NullAndEmptySource
    public void handlesInvalidItemNumbers(String id) {
        // Arrange - these tests need to be low-level as we are bypassing the validation provided by the message
        Connection connection = new NitriteConnection(
                DatabaseHelper.generateDefaultDatabase());
        Request request = new Request(LoadCustomerPhoneNumber.REQUEST_CODE);
        request.setValue(LoadCustomerPhoneNumber.Fields.CUSTOMER_ID, "123");
        request.setValue(LoadCustomerPhoneNumber.Fields.NUMBER, id);

        // Act
        Response response = connection.send(request);

        // Assert
        Status status = response.getStatus();
        assertAll(
                () -> assertEquals(INVALID_REQUEST_NUMBER.getCode(), status.getErrorCode()),
                () -> assertEquals(INVALID_REQUEST_NUMBER.getMessage(), status.getErrorMessage())
        );
    }

    @Test
    public void handlesMissingCustomerEmail() {
        // Arrange
        Connection connection = new NitriteConnection(
                DatabaseHelper.generateDefaultDatabase());
        LoadCustomerPhoneNumber message = new LoadCustomerPhoneNumber();
        message.setCustomerId("123");
        message.setNumber(2);

        // Act
        Status status = message.send(connection);

        // Assert
        assertAll(
                () -> assertEquals(CUSTOMER_PHONE_NUMBER_NOT_FOUND.getCode(), status.getErrorCode()),
                () -> assertEquals(CUSTOMER_PHONE_NUMBER_NOT_FOUND.getMessage(), status.getErrorMessage())
        );
    }

    @Test
    public void handlesExistingCustomerEmail() {
        // Arrange
        Connection connection = new NitriteConnection(
                DatabaseHelper.generateDefaultDatabase());
        LoadCustomerPhoneNumber message = new LoadCustomerPhoneNumber();
        message.setCustomerId("123");
        message.setNumber(1);

        // Act
        Status status = message.send(connection);

        // Assert
        assertAll(
                () -> assertTrue(status.getWasSuccessful()),
                () -> assertEquals("Mobile", message.getTypeFromServer()),
                () -> assertEquals("+99", message.getPrefixFromServer()),
                () -> assertEquals("123-4567", message.getPhoneNumberFromServer()),
                () -> assertTrue(message.getIsPrimaryFromServer()),
                () -> assertTrue(message.getCanSendTxtFromServer())
        );
    }
}