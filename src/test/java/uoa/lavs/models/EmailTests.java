package uoa.lavs.models;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

public class EmailTests {

    @Test
    public void getListRepresentation() {
        Email email = new Email("cust123", "test@example.com", true, 1);

        List<String> expected = List.of(
            "test@example.com",
            "true"
        );

        List<String> actual = email.getListRepresentation();
        assertEquals(expected, actual);
    }
}