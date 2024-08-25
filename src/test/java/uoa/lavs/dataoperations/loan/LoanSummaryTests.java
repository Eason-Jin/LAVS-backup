package uoa.lavs.dataoperations.loan;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;
import uoa.lavs.dataoperations.DataOperationsTestsHelper;
import uoa.lavs.utility.LoanSummary;

public class LoanSummaryTests {

  @Test
  public void testCalculateLoanSummaryLocally() throws Exception {
    DataOperationsTestsHelper.createTestingDatabases();
    String loanId = "1-01";

    LoanSummary loanSummary = LoanSummaryLoader.calculateLocally(loanId);

    assertAll(() -> assertEquals(1000.47, loanSummary.getTotalCost()));
  }

  @Test
  public void testCalculateLoanSummaryFromMainframeSuccess() throws Exception {
    DataOperationsTestsHelper.createTestingDatabases();
    String loanId = "123-09";

    LoanSummary loanSummary = LoanSummaryLoader.calculateFromMainframe(loanId);

    assertAll(() -> assertEquals(10635.55, loanSummary.getTotalCost()));
  }

  @Test
  public void testCalculateLoanSummaryFromMainframeFailure() {
    DataOperationsTestsHelper.createTestingDatabases();
    String loanId = "invalid-id";

    assertThrows(
        Exception.class,
        () -> {
          LoanSummaryLoader.calculateFromMainframe(loanId);
        });
  }

  @Test
  public void testCalculateLoanSummarySuccessDatabase() {
    DataOperationsTestsHelper.createTestingDatabases();
    String loanId = "1-01";

    LoanSummary loanSummary = LoanSummaryLoader.calculateLoanSummary(loanId);

    assertAll(() -> assertEquals(1000.47, loanSummary.getTotalCost()));
  }

  @Test
  public void testCalculateLoanSummarySuccessMainframe() {
    DataOperationsTestsHelper.createTestingDatabases();
    String loanId = "123-09";

    LoanSummary loanSummary = LoanSummaryLoader.calculateLoanSummary(loanId);

    assertAll(() -> assertEquals(10635.55, loanSummary.getTotalCost()));
  }

  @Test
  public void testCalculateLoanSummaryFailure() {
    DataOperationsTestsHelper.createTestingDatabases();
    String loanId = "invalid-id";

    LoanSummary loanSummary = LoanSummaryLoader.calculateLoanSummary(loanId);

    assertAll(() -> assertEquals(0.0, loanSummary.getTotalCost()));
  }
}
