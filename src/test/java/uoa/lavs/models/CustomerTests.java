package uoa.lavs.models;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;

public class CustomerTests {

    @Test
    public void testEqualsSameObject() {
        Customer customer = new Customer(
                "cust123",
                "Mr.",
                "John Doe",
                LocalDate.of(1990, 1, 1),
                "Engineer",
                "USA",
                "H1B",
                "Active",
                "Notes");
        assertTrue(customer.equals(customer));
    }

    @Test
    public void testEqualsDifferentObjectSameId() {
        Customer customer1 = new Customer(
                "cust123",
                "Mr.",
                "John Doe",
                LocalDate.of(1990, 1, 1),
                "Engineer",
                "USA",
                "H1B",
                "Active",
                "Notes");
        Customer customer2 = new Customer(
                "cust123",
                "Ms.",
                "Jane Doe",
                LocalDate.of(1992, 2, 2),
                "Doctor",
                "Canada",
                "Work Permit",
                "Inactive",
                "Different Notes");
        assertTrue(customer1.equals(customer2));
    }

    @Test
    public void testEqualsNullObject() {
        Customer customer = new Customer(
                "cust123",
                "Mr.",
                "John Doe",
                LocalDate.of(1990, 1, 1),
                "Engineer",
                "USA",
                "H1B",
                "Active",
                "Notes");
        assertFalse(customer.equals(null));
    }
}