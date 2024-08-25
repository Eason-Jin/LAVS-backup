package uoa.lavs.dataoperations.customer;

import org.junit.jupiter.api.Test;

import uoa.lavs.dataoperations.DataOperationsTestsHelper;
import uoa.lavs.models.Customer;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.util.List;

public class CustomerFinderTests {

    @Test
    public void findExistingCustomerFromDatabase() throws Exception {
        DataOperationsTestsHelper.createTestingDatabases();

        List<Customer> customers = CustomerFinder.findFromDatabaseByName("Bob");

        // Assert
        assertAll("customer",
                () -> assertEquals("1", customers.get(0).getId()),
                () -> assertEquals("Bob Black", customers.get(0).getName()),
                () -> assertEquals(LocalDate.of(1990, 1, 1), customers.get(0).getDob()));
    }

    @Test
    public void findNonExistingCustomerFromDatabase() throws Exception {
        DataOperationsTestsHelper.createTestingDatabases();

        assertThrows(Exception.class, () -> {
            CustomerFinder.findFromDatabaseByName("Jane");
        });
    }

    @Test
    public void findExistingCustomerFromMainframe() throws Exception {
        DataOperationsTestsHelper.createTestingDatabases();

        List<Customer> customers = CustomerFinder.findFromMainframeByName("John Doe");

        // Assert
        assertAll("customer",
                () -> assertEquals("123", customers.get(0).getId()),
                () -> assertEquals("John Doe", customers.get(0).getName()),
                () -> assertEquals(LocalDate.of(1945, 3, 12), customers.get(0).getDob()));
    }

    @Test
    public void findNonExistingCustomerFromMainframe() throws Exception {
        DataOperationsTestsHelper.createTestingDatabases();
        String customerName = "Joseph Smith";

        assertThrows(Exception.class, () -> {
            CustomerFinder.findFromMainframeByName(customerName);
        });
    }

    @Test
    public void findExistingCustomer() throws Exception {
        DataOperationsTestsHelper.createTestingDatabases();
        String customerName = "John Doe";

        List<Customer> customers = CustomerFinder.findCustomerByName(customerName);

        // Assert
        assertAll("customer",
                () -> assertEquals("123", customers.get(0).getId()),
                () -> assertEquals("John Doe", customers.get(0).getName()),
                () -> assertEquals(LocalDate.of(1945, 3, 12), customers.get(0).getDob()));
    }

    @Test
    public void findNonExistingCustomer() throws Exception {
        DataOperationsTestsHelper.createTestingDatabases();
        String customerName = "Joseph Smith";

        List<Customer> customers = CustomerFinder.findCustomerByName(customerName);
        assertTrue(customers.isEmpty());
    }

    @Test
    public void findCustomerOnlyExistingInDatabase() throws Exception {
        DataOperationsTestsHelper.createTestingDatabases();
        String customerName = "Bob Black";

        List<Customer> customers = CustomerFinder.findCustomerByName(customerName);

        assertAll(
                () -> assertEquals("1", customers.get(0).getId()),
                () -> assertEquals("Bob Black", customers.get(0).getName()),
                () -> assertEquals(LocalDate.of(1990, 1, 1), customers.get(0).getDob()));
    }

    @Test
    public void findCustomerWithInvalidConnection() {
        DataOperationsTestsHelper.createTestingDatabases();
        String customerName = "";

        assertThrows(Exception.class, () -> {
            CustomerFinder.findFromMainframeByName(customerName);
        });
    }
}