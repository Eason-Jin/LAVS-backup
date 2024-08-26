package uoa.lavs.dataoperations.loan;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import uoa.lavs.LocalInstance;
import uoa.lavs.mainframe.Status;
import uoa.lavs.mainframe.messages.loan.UpdateLoanCoborrower;

public class CoborrowerUpdater {

  private static boolean failed = false;

  public static void updateData(String loanId, String coborrowerId, Integer number) {
    try {
      updateMainframe(loanId, coborrowerId, number);
    } catch (Exception e) {
      System.out.println("Mainframe update failed: " + e.getMessage());
      failed = true;
    } finally {
      try {
        updateDatabase(loanId, coborrowerId);
      } catch (Exception e) {
        System.out.println("Database update failed: " + e.getMessage());
      } finally {
        if (failed) {
          addFailedUpdate(loanId, coborrowerId);
        } else {
          addInMainframe(loanId, coborrowerId);
        }
      }
    }
  }

  public static void updateDatabase(String loanId, String coborrowerId) throws SQLException {

    String sql = "INSERT INTO Coborrower (CoborrowerID, LoanID)" + " VALUES (?, ?)";

    // PreparedStatement prepares an SQL statement for execution. We then obtain the auto generated
    // incremented
    // key value.
    try (Connection connection = LocalInstance.getDatabaseConnection();
        PreparedStatement statement =
            connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
      statement.setString(1, coborrowerId);
      statement.setString(2, loanId);

      // Now that we have filled in our SQL query, execute it
      statement.executeUpdate();
    }
  }

  public static int updateMainframe(String loanId, String coborrowerId, Integer number)
      throws Exception {
    UpdateLoanCoborrower updateLoanCoborrower = new UpdateLoanCoborrower();

    if (number != null) {
      updateLoanCoborrower.setLoanId(loanId);
      updateLoanCoborrower.setCoborrowerId(coborrowerId);
      updateLoanCoborrower.setNumber(number);
    } else {
      updateLoanCoborrower.setLoanId(loanId);
      updateLoanCoborrower.setCoborrowerId(coborrowerId);
    }
    Status status = updateLoanCoborrower.send(LocalInstance.getConnection());
    if (!status.getWasSuccessful()) {
      failed = true;
      System.out.println(
          "Something went wrong - the Mainframe send failed! The code is " + status.getErrorCode());
      throw new Exception("Mainframe send failed");
    }
    return updateLoanCoborrower.getNumberFromServer();
  }

  private static void addFailedUpdate(String loanId, String coborrowerId) {
    String sql = "UPDATE Coborrower SET InMainframe = false WHERE LoanID = ? AND CoborrowerID = ?";
    try (Connection connection = LocalInstance.getDatabaseConnection();
        PreparedStatement statement = connection.prepareStatement(sql)) {
      statement.setString(1, loanId);
      statement.setString(2, coborrowerId);
      statement.executeUpdate();
    } catch (SQLException e) {
      System.out.println("Failed to update database: " + e.getMessage());
    }
  }

  private static void addInMainframe(String loanId, String coborrowerId) {
    String sql = "UPDATE Coborrower SET InMainframe = true WHERE LoanID = ? AND CoborrowerID = ?";
    try (Connection connection = LocalInstance.getDatabaseConnection();
        PreparedStatement statement = connection.prepareStatement(sql)) {
      statement.setString(1, loanId);
      statement.setString(2, coborrowerId);
      statement.executeUpdate();
    } catch (SQLException e) {
      System.out.println("Failed to update database: " + e.getMessage());
    }
  }

  public static List<String> getFailedUpdates() {
    List<String> failedUpdates = new ArrayList<>();
    String sql = "SELECT LoanID, CoborrowerID FROM Coborrower WHERE InMainframe = false";
    try (Connection connection = LocalInstance.getDatabaseConnection();
        PreparedStatement statement = connection.prepareStatement(sql);
        ResultSet resultSet = statement.executeQuery()) {
      while (resultSet.next()) {
        String loanId = resultSet.getString("LoanID");
        String coborrowerId = resultSet.getString("CoborrowerID");
        failedUpdates.add(loanId + "," + coborrowerId);
      }
    } catch (SQLException e) {
      System.out.println("Failed to get failed updates: " + e.getMessage());
    }
    return failedUpdates;
  }

  public static void retryFailedUpdates() throws Exception {
    List<String> failedUpdates = getFailedUpdates();
    for (String coborrowerInfo : failedUpdates) {
      String[] parts = coborrowerInfo.split(",");
      if (parts.length == 2) {
        String loanId = parts[0];
        String coborrowerId = parts[1];
          updateMainframe(loanId, coborrowerId, null);
          addInMainframe(loanId, coborrowerId);
      } else {
        System.out.println("Invalid coborrower info: " + coborrowerInfo);
      }
    }
  }
}
