package uoa.lavs.models;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class PendingUpdateRowTests {

    @Test
    public void testConstructorAndGetters() {
        PendingUpdateRow pendingUpdateRow = new PendingUpdateRow(
            "cust123",
            true,
            false,
            true,
            false,
            true,
            false,
            true
        );

        assertEquals("cust123", pendingUpdateRow.getCustomerId());
        assertTrue(pendingUpdateRow.getGeneralDetails());
        assertFalse(pendingUpdateRow.getAddress());
        assertTrue(pendingUpdateRow.getEmail());
        assertFalse(pendingUpdateRow.getPhoneNumber());
        assertTrue(pendingUpdateRow.getEmployer());
        assertFalse(pendingUpdateRow.getLoan());
        assertTrue(pendingUpdateRow.getCoborrower());
    }
}