package uoa.lavs.mainframe.simulator.nitrite;

import org.junit.jupiter.api.Test;
import uoa.lavs.mainframe.Connection;
import uoa.lavs.mainframe.Status;
import uoa.lavs.mainframe.messages.customer.*;
import uoa.lavs.mainframe.simulator.NitriteConnection;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

public class IntegrationTests {
    @Test
    public void addingMultipleCustomersWillUpdateTheNextId() throws IOException {
        // Arrange
        Connection connection = new NitriteConnection("testing/lavs-data.db");

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
        Connection connection = new NitriteConnection("testing/lavs-data.db");

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

    private Status runUpdateCustomerChildTest(
            UpdateCustomerChildMessage message,
            Connection connection,
            String customerId) throws IOException {
        message.setCustomerId(customerId);
        message.setNumber(null);
        Status status = message.send(connection);
        assertNotNull(message.getNumberFromServer());
        return status;
    }

    @Test
    public void addNewCustomerAndAllChildItems() throws IOException {
        // Arrange
        Connection connection = new NitriteConnection("testing/lavs-data.db");

        try {
            // Act 1: add a new customer
            UpdateCustomer addCustomer = new UpdateCustomer();
            addCustomer.setCustomerId(null);
            addCustomer.setName("John Doe");
            Status status = addCustomer.send(connection);
            assertTrue(status.getWasSuccessful());
            String customerId = addCustomer.getCustomerIdFromServer();

            // Act 2: add a new address
            Status addressStatus = runUpdateCustomerChildTest(new UpdateCustomerAddress(), connection, customerId);

            // Act 3: add a new phone number
            Status phoneStatus = runUpdateCustomerChildTest(new UpdateCustomerPhoneNumber(), connection, customerId);

            // Act 4: add a new email
            Status emailStatus = runUpdateCustomerChildTest(new UpdateCustomerEmail(), connection, customerId);

            // Act 5: add a page of notes
            Status noteStatus = runUpdateCustomerChildTest(new UpdateCustomerNote(), connection, customerId);

            // Act 6: add an employer
            Status employerStatus = runUpdateCustomerChildTest(new UpdateCustomerEmployer(), connection, customerId);

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
