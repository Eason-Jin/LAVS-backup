package uoa.lavs.mainframe.simulator.http;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import uoa.lavs.mainframe.Connection;
import uoa.lavs.mainframe.Status;
import uoa.lavs.mainframe.messages.customer.UpdateCustomer;
import uoa.lavs.mainframe.simulator.HttpConnection;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static uoa.lavs.mainframe.MessageErrorStatus.CUSTOMER_NOT_FOUND;

@EnabledIfEnvironmentVariable(named = "LAVS_ENVIRONMENT", matches = "http_test")
class UpdateCustomerTests {
    private static void setMessageParameters(UpdateCustomer message, String id) {
        message.setCustomerId(id);
        message.setName("Jane Doe");
        message.setTitle("Mrs");
        message.setOccupation("Test Mannequin");
        message.setCitizenship("Australia");
        message.setVisa("Automatic");
        message.setDateofBirth(LocalDate.of(1998, 7, 6));
    }

    @Test
    public void handlesMissingCustomer() {
        // Arrange
        Connection connection = new HttpConnection(Constants.BASE_URL);
        UpdateCustomer message = new UpdateCustomer();
        setMessageParameters(message, "0000021");

        // Act
        Status status = message.send(connection);

        // Assert
        assertAll(
                () -> assertEquals(CUSTOMER_NOT_FOUND.getCode(), status.getErrorCode()),
                () -> assertEquals(CUSTOMER_NOT_FOUND.getMessage(), status.getErrorMessage())
        );
    }

    @Test
    public void handlesNewCustomer() {
        // Arrange
        Connection connection = new HttpConnection(Constants.BASE_URL);
        UpdateCustomer message = new UpdateCustomer();
        setMessageParameters(message, null);

        // Act
        Status status = message.send(connection);

        // Assert
        assertAll(
                () -> assertTrue(status.getWasSuccessful()),
                () -> assertNotNull(message.getCustomerIdFromServer()),
                () -> assertEquals("Jane Doe", message.getNameFromServer()),
                () -> assertEquals("Mrs", message.getTitleFromServer()),
                () -> assertEquals("Test Mannequin", message.getOccupationFromServer()),
                () -> assertEquals("Automatic", message.getVisaFromServer()),
                () -> assertEquals("Australia", message.getCitizenshipFromServer()),
                () -> assertEquals("Active", message.getStatusFromServer()),
                () -> assertEquals(LocalDate.of(1998, 7, 6), message.getDateofBirthFromServer())
        );
    }

    @Test
    public void handlesExistingCustomer() {
        // Arrange
        Connection connection = new HttpConnection(Constants.BASE_URL);
        UpdateCustomer message = new UpdateCustomer();
        setMessageParameters(message, "0000038");

        // Act
        Status status = message.send(connection);

        // Assert
        assertAll(
                () -> assertTrue(status.getWasSuccessful()),
                () -> assertNotNull("0000038", message.getCustomerIdFromServer()),
                () -> assertEquals("Jane Doe", message.getNameFromServer()),
                () -> assertEquals("Mrs", message.getTitleFromServer()),
                () -> assertEquals("Test Mannequin", message.getOccupationFromServer()),
                () -> assertEquals("Automatic", message.getVisaFromServer()),
                () -> assertEquals("Australia", message.getCitizenshipFromServer()),
                () -> assertEquals("Active", message.getStatusFromServer()),
                () -> assertEquals(LocalDate.of(1998, 7, 6), message.getDateofBirthFromServer())
        );
    }

}