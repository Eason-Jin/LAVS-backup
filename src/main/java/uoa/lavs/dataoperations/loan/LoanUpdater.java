package uoa.lavs.dataoperations.loan;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import uoa.lavs.LocalInstance;
import uoa.lavs.mainframe.LoanStatus;
import uoa.lavs.mainframe.Status;
import uoa.lavs.mainframe.messages.loan.UpdateLoan;
import uoa.lavs.mainframe.messages.loan.UpdateLoanStatus;
import uoa.lavs.models.Loan;

public class LoanUpdater {

  private static boolean failed = false;

  public static void updateData(String loanId, Loan loan) {
    try {
      // Attempt to update the mainframe first
      loanId = updateMainframe(loanId, loan);
    } catch (Exception e) {
      System.out.println("Mainframe update failed: " + e.getMessage());
      failed = true;
    } finally {
      try {
        // Always attempt to update the database
        updateDatabase(loanId, loan);
      } catch (SQLException e) {
        System.out.println("Database update failed: " + e.getMessage());
      } finally {
        if (failed) {
          addFailedUpdate(loan.getLoanId());
        } else {
          addInMainframe(loan.getLoanId(), loan.getLoanId());
        }
      }
    }
  }

  private static void updateDatabase(String loanId, Loan loan) throws SQLException {
    String sql;
    if (loan.getLoanId() == null) {
      String GET_MAX_NUMBER_SQL =
          "SELECT COALESCE(MAX(CAST(SUBSTR(LoanID, INSTR(LoanID, '-') + 1) AS INTEGER)), 0) + 1"
              + " FROM Loan WHERE CustomerID = ?";
      try (Connection connection = LocalInstance.getDatabaseConnection();
          PreparedStatement getMaxNumberStatement =
              connection.prepareStatement(GET_MAX_NUMBER_SQL)) {
        getMaxNumberStatement.setString(1, loan.getCustomerId());
        try (ResultSet resultSet = getMaxNumberStatement.executeQuery()) {
          if (resultSet.next()) {
            int nextNumber = resultSet.getInt(1);
            loan.setLoanId(loan.getCustomerId().replace(" (Temporary)", "") + "-" + String.format("%02d", nextNumber) + " (Temporary)");
          }
        }
      }
    }
    sql =
        "INSERT INTO Loan (CustomerName, Status, Principal, RateValue, RateType, StartDate,"
            + " Period, Term, PaymentAmount, PaymentFrequency, Compounding, CustomerID, LoanID)"
            + " VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

    try (Connection connection = LocalInstance.getDatabaseConnection();
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
    updateLoan.setLoanId(null);
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

    Status status = updateLoan.send(LocalInstance.getConnection());
    if (!status.getWasSuccessful()) {
      System.out.println(
          "Something went wrong - the Mainframe send failed! The code is " + status.getErrorCode());
      throw new Exception("Mainframe send failed");
    }
    updateLoanStatus.setLoanId(updateLoan.getLoanIdFromServer());
    Status status2 = updateLoanStatus.send(LocalInstance.getConnection());
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

  private static void addFailedUpdate(String loanId) {
    String sql = "UPDATE Loan SET InMainframe = false WHERE LoanID = ?";
    try (Connection connection = LocalInstance.getDatabaseConnection();
        PreparedStatement statement = connection.prepareStatement(sql)) {
      statement.setString(1, loanId);
      statement.executeUpdate();
    } catch (SQLException e) {
      System.out.println("Failed to update database: " + e.getMessage());
    }
  }

  private static void addInMainframe(String loanId, String mainframeId) {
    String sql = "UPDATE Loan SET LoanID = ?, InMainframe = ? WHERE LoanID = ?";
    try (Connection connection = LocalInstance.getDatabaseConnection();
        Statement pragmaStatement = connection.createStatement();
        PreparedStatement statement = connection.prepareStatement(sql)) {
      pragmaStatement.execute("PRAGMA foreign_keys = ON");
      statement.setString(1, mainframeId);
      statement.setBoolean(2, true);
      statement.setString(3, loanId);
      statement.executeUpdate();
    } catch (SQLException e) {
      System.out.println("Failed to update database: " + e.getMessage());
    }
  }

  public static List<Loan> getFailedUpdates() {
    List<Loan> failedUpdates = new ArrayList<>();
    String sql = "SELECT * FROM Loan WHERE InMainframe = false";
    try (Connection connection = LocalInstance.getDatabaseConnection();
        PreparedStatement statement = connection.prepareStatement(sql);
        ResultSet resultSet = statement.executeQuery()) {
      while (resultSet.next()) {
        String customerId = resultSet.getString("CustomerID");
        String loanId = resultSet.getString("LoanID");
        List<Loan> loans = LoanFinder.findData(customerId);
        for (Loan loanOnAccount : loans) {
          if (loanOnAccount.getLoanId().equals(loanId)) {
            failedUpdates.add(loanOnAccount);
            break;
          }
        }
      }
    } catch (SQLException e) {
      System.out.println("Failed to get failed updates: " + e.getMessage());
    }
    return failedUpdates;
  }

  public static void retryFailedUpdates() throws Exception {
    List<Loan> failedUpdates = getFailedUpdates();
    for (Loan loan : failedUpdates) {
      String loanId = loan.getLoanId();
      loan.setLoanId(null); // Reset loan ID before retrying
      String id = updateMainframe(loanId, loan);
      addInMainframe(loanId, id);
    }
  }
}
