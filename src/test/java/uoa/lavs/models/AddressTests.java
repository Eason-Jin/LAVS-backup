package uoa.lavs.models;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

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

    @Test
    public void getListRepresentation() {
        Address address = new Address(
            "cust123",
            "Home",
            "123 Main St",
            "Apt 4B",
            "Suburbia",
            "Metropolis",
            "12345",
            "Countryland",
            true,
            false,
            1
        );

        List<String> expected = List.of(
            "Home",
            "123 Main St",
            "Apt 4B",
            "Suburbia",
            "Metropolis",
            "12345",
            "Countryland",
            "true",
            "false"
        );

        List<String> actual = address.getListRepresentation();
        assertEquals(expected, actual);
    }
}