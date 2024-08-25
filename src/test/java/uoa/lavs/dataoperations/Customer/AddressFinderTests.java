package uoa.lavs.dataoperations.customer;

import org.junit.jupiter.api.Test;

import uoa.lavs.dataoperations.DataOperationsTestsHelper;
import uoa.lavs.models.Address;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;

public class AddressFinderTests {

    @Test
    public void findAddressFromDatabase() throws Exception {
        DataOperationsTestsHelper.createTestingDatabases();

        List<Address> addresses = AddressFinder.findFromDatabase("1");

        // Assert
        assertEquals("1", addresses.get(0).getCustomerId());
        assertEquals(1, addresses.get(0).getNumber());
        assertEquals("35 Owens Road", addresses.get(0).getLine1());
        assertEquals("Mt Eden", addresses.get(0).getSuburb());
        assertEquals("Auckland", addresses.get(0).getCity());
        assertEquals("1234", addresses.get(0).getPostCode());
        assertEquals("New Zealand", addresses.get(0).getCountry());
        assertEquals(true, addresses.get(0).getIsPrimary());
        assertEquals(true, addresses.get(0).getIsMailing());
    }

    @Test
    public void findAddressNotInDatabase() throws Exception {
        DataOperationsTestsHelper.createTestingDatabases();

        assertThrows(Exception.class, () -> {
            AddressFinder.findFromDatabase("2");
        });
    }
}