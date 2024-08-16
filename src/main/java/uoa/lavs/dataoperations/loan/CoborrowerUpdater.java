package uoa.lavs.dataoperations.loan;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import uoa.lavs.mainframe.Instance;
import uoa.lavs.mainframe.Status;
import uoa.lavs.mainframe.messages.loan.UpdateLoanCoborrower;

public class CoborrowerUpdater {

  public static void updateData(String loanId, String coborrowerId, Integer number) {
    Integer coborrowerNumber = null;
    try {
      coborrowerNumber = updateMainframe(loanId, coborrowerId, number);
    } catch (Exception e) {
      System.out.println("Mainframe update failed: " + e.getMessage());
    } finally {
      try {
        if (coborrowerNumber == null) {
          coborrowerNumber = number;
        }
        updateDatabase(loanId, coborrowerId);
      } catch (Exception e) {
        System.out.println("Database update failed: " + e.getMessage());
      }
    }
  }

  private static void updateDatabase(String loanId, String coborrowerId) throws SQLException {

    String sql = "INSERT INTO Coborrower (CoborrowerID, LoanID)" + " VALUES (?, ?)";

    // PreparedStatement prepares an SQL statement for execution. We then obtain the auto generated
    // incremented
    // key value.
    try (Connection connection = Instance.getDatabaseConnection();
        PreparedStatement statement =
            connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
      statement.setString(1, coborrowerId);
      statement.setString(2, loanId);

      // Now that we have filled in our SQL query, execute it
      statement.executeUpdate();
    }
  }

  private static int updateMainframe(String loanId, String coborrowerId, Integer number)
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
    Status status = updateLoanCoborrower.send(Instance.getConnection());
    if (!status.getWasSuccessful()) {
      System.out.println(
          "Something went wrong - the Mainframe send failed! The code is " + status.getErrorCode());
      throw new Exception("Mainframe send failed");
    }
    return updateLoanCoborrower.getNumberFromServer();
  }
}
