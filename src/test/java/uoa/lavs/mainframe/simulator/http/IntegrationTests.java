package uoa.lavs.mainframe.simulator.http;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import uoa.lavs.mainframe.Connection;
import uoa.lavs.mainframe.Frequency;
import uoa.lavs.mainframe.RateType;
import uoa.lavs.mainframe.Status;
import uoa.lavs.mainframe.messages.customer.*;
import uoa.lavs.mainframe.messages.loan.UpdateLoan;
import uoa.lavs.mainframe.simulator.HttpConnection;

import java.io.IOException;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@EnabledIfEnvironmentVariable(named = "LAVS_ENVIRONMENT", matches = "http_test")
public class IntegrationTests {
    private static String getTypeName(Object message) {
        String fullName = message.getClass().getName();
        String[] parts = fullName.split("\\.");
        return parts[parts.length - 1];
    }

    @Test
    public void addingMultipleCustomersWillUpdateTheNextId() throws IOException {
        // Arrange
        Connection connection = new HttpConnection(Constants.BASE_URL);

        try {
            // Act 1: add the first customer
            UpdateCustomer update = new UpdateCustomer();
            update.setCustomerId(null);
            update.setName("John Doe");
            Status firstStatus = update.send(connection);
            String firstId = update.getCustomerIdFromServer();

            // Assert 1
            assertTrue(firstStatus.getWasSuccessful());

            // Act 2: add the second customer
            update.setCustomerId(null);
            Status secondStatus = update.send(connection);
            String secondId = update.getCustomerIdFromServer();

            // Assert 2
            assertAll(
                    () -> assertTrue(secondStatus.getWasSuccessful()),
                    () -> assertNotEquals(firstId, secondId)
            );

            // Act 3: add the third customer
            update.setCustomerId(null);
            Status thirdStatus = update.send(connection);
            String thirdId = update.getCustomerIdFromServer();
            assertAll(
                    () -> assertTrue(thirdStatus.getWasSuccessful()),
                    () -> assertNotEquals(firstId, thirdId),
                    () -> assertNotEquals(thirdId, secondId)
            );
        } finally {
            connection.close();
        }
    }

    @Test
    public void updateLoadCustomer() throws IOException {
        // Arrange
        Connection connection = new HttpConnection(Constants.BASE_URL);

        try {
            // Act 1: add a new customer
            UpdateCustomer update = new UpdateCustomer();
            update.setCustomerId(null);
            update.setName("John Doe");
            Status status = update.send(connection);
            assertTrue(status.getWasSuccessful());

            // Act 2: retrieve the customer details
            LoadCustomer load = new LoadCustomer();
            load.setCustomerId(update.getCustomerIdFromServer());
            status = load.send(connection);
            assertTrue(status.getWasSuccessful());

            // Act 3: attempt to find the customer
            FindCustomer find = new FindCustomer();
            find.setCustomerId(update.getCustomerIdFromServer());
            status = find.send(connection);
            assertTrue(status.getWasSuccessful());

            // Assert
            assertAll(
                    () -> assertEquals("John Doe", load.getNameFromServer()),
                    () -> assertEquals("John Doe", find.getNameFromServer(1))
            );
        } finally {
            connection.close();
        }
    }

    @Test
    public void addACustomerAndALoan() throws IOException {
        // Arrange
        Connection connection = new HttpConnection(Constants.BASE_URL);

        try {
            // Act 1: add a new customer
            UpdateCustomer newCustomer = new UpdateCustomer();
            newCustomer.setCustomerId(null);
            newCustomer.setName("John Doe");
            Status customerStatus = newCustomer.send(connection);
            assertTrue(customerStatus.getWasSuccessful());
            String customerId = newCustomer.getCustomerIdFromServer();

            // Act 2: add a new loan for the customer
            UpdateLoan newLoan = new UpdateLoan();
            newLoan.setCustomerId(customerId);
            newLoan.setLoanId(null);
            newLoan.setTerm(30);
            newLoan.setStartDate(LocalDate.of(2024, 8, 1));
            newLoan.setRateValue(6.54);
            newLoan.setRateType(RateType.Fixed);
            newLoan.setPrincipal(10000.00);
            newLoan.setPeriod(12);
            newLoan.setPaymentFrequency(Frequency.Weekly);
            newLoan.setPaymentAmount(100.00);
            newLoan.setCompounding(Frequency.Weekly);
            Status loanStatus = newLoan.send(connection);

            // Assert
            assertAll(
                    () -> assertTrue(loanStatus.getWasSuccessful()),
                    () -> assertTrue(newLoan.getLoanIdFromServer().startsWith(customerId),
                            "New loan ID does not start with " + customerId)
            );
        } finally {
            connection.close();
        }
    }

