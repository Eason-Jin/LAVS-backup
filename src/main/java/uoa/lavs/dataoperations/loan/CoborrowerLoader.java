package uoa.lavs.dataoperations.loan;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import uoa.lavs.mainframe.Instance;
import uoa.lavs.mainframe.Status;
import uoa.lavs.mainframe.messages.loan.LoadLoanCoborrowers;

public class CoborrowerLoader {

  public static List<String> loadData(String loanId, int number) {
    List<String> coborrowerIds = new ArrayList<>();
    try {
      coborrowerIds = loadFromMainframe(loanId, number);
      if (coborrowerIds.isEmpty()) {
        throw new Exception("Coborrower not in mainframe");
      }
    } catch (Exception e) {
      System.out.println("Mainframe load failed: " + e.getMessage());
      System.out.println("Trying to load from database");
      try {
        coborrowerIds = loadFromDatabase(loanId, number);
      } catch (Exception e1) {
        System.out.println("Database load failed: " + e1.getMessage());
      }
    }
    return coborrowerIds;
  }

  private static List<String> loadFromDatabase(String loanId, int number) throws Exception {
    List<String> coborrowerIds = new ArrayList<>();
    String query = "SELECT TOP(" + number + ") CoborrowerID FROM Coborrower WHERE LoanID = ?";
    try (Connection connection = Instance.getDatabaseConnection();
        PreparedStatement statement = connection.prepareStatement(query)) {
      statement.setString(1, loanId);
      try (ResultSet resultSet = statement.executeQuery()) {
        while (resultSet.next()) {
          coborrowerIds.add(resultSet.getString("CoborrowerID"));
        }
      }
    }
    if (coborrowerIds.isEmpty()) {
      throw new Exception("Coborrower not in database");
    }
    return coborrowerIds;
  }

  private static List<String> loadFromMainframe(String loanId, int number) throws Exception {
    LoadLoanCoborrowers loadLoanCoborrower = new LoadLoanCoborrowers();
    loadLoanCoborrower.setLoanId(loanId);
    loadLoanCoborrower.setNumber(number);
    Status status = loadLoanCoborrower.send(Instance.getConnection());
    if (!status.getWasSuccessful()) {
      System.out.println(
          "Something went wrong - the Mainframe send failed! The code is " + status.getErrorCode());
      throw new Exception("Mainframe send failed");
    }

    List<String> coborrowerIds = new ArrayList<>();
    for (int i = 0; i < number; i++) {
      String coborrowerId = loadLoanCoborrower.getCoborrowerIdFromServer(i);
      coborrowerIds.add(coborrowerId);
    }

    if (coborrowerIds.isEmpty()) {
      throw new Exception("Coborrower not in mainframe");
    }
    return coborrowerIds;
  }
}
