package uoa.lavs.models;

import static org.junit.jupiter.api.Assertions.assertEquals;

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
}
