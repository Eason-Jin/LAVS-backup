package uoa.lavs.mainframe.simulator.nitrite;

import org.junit.jupiter.api.Test;
import uoa.lavs.mainframe.Connection;
import uoa.lavs.mainframe.Status;
import uoa.lavs.mainframe.messages.customer.UpdateCustomerEmployer;
import uoa.lavs.mainframe.simulator.NitriteConnection;

import static org.junit.jupiter.api.Assertions.*;
import static uoa.lavs.mainframe.MessageErrorStatus.CUSTOMER_NOT_FOUND;

class UpdateCustomerEmployerTests {
    @Test
    public void handlesMissingCustomer() {
        // Arrange
        Connection connection = new NitriteConnection();
        UpdateCustomerEmployer message = new UpdateCustomerEmployer();
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
    public void handlesNewEmployer() {
        // Arrange
        Connection connection = new NitriteConnection(
                DatabaseHelper.generateDefaultDatabase());
        UpdateCustomerEmployer message = new UpdateCustomerEmployer();
        setMessageParameters(message);
        message.setNumber(2);

        // Act
        Status status = message.send(connection);

        // Assert
        assertExpectedProperties(status, message);
    }

    @Test
    public void handlesEmployerUpdate() {
        // Arrange
        Connection connection = new NitriteConnection(
                DatabaseHelper.generateDefaultDatabase());
        UpdateCustomerEmployer message = new UpdateCustomerEmployer();
        setMessageParameters(message);

        // Act
        Status status = message.send(connection);

        // Assert
        assertExpectedProperties(status, message);
    }

    private static void assertExpectedProperties(Status status, UpdateCustomerEmployer message) {
        assertAll(
                () -> assertTrue(status.getWasSuccessful()),
                () -> assertEquals("99 Here Road", message.getLine1FromServer()),
                () -> assertEquals("MiddleOf", message.getLine2FromServer()),
                () -> assertEquals("Nowhere", message.getSuburbFromServer()),
                () -> assertEquals("Sydney", message.getCityFromServer()),
                () -> assertEquals("9876", message.getPostCodeFromServer()),
                () -> assertEquals("Australia", message.getCountryFromServer()),
                () -> assertEquals("new@new.co.no", message.getEmailAddressFromServer()),
                () -> assertEquals("https://new.co.no", message.getWebsiteFromServer()),
                () -> assertEquals("+77-963-8520", message.getPhoneNumberFromServer()),
                () -> assertFalse( message.getIsOwnerFromServer())
        );
    }

    private static void setMessageParameters(UpdateCustomerEmployer message) {
        message.setCustomerId("123");
        message.setNumber(1);
        message.setLine1("99 Here Road");
        message.setLine2("MiddleOf");
        message.setSuburb("Nowhere");
        message.setCity("Sydney");
        message.setPostCode("9876");
        message.setCountry("Australia");
        message.setEmailAddress("new@new.co.no");
        message.setWebsite("https://new.co.no");
        message.setPhoneNumber("+77-963-8520");
    }

}