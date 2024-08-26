package uoa.lavs.dataoperations.loan;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;
import uoa.lavs.dataoperations.DataOperationsTestsHelper;
import uoa.lavs.models.Loan;

public class LoanLoaderTests {

  @Test
  public void loadExistingLoanFromDatabase() throws Exception {
    DataOperationsTestsHelper.createTestingDatabasesForLoans();
    String loanId = "1-01";

    Loan loan = LoanLoader.loadFromDatabase(loanId);

    assertAll(
        () -> assertEquals("1", loan.getCustomerId()),
        () -> assertEquals("1-01", loan.getLoanId()));
  }

  @Test
  public void loadNonExistingLoanFromDatabase() {
    DataOperationsTestsHelper.createTestingDatabasesForLoans();
    String loanId = "invalid-id";

    assertThrows(
        Exception.class,
        () -> {
          LoanLoader.loadFromDatabase(loanId);
        });
  }

  @Test
  public void loadExistingLoanFromMainframe() throws Exception {
    DataOperationsTestsHelper.createTestingDatabasesForLoans();
    String loanId = "123-09";

    Loan loan = LoanLoader.loadFromMainframe(loanId);

    assertAll(
        () -> assertEquals("123", loan.getCustomerId()),
        () -> assertEquals("123-09", loan.getLoanId()));
  }

  @Test
  public void loadNonExistingLoanFromMainframe() {
    DataOperationsTestsHelper.createTestingDatabasesForLoans();
    String loanId = "invalid-id";

    assertThrows(
        Exception.class,
        () -> {
          LoanLoader.loadFromMainframe(loanId);
        });
  }

  @Test
  public void loadExistingDatabaseLoan() {
    DataOperationsTestsHelper.createTestingDatabasesForLoans();
    String loanId = "1-01";

    Loan loan = LoanLoader.loadData(loanId);

    assertAll(
        () -> assertEquals("1", loan.getCustomerId()),
        () -> assertEquals("1-01", loan.getLoanId()));
  }

  @Test
  public void loadExistingMainframeLoan() {
    DataOperationsTestsHelper.createTestingDatabasesForLoans();
    String loanId = "123-09";

    Loan loan = LoanLoader.loadData(loanId);

    assertAll(
        () -> assertEquals("123", loan.getCustomerId()),
        () -> assertEquals("123-09", loan.getLoanId()));
  }

  @Test
  public void loadNonExistingLoan() {
    DataOperationsTestsHelper.createTestingDatabasesForLoans();
    String loanId = "invalid-id";

    Loan loan = LoanLoader.loadData(loanId);

    assertAll(
        () -> assertEquals(null, loan.getCustomerId()), () -> assertEquals(null, loan.getLoanId()));
  }
}
