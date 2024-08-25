package uoa.lavs.dataoperations.customer;

import org.junit.jupiter.api.Test;

import uoa.lavs.dataoperations.DataOperationsTestsHelper;
import uoa.lavs.models.Email;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

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
}