package uoa.lavs.dataoperations.customer;

import org.junit.jupiter.api.Test;

import uoa.lavs.dataoperations.DataOperationsTestsHelper;
import uoa.lavs.models.Customer;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDate;

public class CustomerLoaderTests {

    @Test
    public void loadExistingCustomerFromDatabase() throws Exception {
        DataOperationsTestsHelper.createTestingDatabases();

        Customer customer = CustomerLoader.loadFromDatabase("1");

        // Assert
        assertAll("customer",
                () -> assertEquals("1", customer.getId()),
                () -> assertEquals("Mr", customer.getTitle()),
                () -> assertEquals("John Doe", customer.getName()),
                () -> assertEquals(LocalDate.of(1990, 1, 1), customer.getDob()),
                () -> assertEquals("Engineer", customer.getOccupation()),
                () -> assertEquals("NZ", customer.getCitizenship()),
                () -> assertEquals("Work", customer.getVisaType()),
                () -> assertEquals("Note", customer.getNotes()));
    }

    @Test
    public void loadNonExistingCustomerFromDatabase() throws Exception {
        DataOperationsTestsHelper.createTestingDatabases();

        assertThrows(Exception.class, () -> {
            CustomerLoader.loadFromDatabase("2");
        });
    }
}