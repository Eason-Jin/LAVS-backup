package uoa.lavs.dataoperations.customer;

import org.junit.jupiter.api.Test;

import uoa.lavs.dataoperations.DataOperationsTestsHelper;
import uoa.lavs.models.Customer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDate;
import java.util.List;

public class CustomerFinderTests {

    @Test
    public void findCustomerFromDatabase() throws Exception {
        DataOperationsTestsHelper.createTestingDatabases();
        DataOperationsTestsHelper.insertCustomer();

        List<Customer> customers = CustomerFinder.findFromDatabaseByName("John");

        // Assert
        assertEquals("1", customers.get(0).getId());
        assertEquals("John Doe", customers.get(0).getName());
        assertEquals(LocalDate.of(1990, 1, 1), customers.get(0).getDob());
    }

    @Test
    public void findCustomerNotInDatabase() throws Exception {
        DataOperationsTestsHelper.createTestingDatabases();

        assertThrows(Exception.class, () -> {
            CustomerFinder.findFromDatabaseByName("John");
        });
    }
}
