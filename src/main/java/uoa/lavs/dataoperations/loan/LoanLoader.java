package uoa.lavs.dataoperations.loan;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;
import uoa.lavs.mainframe.Frequency;
import uoa.lavs.mainframe.Instance;
import uoa.lavs.mainframe.RateType;
import uoa.lavs.mainframe.Status;
import uoa.lavs.mainframe.messages.loan.LoadLoan;
import uoa.lavs.models.Loan;

public class LoanLoader {

  public static Loan loadData(String loanId) {
    Loan loan = new Loan();
    try {
      loan = loadFromMainframe(loanId);
      if (loan.getCustomerName() == null) {
        throw new Exception("Loan not in mainframe");
      }
    } catch (Exception e) {
      System.out.println("Mainframe load failed: " + e.getMessage());
      System.out.println("Trying to load from database");
      try {
        loan = loadFromDatabase(loanId);
      } catch (Exception e1) {
        System.out.println("Database load failed: " + e1.getMessage());
      }
    }
    return loan;
  }

  private static Loan loadFromDatabase(String loanId) throws Exception {
    Loan loan = new Loan();
    String query = "SELECT * FROM Loan WHERE LoanID = ?";
    try (Connection connection = Instance.getDatabaseConnection();
        PreparedStatement statement = connection.prepareStatement(query)) {
      statement.setString(1, loanId);
      try (ResultSet resultSet = statement.executeQuery()) {
        if (!resultSet.next()) {
          throw new Exception("Loan not in database");
        }
        loan.setLoanId(resultSet.getString("LoanID"));
        loan.setCustomerName(resultSet.getString("CustomerName"));
        loan.setStatus(resultSet.getString("Status"));
        loan.setPrincipal(resultSet.getDouble("Principal"));
        loan.setRateValue(resultSet.getDouble("RateValue"));
        loan.setRateType(RateType.valueOf(resultSet.getString("RateType")));
        loan.setStartDate(resultSet.getObject("StartDate", LocalDate.class));
        loan.setPeriod(resultSet.getInt("Period"));
        loan.setTerm(resultSet.getInt("Term"));
        loan.setPaymentAmount(resultSet.getDouble("PaymentAmount"));
        loan.setPaymentFrequency(Frequency.valueOf(resultSet.getString("PaymentFrequency")));
        loan.setCompounding(Frequency.valueOf(resultSet.getString("Compounding")));
        loan.setCustomerId(resultSet.getString("CustomerID"));
      }
    }
    return loan;
  }

  private static Loan loadFromMainframe(String loanId) throws Exception {
    LoadLoan loadLoan = new LoadLoan();
    loadLoan.setLoanId(loanId);
    Status status = loadLoan.send(Instance.getConnection());
    if (!status.getWasSuccessful()) {
      System.out.println(
          "Something went wrong - the Mainframe send failed! The code is " + status.getErrorCode());
      throw new Exception("Mainframe send failed");
    }

    Loan loan = new Loan();
    loan.setLoanId(loanId);
    loan.setCustomerName(loadLoan.getCustomerNameFromServer());
    loan.setStatus(loadLoan.getStatusFromServer());
    loan.setPrincipal(loadLoan.getPrincipalFromServer());
    loan.setRateValue(loadLoan.getRateValueFromServer());
    loan.setRateType(loadLoan.getRateTypeFromServer());
    loan.setStartDate(loadLoan.getStartDateFromServer());
    loan.setPeriod(loadLoan.getPeriodFromServer());
    loan.setTerm(loadLoan.getTermFromServer());
    loan.setPaymentAmount(loadLoan.getPaymentAmountFromServer());
    loan.setPaymentFrequency(loadLoan.getPaymentFrequencyFromServer());
    loan.setCompounding(loadLoan.getCompoundingFromServer());
    return loan;
  }
}
