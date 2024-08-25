package uoa.lavs.dataoperations.customer;

import org.junit.jupiter.api.Test;

import uoa.lavs.dataoperations.DataOperationsTestsHelper;
import uoa.lavs.models.Employer;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

public class EmployerFinderTests {

    @Test
    public void findExistingEmployerFromDatabase() throws Exception {
        DataOperationsTestsHelper.createTestingDatabases();

        List<Employer> employers = EmployerFinder.findFromDatabase("1");

        // Assert
        assertAll("employer",
                () -> assertEquals("1", employers.get(0).getCustomerId()),
                () -> assertEquals("Tech Corp", employers.get(0).getName()),
                () -> assertEquals("123 Tech Street", employers.get(0).getLine1()),
                () -> assertEquals("Tech Suburb", employers.get(0).getSuburb()),
                () -> assertEquals("Tech City", employers.get(0).getCity()),
                () -> assertEquals("1234", employers.get(0).getPostCode()),
                () -> assertEquals("Tech Country", employers.get(0).getCountry()),
                () -> assertEquals("0934534345", employers.get(0).getPhoneNumber()),
                () -> assertEquals("techcorp@tech.com", employers.get(0).getEmailAddress()),
                () -> assertEquals("www.techcorp.com", employers.get(0).getWebsite()),
                () -> assertEquals(true, employers.get(0).getIsOwner()));
    }

    @Test
    public void findNonExistingEmployerFromDatabase() throws Exception {
        DataOperationsTestsHelper.createTestingDatabases();

        assertThrows(Exception.class, () -> {
            EmployerFinder.findFromDatabase("2");
        });
    }

    @Test
    public void findExistingEmployerFromMainframe() throws Exception {
        DataOperationsTestsHelper.createTestingDatabases();

        List<Employer> employers = EmployerFinder.findFromMainframe("123");

        // Assert
        assertAll("employer",
                () -> assertEquals("123", employers.get(0).getCustomerId()),
                () -> assertEquals("The Best Employer", employers.get(0).getName()),
                () -> assertEquals("5 Somewhere Lane", employers.get(0).getLine1()),
                () -> assertEquals("Nowhere", employers.get(0).getLine2()),
                () -> assertEquals("Important", employers.get(0).getSuburb()),
                () -> assertEquals("Auckland", employers.get(0).getCity()),
                () -> assertEquals("1234", employers.get(0).getPostCode()),
                () -> assertEquals("New Zealand", employers.get(0).getCountry()),
                () -> assertEquals("bigboss@nowhere.co.no", employers.get(0).getEmailAddress()),
                () -> assertEquals("+99-123-9876", employers.get(0).getPhoneNumber()),
                () -> assertEquals("http://nowhere.co.no", employers.get(0).getWebsite()),
                () -> assertEquals(true, employers.get(0).getIsOwner()));
    }

    @Test
    public void findEmployerFromMainframeWithInvalidCustomerId() throws Exception {
        DataOperationsTestsHelper.createTestingDatabases();
        String customerId = "invalid-id";

        assertThrows(Exception.class, () -> {
            EmployerFinder.findFromMainframe(customerId);
        });
    }

    @Test
    public void findExistingEmployer() throws Exception {
        DataOperationsTestsHelper.createTestingDatabases();
        String customerId = "456";

        List<Employer> employers = EmployerFinder.findData(customerId);

        // Assert
        assertAll("employer",
                () -> assertEquals("456", employers.get(0).getCustomerId()),
                () -> assertEquals("The Best Employer", employers.get(0).getName()),
                () -> assertEquals("5 Somewhere Lane", employers.get(0).getLine1()),
                () -> assertEquals("Nowhere", employers.get(0).getLine2()),
                () -> assertEquals("Important", employers.get(0).getSuburb()),
                () -> assertEquals("Auckland", employers.get(0).getCity()),
                () -> assertEquals("1234", employers.get(0).getPostCode()),
                () -> assertEquals("New Zealand", employers.get(0).getCountry()),
                () -> assertEquals("bigboss@nowhere.co.no", employers.get(0).getEmailAddress()),
                () -> assertEquals("+99-123-9876", employers.get(0).getPhoneNumber()),
                () -> assertEquals("http://nowhere.co.no", employers.get(0).getWebsite()),
                () -> assertEquals(true, employers.get(0).getIsOwner()));
    }

    @Test
    public void findNonExistingEmployer() throws Exception {
        DataOperationsTestsHelper.createTestingDatabases();
        String customerId = "789";

        List<Employer> employers = EmployerFinder.findData(customerId);
        assertTrue(employers.isEmpty());
    }

    @Test
    public void findEmployerOnlyExistingInDatabase() throws Exception {
        DataOperationsTestsHelper.createTestingDatabases();
        String customerId = "1";

        List<Employer> employers = EmployerFinder.findData(customerId);

        // Assert
        assertAll("employer",
                () -> assertEquals("1", employers.get(0).getCustomerId()),
                () -> assertEquals("Tech Corp", employers.get(0).getName()));
    }
}