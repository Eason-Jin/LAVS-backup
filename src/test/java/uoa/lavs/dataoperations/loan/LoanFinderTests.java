package uoa.lavs.dataoperations.loan;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import org.junit.jupiter.api.Test;
import uoa.lavs.dataoperations.DataOperationsTestsHelper;
import uoa.lavs.models.Loan;

public class LoanFinderTests {

  @Test
  public void findExistingLoanFromDatabase() throws Exception {
    DataOperationsTestsHelper.createTestingDatabases();
    String customerId = "1";

    List<Loan> loans = LoanFinder.findFromDatabase(customerId);

    assertAll(
        () -> assertEquals("1", loans.get(0).getCustomerId()),
        () -> assertEquals("1-01", loans.get(0).getLoanId()));
  }

  @Test
  public void findNonExistingLoanFromDatabase() throws Exception {
    DataOperationsTestsHelper.createTestingDatabases();
    String customerId = "invalid-id";
    List<Loan> loans = LoanFinder.findFromDatabase(customerId);

    assertTrue(loans.isEmpty());
  }

  @Test
  public void findExistingLoanFromMainframe() throws Exception {
    DataOperationsTestsHelper.createTestingDatabases();
    String customerId = "123";

    List<Loan> loans = LoanFinder.findData(customerId);

    assertAll(
        () -> assertEquals("123", loans.get(0).getCustomerId()),
        () -> assertEquals("123-09", loans.get(0).getLoanId()));
  }

  @Test
  public void findNonExistingLoanFromMainframe() throws Exception {
    DataOperationsTestsHelper.createTestingDatabases();
    String customerId = "invalid-id";

    assertThrows(
        Exception.class,
        () -> {
          LoanFinder.findFromMainframe(customerId);
        });
  }

  @Test
  public void findExistingLoan() throws Exception {
    DataOperationsTestsHelper.createTestingDatabases();
    String customerId = "123";

    List<Loan> loans = LoanFinder.findData(customerId);

    assertAll(
        () -> assertEquals("123", loans.get(0).getCustomerId()),
        () -> assertEquals("123-09", loans.get(0).getLoanId()));
  }

  @Test
  public void findNonExistingLoan() throws Exception {
    DataOperationsTestsHelper.createTestingDatabases();
    String customerId = "3";

    List<Loan> phones = LoanFinder.findData(customerId);
    assertTrue(phones.isEmpty());
  }
}
