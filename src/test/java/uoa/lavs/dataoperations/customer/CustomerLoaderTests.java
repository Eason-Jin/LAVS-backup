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
                () -> assertEquals("Bob Black", customer.getName()),
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

    @Test
    public void loadExistingCustomerFromMainframe() throws Exception {
        DataOperationsTestsHelper.createTestingDatabases();

        Customer customer = CustomerLoader.loadFromMainframe("123");

        // Assert
        assertAll("customer",
                () -> assertEquals("123", customer.getId()),
                () -> assertEquals("Mr", customer.getTitle()),
                () -> assertEquals("John Doe", customer.getName()),
                () -> assertEquals(LocalDate.of(1945, 3, 12), customer.getDob()),
                () -> assertEquals("Test dummy", customer.getOccupation()),
                () -> assertEquals("New Zealand", customer.getCitizenship()),
                () -> assertEquals("n/a", customer.getVisaType()),
                () -> assertEquals(
                        "Test line #1Test line #2Test line #3Test line #4Test line #5",
                        customer.getNotes()));
    }

    @Test
    public void loadNonExistingCustomerFromMainframe() throws Exception {
        DataOperationsTestsHelper.createTestingDatabases();
        String customerId = "1567";

        assertThrows(Exception.class, () -> {
            CustomerLoader.loadFromMainframe(customerId);
        });
    }

    @Test
    public void loadCustomer() throws Exception {
        DataOperationsTestsHelper.createTestingDatabases();
        String customerId = "654";

        Customer customer = CustomerLoader.loadData(customerId);

        // Assert
        assertAll("customer",
                () -> assertEquals("654", customer.getId()),
                () -> assertEquals("Mr", customer.getTitle()),
                () -> assertEquals("Jane Doe", customer.getName()),
                () -> assertEquals(LocalDate.of(1945, 3, 12), customer.getDob()),
                () -> assertEquals("Test dummy", customer.getOccupation()),
                () -> assertEquals("New Zealand", customer.getCitizenship()),
                () -> assertEquals("n/a", customer.getVisaType()),
                () -> assertEquals(
                        "",
                        customer.getNotes()));
    }

    @Test
    public void loadCustomerOnlyExsistingInDatabase() throws Exception {
        DataOperationsTestsHelper.createTestingDatabases();
        String customerId = "1";

        Customer customer = CustomerLoader.loadData(customerId);

        // Assert
        assertAll("customer",
                () -> assertEquals("1", customer.getId()),
                () -> assertEquals("Mr", customer.getTitle()),
                () -> assertEquals("Bob Black", customer.getName()),
                () -> assertEquals(LocalDate.of(1990, 1, 1), customer.getDob()),
                () -> assertEquals("Engineer", customer.getOccupation()),
                () -> assertEquals("NZ", customer.getCitizenship()),
                () -> assertEquals("Work", customer.getVisaType()),
                () -> assertEquals("Note", customer.getNotes()));
    }

    @Test
    public void loadNonExistingCustomer() throws Exception {
        DataOperationsTestsHelper.createTestingDatabases();
        String customerId = "invalid-id";

        Customer customer = CustomerLoader.loadData(customerId);
        assertAll("customer", () -> assertEquals(null, customer.getId()));
    }
}