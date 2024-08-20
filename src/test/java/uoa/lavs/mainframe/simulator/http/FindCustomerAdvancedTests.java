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
import uoa.lavs.mainframe.messages.customer.FindCustomerAdvanced;
import uoa.lavs.mainframe.simulator.HttpConnection;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;
import static uoa.lavs.mainframe.MessageErrorStatus.INVALID_REQUEST_SEARCH;

@EnabledIfEnvironmentVariable(named = "LAVS_ENVIRONMENT", matches = "http_test")
class FindCustomerAdvancedTests {
    @Test
    public void handlesMissingCustomer() {
        // Arrange
        Connection connection = new HttpConnection(Constants.BASE_URL);
        FindCustomerAdvanced message = new FindCustomerAdvanced();
        message.setSearchName("Moana");

        // Act
        Status status = message.send(connection);

        // Assert
        assertAll(
                () -> assertTrue(status.getWasSuccessful()),
                () -> assertEquals(0, status.getErrorCode()),
                () -> assertNull(status.getErrorMessage()),
                () -> assertEquals(0, message.getCustomerCountFromServer())
        );
    }

    @ParameterizedTest
    @ValueSource(strings = {"1", "12"})
    @NullAndEmptySource
    public void handlesInvalidCustomerNumbers(String id) {
        // Arrange - these tests need to be low-level as we are bypassing the validation provided by the message
        Connection connection = new HttpConnection(Constants.BASE_URL);
        Request request = new Request(FindCustomerAdvanced.REQUEST_CODE);
        request.setValue(FindCustomerAdvanced.Fields.NAME, id);

        // Act
        Response response = connection.send(request);

        // Assert
        Status status = response.getStatus();
        assertEquals(INVALID_REQUEST_SEARCH.getCode(), status.getErrorCode());
    }

    @Test
    public void handlesExistingCustomer() {
        // Arrange
        Connection connection = new HttpConnection(Constants.BASE_URL);
        FindCustomerAdvanced message = new FindCustomerAdvanced();
        message.setSearchName("John");

        // Act
        Status status = message.send(connection);

        // Assert
        assertAll(
                () -> assertTrue(status.getWasSuccessful()),
                () -> assertEquals(0, status.getErrorCode()),
                () -> assertNull(status.getErrorMessage()),
                () -> assertTrue(message.getCustomerCountFromServer() > 0)
        );
    }

    @Test
    public void handlesMultipleCustomers() {
        // Arrange
        Connection connection = new HttpConnection(Constants.BASE_URL);
        FindCustomerAdvanced message = new FindCustomerAdvanced();
        message.setSearchName("Smith");

        // Act
        Status status = message.send(connection);
        ArrayList<String> customers = new ArrayList<>();
        for (int row = 1; row <= message.getCustomerCountFromServer(); row++) {
            String id = message.getIdFromServer(row);
            String name = message.getNameFromServer(row);
            customers.add(id + ":" + name);
        }
        customers.sort(String::compareTo);      // the order of customers is not guaranteed

        // Assert
        assertTrue(status.getWasSuccessful());
        String actual = String.join(",", customers);
        assertAll(
                () -> assertEquals("8888864:Pedro Smith,8888876:Petra Smith,8888888:Peter Smith", actual),
                () -> assertEquals(3, message.getCustomerCountFromServer())
        );
    }

}