package uoa.lavs.dataoperations.loan;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import uoa.lavs.mainframe.Frequency;
import uoa.lavs.LocalInstance;
import uoa.lavs.mainframe.RateType;
import uoa.lavs.mainframe.Status;
import uoa.lavs.mainframe.messages.loan.FindLoan;
import uoa.lavs.mainframe.messages.loan.LoadLoan;
import uoa.lavs.models.Loan;

public class LoanFinder {

  public static List<Loan> findData(String customerId) {
    List<Loan> loans = new ArrayList<>();
    try {
      loans = findFromMainframe(customerId);
    } catch (Exception e) {
      System.out.println("Mainframe find failed: " + e.getMessage());
      System.out.println("Trying to find from database");
      try {
        loans = findFromDatabase(customerId);
      } catch (Exception e1) {
        System.out.println("Database find failed: " + e1.getMessage());
      }
    }
    return loans;
  }

  private static List<Loan> findFromMainframe(String customerId) throws Exception {
    FindLoan findLoan = new FindLoan();
    LoadLoan loadLoan = new LoadLoan();
    findLoan.setId(customerId);
    Status status = findLoan.send(LocalInstance.getConnection());
    if (!status.getWasSuccessful()) {
      System.out.println(
          "Something went wrong - the Mainframe send failed! The code is " + status.getErrorCode());
      throw new Exception("Mainframe send failed");
    }
    Integer loanCount = findLoan.getCountFromServer();
    if (loanCount == 0) {
      throw new Exception("Loan not found in mainframe");
    }
    List<Loan> loans = new ArrayList<>(loanCount);

    for (int i = 1; i <= loanCount; i++) {
      Loan loan = new Loan();
      loadLoan.setLoanId(findLoan.getLoanIdFromServer(i));
      loadLoan.send(LocalInstance.getConnection());
      loan.setLoanId(findLoan.getLoanIdFromServer(i));
      loan.setCustomerId(findLoan.getCustomerIdFromServer());
      loan.setCustomerName(findLoan.getCustomerNameFromServer());
      loan.setStatus(findLoan.getStatusFromServer(i));
      loan.setPrincipal(findLoan.getPrincipalFromServer(i));
      loan.setRateType(loadLoan.getRateTypeFromServer());
      loan.setRateValue(loadLoan.getRateValueFromServer());
      loan.setStartDate(loadLoan.getStartDateFromServer());
      loan.setPeriod(loadLoan.getPeriodFromServer());
      loan.setTerm(loadLoan.getTermFromServer());
      loan.setPaymentAmount(loadLoan.getPaymentAmountFromServer());
      loan.setPaymentFrequency(loadLoan.getPaymentFrequencyFromServer());
      loan.setCompounding(loadLoan.getCompoundingFromServer());
      loan.setCustomerName(loadLoan.getCustomerNameFromServer());
      loans.add(loan);
    }
    return loans;
  }

  private static List<Loan> findFromDatabase(String customerId) throws Exception {
    List<Loan> loans = new ArrayList<>();
    Connection connection = LocalInstance.getDatabaseConnection();
    String query = "SELECT * FROM loan WHERE CustomerID = ?;";
    PreparedStatement preparedStatement = connection.prepareStatement(query);
    preparedStatement.setString(1, customerId);
    ResultSet resultSet = preparedStatement.executeQuery();
    while (resultSet.next()) {
      Loan loan = new Loan();
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
      loans.add(loan);
    }
    connection.close();
    return loans;
  }
}
