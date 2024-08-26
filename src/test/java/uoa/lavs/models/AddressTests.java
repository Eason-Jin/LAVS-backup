package uoa.lavs.models;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class AddressTests {

    @Test
    public void getStreetAddress() {
        Address address = new Address();
        address.setLine1("123 Main St");
        address.setLine2("Apt 4B");
        String expected = "123 Main St Apt 4B";
        String actual = address.getStreetAddress();
        assertEquals(expected, actual);
    }
}