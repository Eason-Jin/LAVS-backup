package uoa.lavs.dataoperations.customer;

import org.junit.jupiter.api.Test;

import uoa.lavs.dataoperations.DataOperationsTestsHelper;
import uoa.lavs.models.Email;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

public class EmailFinderTests {

    @Test
    public void findExistingEmailFromDatabase() throws Exception {
        DataOperationsTestsHelper.createTestingDatabases();

        List<Email> emails = EmailFinder.findFromDatabase("1");

        // Assert
        assertAll("email",
                () -> assertEquals("1", emails.get(0).getCustomerId()),
                () -> assertEquals(1, emails.get(0).getNumber()),
                () -> assertEquals("john@gmail.com", emails.get(0).getAddress()));
    }

    @Test
    public void findNonExistingEmailFromDatabase() throws Exception {
        DataOperationsTestsHelper.createTestingDatabases();

        assertThrows(Exception.class, () -> {
            EmailFinder.findFromDatabase("2");
        });
    }

    @Test
    public void findExistingEmailFromMainframe() throws Exception {
        DataOperationsTestsHelper.createTestingDatabases();

        List<Email> emails = EmailFinder.findFromMainframe("123");

        // Assert
        assertAll("email",
                () -> assertEquals("123", emails.get(0).getCustomerId()),
                () -> assertEquals(1, emails.get(0).getNumber()),
                () -> assertEquals("noone@nowhere.co.no", emails.get(0).getAddress()),
                () -> assertEquals(true, emails.get(0).getIsPrimary()));
    }

    @Test
    public void findNoneExistinEmailFromMainframe() throws Exception {
        DataOperationsTestsHelper.createTestingDatabases();
        String customerId = "654";

        assertThrows(Exception.class, () -> {
            EmailFinder.findFromMainframe(customerId);
        });
    }

    @Test
    public void findEmailFromMainframeWithInvalidCustomerId() throws Exception {
        DataOperationsTestsHelper.createTestingDatabases();
        String customerId = "invalid-id";

        assertThrows(Exception.class, () -> {
            EmailFinder.findFromMainframe(customerId);
        });
    }

    @Test
    public void findExistingEmail() throws Exception {
        DataOperationsTestsHelper.createTestingDatabases();
        String customerId = "123";

        List<Email> emails = EmailFinder.findData(customerId);

        assertAll(() -> assertEquals("123", emails.get(0).getCustomerId()));
    }

    @Test
    public void findNonExistingEmail() throws Exception {
        DataOperationsTestsHelper.createTestingDatabases();
        String customerId = "2";

        List<Email> emails = EmailFinder.findData(customerId);
        assertTrue(emails.isEmpty());
    }

    @Test
    public void findEmailOnlyExistingInDatabase() throws Exception {
        DataOperationsTestsHelper.createTestingDatabases();
        String customerId = "1";

        List<Email> emails = EmailFinder.findData(customerId);

        assertAll(
                () -> assertEquals("1", emails.get(0).getCustomerId()),
                () -> assertEquals(1, emails.get(0).getNumber()));
    }
}