package uoa.lavs.mainframe.simulator.nitrite;

import org.junit.jupiter.api.Test;
import uoa.lavs.mainframe.Connection;
import uoa.lavs.mainframe.Status;
import uoa.lavs.mainframe.messages.customer.LoadCustomer;
import uoa.lavs.mainframe.messages.customer.UpdateCustomer;
import uoa.lavs.mainframe.simulator.NitriteConnection;

import static org.junit.jupiter.api.Assertions.*;

public class IntegrationTests {
    @Test
    public void updateLoadCustomer() {
        // Arrange
        Connection connection = new NitriteConnection("testing/lavs-data.db");

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

        // Assert
        assertEquals("John Doe", load.getNameFromServer());
    }
}
