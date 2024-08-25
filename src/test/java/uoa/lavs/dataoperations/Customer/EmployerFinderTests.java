package uoa.lavs.dataoperations.customer;

import org.junit.jupiter.api.Test;

import uoa.lavs.dataoperations.DataOperationsTestsHelper;
import uoa.lavs.models.Employer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;

public class EmployerFinderTests {

    @Test
    public void findEmployerFromDatabase() throws Exception {
        DataOperationsTestsHelper.createTestingDatabases();

        List<Employer> employers = EmployerFinder.findFromDatabase("1");

        // Assert
        assertEquals("1", employers.get(0).getCustomerId());
        assertEquals("Tech Corp", employers.get(0).getName());
        assertEquals("123 Tech Street", employers.get(0).getLine1());
        assertEquals("Tech Suburb", employers.get(0).getSuburb());
        assertEquals("Tech City", employers.get(0).getCity());
        assertEquals("1234", employers.get(0).getPostCode());
        assertEquals("Tech Country", employers.get(0).getCountry());
        assertEquals("0934534345", employers.get(0).getPhoneNumber());
        assertEquals("techcorp@tech.com", employers.get(0).getEmailAddress());
        assertEquals("www.techcorp.com", employers.get(0).getWebsite());
        assertEquals(true, employers.get(0).getIsOwner());
    }

    @Test
    public void findEmployerNotInDatabase() throws Exception {
        DataOperationsTestsHelper.createTestingDatabases();

        assertThrows(Exception.class, () -> {
            EmployerFinder.findFromDatabase("2");
        });
    }
}