package uoa.lavs.dataoperations.loan;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import uoa.lavs.mainframe.Instance;
import uoa.lavs.mainframe.Status;
import uoa.lavs.mainframe.messages.loan.FindLoan;
import uoa.lavs.models.Loan;

public class LoanFinder {

  public static List<Loan> findData(String customerId) {
    List<Loan> loans = new ArrayList<>();
    try {
      loans = findFromDatabase(customerId);
    } catch (Exception e) {
      System.out.println("Database find failed: " + e.getMessage());
      System.out.println("Trying to find from mainframe");
      try {
        loans = findFromMainframe(customerId);
      } catch (Exception e1) {
        System.out.println("Mainframe find failed: " + e1.getMessage());
      }
    }
    return loans;
  }

  private static List<Loan> findFromMainframe(String customerId) throws Exception {
    FindLoan findLoan = new FindLoan();
    findLoan.setId(customerId);
    Status status = findLoan.send(Instance.getConnection());
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
      loan.setLoanId(findLoan.getLoanIdFromServer(i));
      loan.setCustomerId(findLoan.getCustomerIdFromServer());
      loan.setCustomerName(findLoan.getCustomerNameFromServer());
      loan.setStatus(findLoan.getStatusFromServer(i));
      loan.setPrincipal(findLoan.getPrincipalFromServer(i));
      loans.add(loan);
    }
    return loans;
  }

  private static List<Loan> findFromDatabase(String customerId) throws Exception {
    List<Loan> loans = new ArrayList<>();
    Connection connection = Instance.getDatabaseConnection();
    String query = "SELECT * FROM loan WHERE CustomerID = ?;";
    PreparedStatement preparedStatement = connection.prepareStatement(query);
    preparedStatement.setString(1, customerId);
    ResultSet resultSet = preparedStatement.executeQuery();
    while (resultSet.next()) {
      Loan loan = new Loan();
      loan.setLoanId(resultSet.getString("LoanID"));
      loan.setCustomerId(resultSet.getString("CustomerID"));
      loan.setCustomerName(resultSet.getString("CustomerName"));
      loan.setStatus(resultSet.getString("Status"));
      loan.setPrincipal(resultSet.getDouble("Principal"));
      loans.add(loan);
    }
    connection.close();
    return loans;
  }
}
