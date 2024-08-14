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
import uoa.lavs.mainframe.messages.customer.LoadCustomer;
import uoa.lavs.mainframe.simulator.HttpConnection;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static uoa.lavs.mainframe.MessageErrorStatus.CUSTOMER_NOT_FOUND;
import static uoa.lavs.mainframe.MessageErrorStatus.INVALID_REQUEST_CUSTOMER_ID;

@EnabledIfEnvironmentVariable(named = "LAVS_ENVIRONMENT", matches = "http_test")
class LoadCustomerTests {
    @Test
    public void handlesMissingCustomer() {
        // Arrange
        Connection connection = new HttpConnection(Constants.BASE_URL);
        LoadCustomer message = new LoadCustomer();
        message.setCustomerId("0000021");

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
        Request request = new Request(LoadCustomer.REQUEST_CODE);
        request.setValue(LoadCustomer.Fields.CUSTOMER_ID, id);

        // Act
        Response response = connection.send(request);

        // Assert
        Status status = response.getStatus();
        assertAll(
                () -> assertEquals(INVALID_REQUEST_CUSTOMER_ID.getCode(), status.getErrorCode())
        );
    }

    @Test
    public void handlesExistingCustomer() {
        // Arrange
        Connection connection = new HttpConnection(Constants.BASE_URL);
        LoadCustomer message = new LoadCustomer();
        message.setCustomerId("0000022");

        // Act
        Status status = message.send(connection);

        // Assert
        assertAll(
                () -> assertTrue(status.getWasSuccessful()),
                () -> assertEquals(0, status.getErrorCode()),
                () -> assertEquals("John Doe", message.getNameFromServer()),
                () -> assertEquals("Mr", message.getTitleFromServer()),
                () -> assertEquals("Test Dummy", message.getOccupationFromServer()),
                () -> assertEquals("n/a", message.getVisaFromServer()),
                () -> assertEquals("New Zealand", message.getCitizenshipFromServer()),
                () -> assertEquals("Active", message.getStatusFromServer()),
                () -> assertEquals(LocalDate.of(2003, 2, 1), message.getDateofBirthFromServer())
        );
    }
}