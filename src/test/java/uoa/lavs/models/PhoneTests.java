package uoa.lavs.models;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

public class PhoneTests {

    @Test
    public void testGetListRepresentation() {
        Phone phone = new Phone(
            "cust123",
            "Mobile",
            "+1",
            "1234567890",
            true,
            true,
            1
        );

        List<String> expected = List.of(
            "Mobile",
            "+1",
            "1234567890",
            "true",
            "true"
        );

        List<String> actual = phone.getListRepresentation();
        assertEquals(expected, actual);
    }
}