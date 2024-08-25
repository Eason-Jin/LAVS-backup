package uoa.lavs.dataoperations.customer;

import org.junit.jupiter.api.Test;

import uoa.lavs.dataoperations.DataOperationsTestsHelper;
import uoa.lavs.models.Address;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;

public class AddressFinderTests {

    @Test
    public void findExsistingAddressFromDatabase() throws Exception {
        DataOperationsTestsHelper.createTestingDatabases();

        List<Address> addresses = AddressFinder.findFromDatabase("1");

        // Assert
        assertAll("address",
                () -> assertEquals("1", addresses.get(0).getCustomerId()),
                () -> assertEquals(1, addresses.get(0).getNumber()),
                () -> assertEquals("35 Owens Road", addresses.get(0).getLine1()),
                () -> assertEquals("Mt Eden", addresses.get(0).getSuburb()),
                () -> assertEquals("Auckland", addresses.get(0).getCity()),
                () -> assertEquals("1234", addresses.get(0).getPostCode()),
                () -> assertEquals("New Zealand", addresses.get(0).getCountry()),
                () -> assertEquals(true, addresses.get(0).getIsPrimary()),
                () -> assertEquals(true, addresses.get(0).getIsMailing()));
    }

    @Test
    public void findNonExistingAddressFromDatabase() throws Exception {
        DataOperationsTestsHelper.createTestingDatabases();

        assertThrows(Exception.class, () -> {
            AddressFinder.findFromDatabase("2");
        });
    }

    @Test
    public void findExistingAddressFromMainframe() throws Exception {
        DataOperationsTestsHelper.createTestingDatabases();

        List<Address> addresses = AddressFinder.findFromMainframe("123");

        // Assert
        assertAll("address",
                () -> assertEquals("123", addresses.get(0).getCustomerId()),
                () -> assertEquals(1, addresses.get(0).getNumber()),
                () -> assertEquals("Home", addresses.get(0).getType()),
                () -> assertEquals("5 Somewhere Lane", addresses.get(0).getLine1()),
                () -> assertEquals("Nowhere", addresses.get(0).getLine2()),
                () -> assertEquals("Important", addresses.get(0).getSuburb()),
                () -> assertEquals("Auckland", addresses.get(0).getCity()),
                () -> assertEquals("1234", addresses.get(0).getPostCode()),
                () -> assertEquals("New Zealand", addresses.get(0).getCountry()),
                () -> assertEquals(true, addresses.get(0).getIsPrimary()));
    }

    @Test
    public void findNonExistingAddressFromMainframe() throws Exception {
        DataOperationsTestsHelper.createTestingDatabases();
        String customerId = "654";

        assertThrows(Exception.class, () -> {
            AddressFinder.findFromMainframe(customerId);
        });
    }

    @Test
    public void findAddressFromMainframeWithInvalidCustomerId() throws Exception {
        DataOperationsTestsHelper.createTestingDatabases();
        String customerId = "invalid-id";

        assertThrows(Exception.class, () -> {
            AddressFinder.findFromMainframe(customerId);
        });
    }

    @Test
    public void findExistingAddress() throws Exception {
        DataOperationsTestsHelper.createTestingDatabases();
        String customerId = "456";

        List<Address> addresses = AddressFinder.findData(customerId);

        // Assert
        assertAll("address",
                () -> assertEquals("456", addresses.get(0).getCustomerId()),
                () -> assertEquals(1, addresses.get(0).getNumber()),
                () -> assertEquals("Home", addresses.get(0).getType()),
                () -> assertEquals("5 Somewhere Lane", addresses.get(0).getLine1()),
                () -> assertEquals("Nowhere", addresses.get(0).getLine2()),
                () -> assertEquals("Important", addresses.get(0).getSuburb()),
                () -> assertEquals("Auckland", addresses.get(0).getCity()),
                () -> assertEquals("1234", addresses.get(0).getPostCode()),
                () -> assertEquals("New Zealand", addresses.get(0).getCountry()),
                () -> assertEquals(true, addresses.get(0).getIsPrimary()));
    }

    @Test
    public void findNonExistingAddress() throws Exception {
        DataOperationsTestsHelper.createTestingDatabases();
        String customerId = "2";

        List<Address> addresses = AddressFinder.findData(customerId);
        assertEquals(0, addresses.size());
    }

    @Test
    public void findAddressOnlyExistingInDatabase() throws Exception {
        DataOperationsTestsHelper.createTestingDatabases();
        String customerId = "1";

        List<Address> addresses = AddressFinder.findData(customerId);

        assertAll(
                () -> assertEquals("1", addresses.get(0).getCustomerId()),
                () -> assertEquals(1, addresses.get(0).getNumber()),
                () -> assertEquals("35 Owens Road", addresses.get(0).getLine1()),
                () -> assertEquals("Mt Eden", addresses.get(0).getSuburb()),
                () -> assertEquals("Auckland", addresses.get(0).getCity()),
                () -> assertEquals("1234", addresses.get(0).getPostCode()),
                () -> assertEquals("New Zealand", addresses.get(0).getCountry()),
                () -> assertEquals(true, addresses.get(0).getIsPrimary()),
                () -> assertEquals(true, addresses.get(0).getIsMailing()));
    }
}