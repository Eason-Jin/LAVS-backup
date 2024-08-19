package uoa.lavs.dataoperations.loan;

import java.sql.*;
import uoa.lavs.mainframe.Instance;
import uoa.lavs.mainframe.LoanStatus;
import uoa.lavs.mainframe.Status;
import uoa.lavs.mainframe.messages.loan.UpdateLoan;
import uoa.lavs.mainframe.messages.loan.UpdateLoanStatus;
import uoa.lavs.models.Loan;

public class LoanUpdater {

  public static void updateData(String loanId, Loan loan) {
    try {
      // Attempt to update the mainframe first
      loanId = updateMainframe(loanId, loan);
    } catch (Exception e) {
      System.out.println("Mainframe update failed: " + e.getMessage());
    } finally {
      try {
        // Always attempt to update the database
        updateDatabase(loanId, loan);
      } catch (SQLException e) {
        System.out.println("Database update failed: " + e.getMessage());
      }
    }
  }

  private static void updateDatabase(String loanId, Loan loan) throws SQLException {
    String sql;
    if (loan.getLoanId() == null) {
      String GET_MAX_NUMBER_SQL =
          "SELECT COALESCE(MAX(CAST(SUBSTR(LoanID, INSTR(LoanID, '-') + 1) AS INTEGER)), 0) + 1"
              + " FROM Loan WHERE CustomerID = ?";
      try (Connection connection = Instance.getDatabaseConnection();
          PreparedStatement getMaxNumberStatement =
              connection.prepareStatement(GET_MAX_NUMBER_SQL)) {
        getMaxNumberStatement.setString(1, loan.getCustomerId());
        try (ResultSet resultSet = getMaxNumberStatement.executeQuery()) {
          if (resultSet.next()) {
            int nextNumber = resultSet.getInt(1);
            loan.setLoanId(loan.getCustomerId() + "-" + String.format("%02d", nextNumber));
          }
        }
      }
      sql =
          "INSERT INTO Loan (CustomerName, Status, Principal, RateValue, RateType, StartDate,"
              + " Period, Term, PaymentAmount, PaymentFrequency, Compounding, CustomerID, LoanID)"
              + " VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
    } else {
      sql =
          "UPDATE Loan SET CustomerName = ?, Status = ?, Principal = ?, RateValue = ?, RateType ="
              + " ?, StartDate = ?, Period = ?, Term = ?, PaymentAmount = ?, PaymentFrequency = ?,"
              + " Compounding = ? WHERE LoanID = ?";
    }

    try (Connection connection = Instance.getDatabaseConnection();
        PreparedStatement statement = connection.prepareStatement(sql)) {
      statement.setString(1, loan.getCustomerName());
      statement.setString(2, loan.getStatus());
      statement.setDouble(3, loan.getPrincipal());
      statement.setDouble(4, loan.getRateValue());
      statement.setString(5, loan.getRateType().toString());
      statement.setObject(6, loan.getStartDate());
      statement.setInt(7, loan.getPeriod());
      statement.setInt(8, loan.getTerm());
      statement.setDouble(9, loan.getPaymentAmount());
      statement.setString(10, loan.getPaymentFrequency().toString());
      statement.setString(11, loan.getCompounding().toString());
      statement.setString(12, loan.getCustomerId());
      statement.setString(13, loan.getLoanId());

      statement.executeUpdate();
    }
  }

  private static String updateMainframe(String loanId, Loan loan) throws Exception {
    UpdateLoan updateLoan = new UpdateLoan();
    UpdateLoanStatus updateLoanStatus = new UpdateLoanStatus();
    updateLoan.setLoanId(loanId);
    updateLoan.setCustomerId(loan.getCustomerId());
    updateLoan.setPrincipal(loan.getPrincipal());
    updateLoan.setRateValue(loan.getRateValue());
    updateLoan.setRateType(loan.getRateType());
    updateLoan.setStartDate(loan.getStartDate());
    updateLoan.setPeriod(loan.getPeriod());
    updateLoan.setTerm(loan.getTerm());
    updateLoan.setPaymentAmount(loan.getPaymentAmount());
    updateLoan.setPaymentFrequency(loan.getPaymentFrequency());
    updateLoan.setCompounding(loan.getCompounding());
    updateLoanStatus.setStatus(LoanStatus.Pending);

    Status status = updateLoan.send(Instance.getConnection());
    if (!status.getWasSuccessful()) {
      System.out.println(
          "Something went wrong - the Mainframe send failed! The code is " + status.getErrorCode());
      throw new Exception("Mainframe send failed");
    }
    updateLoanStatus.setLoanId(updateLoan.getLoanIdFromServer());
    Status status2 = updateLoanStatus.send(Instance.getConnection());
    if (!status2.getWasSuccessful()) {
      System.out.println(
          "Something went wrong - the Mainframe send failed! The code is " + status.getErrorCode());
      throw new Exception("Mainframe send failed");
    }
    // Synchronise the ID and Status across Local and Mainframe
    loan.setLoanId(updateLoan.getLoanIdFromServer());
    loan.setStatus(updateLoan.getStatusFromServer());
    return updateLoan.getLoanIdFromServer();
  }
}
