package uoa.lavs.mainframe.simulator.http;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import uoa.lavs.mainframe.Connection;
import uoa.lavs.mainframe.Status;
import uoa.lavs.mainframe.messages.customer.UpdateCustomerAddress;
import uoa.lavs.mainframe.simulator.HttpConnection;

import static org.junit.jupiter.api.Assertions.*;
import static uoa.lavs.mainframe.MessageErrorStatus.CUSTOMER_NOT_FOUND;

@EnabledIfEnvironmentVariable(named = "LAVS_ENVIRONMENT", matches = "http_test")
class UpdateCustomerAddressTests {
    private static void assertExpectedProperties(Status status, UpdateCustomerAddress message) {
        assertAll(
                () -> assertTrue(status.getWasSuccessful()),
                () -> assertEquals("Business", message.getTypeFromServer()),
                () -> assertEquals("99 Here Road", message.getLine1FromServer()),
                () -> assertEquals("MiddleOf", message.getLine2FromServer()),
                () -> assertEquals("Nowhere", message.getSuburbFromServer()),
                () -> assertEquals("Sydney", message.getCityFromServer()),
                () -> assertEquals("9876", message.getPostCodeFromServer()),
                () -> assertEquals("Australia", message.getCountryFromServer()),
                () -> assertFalse(message.getIsPrimaryFromServer()),
                () -> assertFalse(message.getIsMailingFromServer())
        );
    }

    private static void setMessageParameters(UpdateCustomerAddress message) {
        message.setCustomerId("0000038");
        message.setNumber(1);
        message.setType("Business");
        message.setLine1("99 Here Road");
        message.setLine2("MiddleOf");
        message.setSuburb("Nowhere");
        message.setCity("Sydney");
        message.setPostCode("9876");
        message.setCountry("Australia");
        message.setIsPrimary(false);
        message.setIsMailing(false);
    }

    @Test
    public void handlesMissingCustomer() {
        // Arrange
        Connection connection = new HttpConnection(Constants.BASE_URL);
        UpdateCustomerAddress message = new UpdateCustomerAddress();
        message.setCustomerId("0000021");
        message.setCountry("Australia");
        message.setLine1("99 Here Road");
        message.setType("Postal");

        // Act
        Status status = message.send(connection);

        // Assert
        assertAll(
                () -> assertEquals(CUSTOMER_NOT_FOUND.getCode(), status.getErrorCode()),
                () -> assertEquals(CUSTOMER_NOT_FOUND.getMessage(), status.getErrorMessage())
        );
    }

    @Test
    public void handlesNewAddress() {
        // Arrange
        Connection connection = new HttpConnection(Constants.BASE_URL);
        UpdateCustomerAddress message = new UpdateCustomerAddress();
        setMessageParameters(message);
        message.setNumber(null);

        // Act
        Status status = message.send(connection);

        // Assert
        assertExpectedProperties(status, message);
    }

    @Test
    public void handlesAddressUpdate() {
        // Arrange
        Connection connection = new HttpConnection(Constants.BASE_URL);
        UpdateCustomerAddress message = new UpdateCustomerAddress();
        setMessageParameters(message);

        // Act
        Status status = message.send(connection);

        // Assert
        assertExpectedProperties(status, message);
    }

}