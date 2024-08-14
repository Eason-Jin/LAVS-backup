package uoa.lavs.mainframe.simulator.http;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import uoa.lavs.mainframe.Connection;
import uoa.lavs.mainframe.Status;
import uoa.lavs.mainframe.messages.customer.UpdateCustomerEmployer;
import uoa.lavs.mainframe.simulator.HttpConnection;

import static org.junit.jupiter.api.Assertions.*;
import static uoa.lavs.mainframe.MessageErrorStatus.CUSTOMER_NOT_FOUND;

@EnabledIfEnvironmentVariable(named = "LAVS_ENVIRONMENT", matches = "http_test")
class UpdateCustomerEmployerTests {
    private static void assertExpectedProperties(Status status, UpdateCustomerEmployer message) {
        assertAll(
                () -> assertTrue(status.getWasSuccessful()),
                () -> assertEquals(0, status.getErrorCode()),
                () -> assertNull(status.getErrorMessage()),
                () -> assertEquals("99 Here Road", message.getLine1FromServer()),
                () -> assertEquals("MiddleOf", message.getLine2FromServer()),
                () -> assertEquals("Nowhere", message.getSuburbFromServer()),
                () -> assertEquals("Sydney", message.getCityFromServer()),
                () -> assertEquals("9876", message.getPostCodeFromServer()),
                () -> assertEquals("Australia", message.getCountryFromServer()),
                () -> assertEquals("new@new.co.no", message.getEmailAddressFromServer()),
                () -> assertEquals("https://new.co.no", message.getWebsiteFromServer()),
                () -> assertEquals("+77-963-8520", message.getPhoneNumberFromServer()),
                () -> assertFalse(message.getIsOwnerFromServer())
        );
    }

    private static void setMessageParameters(UpdateCustomerEmployer message) {
        message.setCustomerId("0000038");
        message.setNumber(1);
        message.setName("Another Greater Employer");
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

    @Test
    public void handlesMissingCustomer() {
        // Arrange
        Connection connection = new HttpConnection(Constants.BASE_URL);
        UpdateCustomerEmployer message = new UpdateCustomerEmployer();
        message.setCustomerId("0000021");
        message.setNumber(1);
        message.setName("Another Greater Employer");
        message.setCountry("New Zealand");

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
        Connection connection = new HttpConnection(Constants.BASE_URL);
        UpdateCustomerEmployer message = new UpdateCustomerEmployer();
        setMessageParameters(message);
        message.setNumber(null);

        // Act
        Status status = message.send(connection);

        // Assert
        assertExpectedProperties(status, message);
    }

    @Test
    public void handlesEmployerUpdate() {
        // Arrange
        Connection connection = new HttpConnection(Constants.BASE_URL);
        UpdateCustomerEmployer message = new UpdateCustomerEmployer();
        setMessageParameters(message);

        // Act
        Status status = message.send(connection);

        // Assert
        assertExpectedProperties(status, message);
    }

}