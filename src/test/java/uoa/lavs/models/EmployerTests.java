package uoa.lavs.models;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.junit.jupiter.api.Test;

public class EmployerTests {

    @Test
    public void getStreetAddress() {
        Employer employer = new Employer();
        employer.setLine1("123 Main St");
        employer.setLine2("Apt 4B");
        String expected = "123 Main St Apt 4B";
        String actual = employer.getStreetAddress();
        assertEquals(expected, actual);
    }

    @Test
    public void getListRepresentation() {
        Employer employer = new Employer(
                "cust123",
                "Tech Corp",
                "123 Tech St",
                "Suite 100",
                "Tech Suburb",
                "Tech City",
                "12345",
                "Tech Country",
                "123-456-7890",
                "contact@techcorp.com",
                "www.techcorp.com",
                true,
                1);

        List<String> expected = List.of(
                "Tech Corp",
                "123 Tech St",
                "Suite 100",
                "Tech Suburb",
                "Tech City",
                "12345",
                "Tech Country",
                "123-456-7890",
                "contact@techcorp.com",
                "www.techcorp.com",
                "true");

        List<String> actual = employer.getListRepresentation();
        assertEquals(expected, actual);
    }
}
