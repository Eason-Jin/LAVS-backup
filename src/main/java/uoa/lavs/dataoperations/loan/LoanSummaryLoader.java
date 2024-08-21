package uoa.lavs.dataoperations.loan;

import uoa.lavs.mainframe.Frequency;
import uoa.lavs.mainframe.Instance;
import uoa.lavs.mainframe.RateType;
import uoa.lavs.mainframe.Status;
import uoa.lavs.mainframe.messages.loan.LoadLoanSummary;
import uoa.lavs.models.Loan;
import uoa.lavs.utility.LoanRepayment;
import uoa.lavs.utility.LoanSummary;
import uoa.lavs.utility.PaymentFrequency;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class LoanSummaryLoader {

    public static LoanSummary calculateLocally(String loanId) throws Exception {
        LoanPaymentsLoader loanPaymentsLoader = new LoanPaymentsLoader();
        List<LoanRepayment> loanRepayments = loanPaymentsLoader.calculateLocally(loanId);
        String query = "SELECT * FROM Loan WHERE LoanID = ?";
        try (Connection connection = Instance.getDatabaseConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, loanId);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (!resultSet.next()) {
                    throw new Exception("Loan not in database");
                }
                Frequency paymentFrequency = Frequency.valueOf(resultSet.getString("PaymentFrequency"));
                LoanSummary loanSummary = new LoanSummary((ArrayList<LoanRepayment>) loanRepayments, paymentFrequency);
                return loanSummary;
            }
        }
    }

    public static LoanSummary calculateFromMainframe(String loanId) throws Exception {
        LoadLoanSummary loadLoanSummary = new LoadLoanSummary();
        loadLoanSummary.setLoanId(loanId);
        Status status = loadLoanSummary.send(Instance.getConnection());
        if (!status.getWasSuccessful()) {
            System.out.println(
                    "Something went wrong - the Mainframe send failed! The code is " + status.getErrorCode());
            throw new Exception("Mainframe send failed");
        }
        Double totalInterest = loadLoanSummary.getTotalInterestFromServer();
        Double totalLoanCost = loadLoanSummary.getTotalLoanCostFromServer();
        LocalDate payOffDate = loadLoanSummary.getPayoffDateFromServer();
        LoanSummary loanSummary = new LoanSummary(totalInterest, totalLoanCost, payOffDate);
        return loanSummary;
    }
}
