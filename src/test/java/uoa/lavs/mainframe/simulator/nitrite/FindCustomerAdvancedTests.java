package uoa.lavs.mainframe.simulator.nitrite;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import uoa.lavs.mainframe.Connection;
import uoa.lavs.mainframe.Request;
import uoa.lavs.mainframe.Response;
import uoa.lavs.mainframe.Status;
import uoa.lavs.mainframe.messages.customer.FindCustomerAdvanced;
import uoa.lavs.mainframe.simulator.NitriteConnection;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static uoa.lavs.mainframe.MessageErrorStatus.INVALID_REQUEST_SEARCH;

class FindCustomerAdvancedTests {
    @Test
    public void handlesMissingCustomer() {
        // Arrange
        Connection connection = new NitriteConnection(
                DatabaseHelper.generateDefaultDatabase());
        FindCustomerAdvanced message = new FindCustomerAdvanced();
        message.setSearchName("Moana");

        // Act
        Status status = message.send(connection);

        // Assert
        assertAll(
                () -> assertTrue(status.getWasSuccessful()),
                () -> assertEquals(0, message.getCustomerCountFromServer())
        );
    }

    @ParameterizedTest
    @ValueSource(strings = {"1", "12"})
    @NullAndEmptySource
    public void handlesInvalidCustomerNumbers(String id) {
        // Arrange - these tests need to be low-level as we are bypassing the validation provided by the message
        Connection connection = new NitriteConnection();
        Request request = new Request(FindCustomerAdvanced.REQUEST_CODE);
        request.setValue(FindCustomerAdvanced.Fields.NAME, id);

        // Act
        Response response = connection.send(request);

        // Assert
        Status status = response.getStatus();
        assertAll(
                () -> assertEquals(INVALID_REQUEST_SEARCH.getCode(), status.getErrorCode()),
                () -> assertEquals(INVALID_REQUEST_SEARCH.getMessage(), status.getErrorMessage())
        );
    }

    @Test
    public void handlesExistingCustomer() {
        // Arrange
        Connection connection = new NitriteConnection(
                DatabaseHelper.generateDefaultDatabase());
        FindCustomerAdvanced message = new FindCustomerAdvanced();
        message.setSearchName("John");

        // Act
        Status status = message.send(connection);

        // Assert
        assertAll(
                () -> assertTrue(status.getWasSuccessful()),
                () -> assertEquals(1, message.getCustomerCountFromServer()),
                () -> assertEquals("123", message.getIdFromServer(1)),
                () -> assertEquals("John Doe", message.getNameFromServer(1)),
                () -> assertEquals(LocalDate.of(1945, 3, 12), message.getDateofBirthFromServer(1))
        );
    }

}