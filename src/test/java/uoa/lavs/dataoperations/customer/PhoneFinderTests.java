package uoa.lavs.dataoperations.customer;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import org.junit.jupiter.api.Test;
import uoa.lavs.dataoperations.DataOperationsTestsHelper;
import uoa.lavs.models.Phone;

public class PhoneFinderTests {

  @Test
  public void findExistingPhoneFromDatabase() throws Exception {
    DataOperationsTestsHelper.createTestingDatabases();
    String customerId = "1";

    PhoneFinder phoneFinder = new PhoneFinder();
    List<Phone> phones = PhoneFinder.findFromDatabase(customerId);

    assertAll(
        () -> assertEquals("1", phones.get(0).getCustomerId()),
        () -> assertEquals("123456789", phones.get(0).getPhoneNumber()));
  }

  @Test
  public void findNonExistingPhoneFromDatabase() throws Exception {
    DataOperationsTestsHelper.createTestingDatabases();
    String customerId = "2";

    assertThrows(Exception.class, () -> {
      PhoneFinder.findFromDatabase(customerId);
    });
  }

  @Test
  public void findExistingPhoneFromMainframe() throws Exception {
    DataOperationsTestsHelper.createTestingDatabases();
    String customerId = "123";

    List<Phone> phones = PhoneFinder.findFromMainframe(customerId);

    assertAll(() -> assertEquals("123", phones.get(0).getCustomerId()));
  }

  @Test
  public void findNonExistingPhoneFromMainframe() throws Exception {
    DataOperationsTestsHelper.createTestingDatabases();
    String customerId = "654";

    assertThrows(Exception.class, () -> {
      PhoneFinder.findFromMainframe(customerId);
    });
  }

  @Test
  public void findPhoneFromMainframeWithInvalidCustomerId() throws Exception {
    DataOperationsTestsHelper.createTestingDatabases();
    String customerId = "invalid-id";

    assertThrows(Exception.class, () -> {
      PhoneFinder.findFromMainframe(customerId);
    });
  }

  @Test
  public void findExistingPhone() throws Exception {
    DataOperationsTestsHelper.createTestingDatabases();
    String customerId = "123";

    List<Phone> phones = PhoneFinder.findData(customerId);

    assertAll(
        () -> assertEquals("123", phones.get(0).getCustomerId()),
        () -> assertEquals("123-4567", phones.get(0).getPhoneNumber()),
        () -> assertEquals("+99", phones.get(0).getPrefix()),
        () -> assertEquals(true, phones.get(0).getIsPrimary()));
  }

  @Test
  public void findNonExistingPhone() {
    DataOperationsTestsHelper.createTestingDatabases();
    String customerId = "3";

    List<Phone> phones = PhoneFinder.findData(customerId);
    assertTrue(phones.isEmpty());
  }

  @Test
  public void findPhoneOnlyExistingInDatabase() {
    DataOperationsTestsHelper.createTestingDatabases();
    String customerId = "1";

    List<Phone> phones = PhoneFinder.findData(customerId);

    assertAll(
        () -> assertEquals("1", phones.get(0).getCustomerId()),
        () -> assertEquals("123456789", phones.get(0).getPhoneNumber()));
  }
}