    private Status runUpdateCustomerChildTest(
            UpdateCustomerChildMessage message,
            Connection connection,
            String customerId) throws IOException {
        message.setCustomerId(customerId);
        message.setNumber(null);
        Status status = message.send(connection);
        assertAll(
                () -> assertTrue(status.getWasSuccessful()),
                () -> assertEquals(0, status.getErrorCode()),
                () -> assertNull(status.getErrorMessage()),
                () -> assertNotNull(message.getNumberFromServer(), "Child update message " + getTypeName(message) + " returned null")
        );
        return status;
    }

    @Test
    public void addNewCustomerAndAllChildItems() throws IOException {
        // Arrange
        Connection connection = new HttpConnection(Constants.BASE_URL);

        try {
            // Act 1: add a new customer
            UpdateCustomer addCustomer = new UpdateCustomer();
            addCustomer.setCustomerId(null);
            addCustomer.setName("John Doe");
            Status status = addCustomer.send(connection);
            assertTrue(status.getWasSuccessful());
            String customerId = addCustomer.getCustomerIdFromServer();

            // Act 2: add a new address
            UpdateCustomerAddress updateCustomerAddress = new UpdateCustomerAddress();
            updateCustomerAddress.setCountry("New Zealand");
            updateCustomerAddress.setLine1("An Address");
            updateCustomerAddress.setType("Test");
            Status addressStatus = runUpdateCustomerChildTest(updateCustomerAddress, connection, customerId);

            // Act 3: add a new phone number
            UpdateCustomerPhoneNumber updateCustomerPhoneNumber = new UpdateCustomerPhoneNumber();
            updateCustomerPhoneNumber.setPhoneNumber("123-4567");
            updateCustomerPhoneNumber.setPrefix("+9");
            updateCustomerPhoneNumber.setType("Test");
            Status phoneStatus = runUpdateCustomerChildTest(updateCustomerPhoneNumber, connection, customerId);

            // Act 4: add a new email
            UpdateCustomerEmail updateCustomerEmail = new UpdateCustomerEmail();
            updateCustomerEmail.setAddress("me@nowhere.com");
            Status emailStatus = runUpdateCustomerChildTest(updateCustomerEmail, connection, customerId);

            // Act 5: add a page of notes
            Status noteStatus = runUpdateCustomerChildTest(new UpdateCustomerNote(), connection, customerId);

            // Act 6: add an employer
            UpdateCustomerEmployer updateCustomerEmployer = new UpdateCustomerEmployer();
            updateCustomerEmployer.setCountry("New Zealand");
            updateCustomerEmployer.setName("Test company");
            Status employerStatus = runUpdateCustomerChildTest(updateCustomerEmployer, connection, customerId);

            // Assert
            assertAll(
                    () -> assertTrue(addressStatus.getWasSuccessful(), "Add address failed"),
                    () -> assertNull(addressStatus.getErrorMessage(), "Add address failed"),
                    () -> assertTrue(phoneStatus.getWasSuccessful(), "Add phone number failed"),
                    () -> assertNull(phoneStatus.getErrorMessage(), "Add phone number failed"),
                    () -> assertTrue(emailStatus.getWasSuccessful(), "Add email failed"),
                    () -> assertNull(emailStatus.getErrorMessage(), "Add email failed"),
                    () -> assertTrue(noteStatus.getWasSuccessful(), "Add note failed"),
                    () -> assertNull(noteStatus.getErrorMessage(), "Add note failed"),
                    () -> assertTrue(employerStatus.getWasSuccessful(), "Add employer failed"),
                    () -> assertNull(employerStatus.getErrorMessage(), "Add employer failed")
            );
        } finally {
            connection.close();
        }
    }
}
