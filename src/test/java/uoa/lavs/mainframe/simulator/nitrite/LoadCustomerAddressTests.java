package uoa.lavs.mainframe.simulator.nitrite;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import uoa.lavs.mainframe.Connection;
import uoa.lavs.mainframe.Request;
import uoa.lavs.mainframe.Response;
import uoa.lavs.mainframe.Status;
import uoa.lavs.mainframe.messages.customer.LoadCustomerAddress;
import uoa.lavs.mainframe.simulator.NitriteConnection;

import static org.junit.jupiter.api.Assertions.*;
import static uoa.lavs.mainframe.MessageErrorStatus.*;

class LoadCustomerAddressTests {
    @Test
    public void handlesMissingCustomer() {
        // Arrange
        Connection connection = new NitriteConnection();
        LoadCustomerAddress message = new LoadCustomerAddress();
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
        Request request = new Request(LoadCustomerAddress.REQUEST_CODE);
        request.setValue(LoadCustomerAddress.Fields.CUSTOMER_ID, id);

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
        Request request = new Request(LoadCustomerAddress.REQUEST_CODE);
        request.setValue(LoadCustomerAddress.Fields.CUSTOMER_ID, "123");
        request.setValue(LoadCustomerAddress.Fields.NUMBER, id);

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
    public void handlesMissingCustomerAddress() {
        // Arrange
        Connection connection = new NitriteConnection(
                DatabaseHelper.generateDefaultDatabase());
        LoadCustomerAddress message = new LoadCustomerAddress();
        message.setCustomerId("123");
        message.setNumber(2);

        // Act
        Status status = message.send(connection);

        // Assert
        assertAll(
                () -> assertEquals(CUSTOMER_ADDRESS_NOT_FOUND.getCode(), status.getErrorCode()),
                () -> assertEquals(CUSTOMER_ADDRESS_NOT_FOUND.getMessage(), status.getErrorMessage())
        );
    }

    @Test
    public void handlesExistingCustomerAddress() {
        // Arrange
        Connection connection = new NitriteConnection(
                DatabaseHelper.generateDefaultDatabase());
        LoadCustomerAddress message = new LoadCustomerAddress();
        message.setCustomerId("123");
        message.setNumber(1);

        // Act
        Status status = message.send(connection);

        // Assert
        assertAll(
                () -> assertTrue(status.getWasSuccessful()),
                () -> assertEquals("Home", message.getTypeFromServer()),
                () -> assertEquals("5 Somewhere Lane", message.getLine1FromServer()),
                () -> assertEquals("Nowhere", message.getLine2FromServer()),
                () -> assertEquals("Important", message.getSuburbFromServer()),
                () -> assertEquals("Auckland", message.getCityFromServer()),
                () -> assertEquals("1234", message.getPostCodeFromServer()),
                () -> assertEquals("New Zealand", message.getCountryFromServer()),
                () -> assertTrue(message.getIsPrimaryFromServer()),
                () -> assertTrue(message.getIsMailingFromServer())
        );
    }
}