package uoa.lavs.dataoperations.loan;

import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;
import org.junit.jupiter.api.Test;
import uoa.lavs.dataoperations.DataOperationsTestsHelper;
import uoa.lavs.utility.LoanRepayment;

public class LoanRepaymentTests {

  @Test
  public void testCalculateLoanRepaymentLocally() throws Exception {
    DataOperationsTestsHelper.createTestingDatabases();
    String loanId = "1-01";
    List<LoanRepayment> loanRepayments = LoanPaymentsLoader.calculateLocally(loanId);

    assert loanRepayments.size() > 0;
  }

  @Test
  public void testCalculateLoanRepaymentLocallyFailure() throws Exception {
    DataOperationsTestsHelper.createTestingDatabases();
    String loanId = "invalid-id";

    assertThrows(
        Exception.class,
        () -> {
          LoanPaymentsLoader.calculateLocally(loanId);
        });
  }

  @Test
  public void testCalculateLoanRepaymentFromMainframe() throws Exception {
    DataOperationsTestsHelper.createTestingDatabases();
    String loanId = "123-09";
    List<LoanRepayment> loanRepayments = LoanPaymentsLoader.calculateFromMainframe(loanId);

    assert loanRepayments.size() > 0;
  }

  @Test
  public void testCalculateLoanRepaymentFromMainframeFailure() {
    DataOperationsTestsHelper.createTestingDatabases();
    String loanId = "invalid-id";

    assertThrows(
        Exception.class,
        () -> {
          LoanPaymentsLoader.calculateFromMainframe(loanId);
        });
  }

  @Test
  public void testCalculateLoanRepaymentDatabase() throws Exception {
    DataOperationsTestsHelper.createTestingDatabases();
    String loanId = "1-01";
    List<LoanRepayment> loanRepayments = LoanPaymentsLoader.calculateLoanRepayments(loanId);

    assert loanRepayments.size() > 0;
  }

  @Test
  public void testCalculateLoanRepaymentMainframe() throws Exception {
    DataOperationsTestsHelper.createTestingDatabases();
    String loanId = "123-09";
    List<LoanRepayment> loanRepayments = LoanPaymentsLoader.calculateLoanRepayments(loanId);

    assert loanRepayments.size() > 0;
  }

  @Test
  public void testCalculateLoanRepaymentFailure() {
    DataOperationsTestsHelper.createTestingDatabases();
    String loanId = "invalid-id";
    List<LoanRepayment> loanRepayments = LoanPaymentsLoader.calculateLoanRepayments(loanId);

    assert loanRepayments.size() == 0;
  }
}
