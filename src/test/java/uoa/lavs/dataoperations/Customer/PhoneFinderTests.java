package uoa.lavs.dataoperations.customer;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import uoa.lavs.dataoperations.DataOperationsTestsHelper;
import uoa.lavs.models.Phone;

public class PhoneFinderTests {

  @BeforeEach
  public void setup() {
    DataOperationsTestsHelper.createTestingDatabases();
  }

  @Test
  public void handleExistingPhone() {
    String customerId = "123";

    List<Phone> phones = PhoneFinder.findData(customerId);

    assertAll(() -> assertEquals("123", phones.get(0).getCustomerId()));
  }
}
