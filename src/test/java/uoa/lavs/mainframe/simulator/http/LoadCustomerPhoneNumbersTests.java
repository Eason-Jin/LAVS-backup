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
import uoa.lavs.mainframe.messages.customer.LoadCustomerPhoneNumbers;
import uoa.lavs.mainframe.simulator.HttpConnection;

import static org.junit.jupiter.api.Assertions.*;
import static uoa.lavs.mainframe.MessageErrorStatus.CUSTOMER_NOT_FOUND;
import static uoa.lavs.mainframe.MessageErrorStatus.INVALID_REQUEST_CUSTOMER_ID;

@EnabledIfEnvironmentVariable(named = "LAVS_ENVIRONMENT", matches = "http_test")
class LoadCustomerPhoneNumbersTests {
    @Test
    public void handlesMissingCustomer() {
        // Arrange
        Connection connection = new HttpConnection(Constants.BASE_URL);
        LoadCustomerPhoneNumbers message = new LoadCustomerPhoneNumbers();
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
        Connection connection = new HttpConnection(Constants.BASE_URL);
        Request request = new Request(LoadCustomerPhoneNumbers.REQUEST_CODE);
        request.setValue(LoadCustomerPhoneNumbers.Fields.CUSTOMER_ID, id);

        // Act
        Response response = connection.send(request);

        // Assert
        Status status = response.getStatus();
        assertAll(
                () -> assertEquals(INVALID_REQUEST_CUSTOMER_ID.getCode(), status.getErrorCode())
        );
    }

    @Test
    public void handlesExistingCustomerPhoneNumbers() {
        // Arrange
        Connection connection = new HttpConnection(Constants.BASE_URL);
        LoadCustomerPhoneNumbers message = new LoadCustomerPhoneNumbers();
        message.setCustomerId("0000022");

        // Act
        Status status = message.send(connection);

        // Assert
        assertAll(
                () -> assertTrue(status.getWasSuccessful()),
                () -> assertEquals("Mobile", message.getTypeFromServer(1)),
                () -> assertEquals("+20", message.getPrefixFromServer(1)),
                () -> assertEquals("123-4567", message.getPhoneNumberFromServer(1)),
                () -> assertTrue(message.getIsPrimaryFromServer(1)),
                () -> assertTrue(message.getCanSendTxtFromServer(1))
        );
    }
}