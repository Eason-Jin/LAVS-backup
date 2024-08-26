package uoa.lavs.dataoperations.loan;

import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;
import org.junit.jupiter.api.Test;
import uoa.lavs.dataoperations.DataOperationsTestsHelper;
import uoa.lavs.models.Loan;
import uoa.lavs.utility.LoanRepayment;

public class LoanRepaymentTests {

  @Test
  public void testCalculateLoanRepaymentLocally() throws Exception {
    LoanPaymentsLoader loanPaymentsLoader = new LoanPaymentsLoader();
    DataOperationsTestsHelper.createTestingDatabasesForLoans();
    String loanId = "1-01";
    List<LoanRepayment> loanRepayments = LoanPaymentsLoader.calculateLocally(loanId);

    assert loanRepayments.size() > 0;
  }

  @Test
  public void testCalculateLoanRepaymentLocallyFailure() throws Exception {
    DataOperationsTestsHelper.createTestingDatabasesForLoans();
    String loanId = "invalid-id";

    assertThrows(
        Exception.class,
        () -> {
          LoanPaymentsLoader.calculateLocally(loanId);
        });
  }

  @Test
  public void testCalculateLoanRepaymentFromMainframe() throws Exception {
    DataOperationsTestsHelper.createTestingDatabasesForLoans();
    String loanId = "123-09";
    List<LoanRepayment> loanRepayments = LoanPaymentsLoader.calculateFromMainframe(loanId);

    assert loanRepayments.size() > 0;
  }

  @Test
  public void testCalculateLoanRepaymentFromMainframeFailure() {
    DataOperationsTestsHelper.createTestingDatabasesForLoans();
    String loanId = "invalid-id";

    assertThrows(
        Exception.class,
        () -> {
          LoanPaymentsLoader.calculateFromMainframe(loanId);
        });
  }

  @Test
  public void testCalculateLoanRepaymentDatabase() throws Exception {
    DataOperationsTestsHelper.createTestingDatabasesForLoans();
    String loanId = "1-02";
    List<LoanRepayment> loanRepayments = LoanPaymentsLoader.calculateLoanRepayments(loanId);

    assert loanRepayments.size() > 0;
  }

  @Test
  public void testCalculateLoanRepaymentMainframe() throws Exception {
    DataOperationsTestsHelper.createTestingDatabasesForLoans();
    String loanId = "123-09";
    List<LoanRepayment> loanRepayments = LoanPaymentsLoader.calculateLoanRepayments(loanId);

    assert loanRepayments.size() > 0;
  }

  @Test
  public void testCalculateLoanRepaymentFailure() {
    DataOperationsTestsHelper.createTestingDatabasesForLoans();
    String loanId = "invalid-id";
    List<LoanRepayment> loanRepayments = LoanPaymentsLoader.calculateLoanRepayments(loanId);

    assert loanRepayments.size() == 0;
  }
}
