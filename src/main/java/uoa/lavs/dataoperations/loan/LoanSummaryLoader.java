package uoa.lavs.dataoperations.loan;

import uoa.lavs.LocalInstance;
import uoa.lavs.mainframe.Status;
import uoa.lavs.mainframe.messages.loan.LoadLoanSummary;
import uoa.lavs.utility.LoanRepayment;
import uoa.lavs.utility.LoanSummary;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class LoanSummaryLoader {

    public static LoanSummary calculateLoanSummary(String loanId) {
        LoanSummary loanSummary = new LoanSummary();
        try {
            loanSummary = calculateFromMainframe(loanId);
        } catch (Exception e) {
            System.out.println("Mainframe calculation failed: " + e.getMessage());
            System.out.println("Trying to find and calculate from database");
            try {
                loanSummary = calculateLocally(loanId);
            } catch (Exception e1) {
                System.out.println("Database find and calculation failed: " + e1.getMessage());
            }
        }
        return loanSummary;

    }

    public static LoanSummary calculateLocally(String loanId) throws Exception {
        LoanPaymentsLoader loanPaymentsLoader = new LoanPaymentsLoader();
        List<LoanRepayment> loanRepayments = loanPaymentsLoader.calculateLocally(loanId);
        LoanSummary loanSummary = new LoanSummary((ArrayList<LoanRepayment>) loanRepayments);
        return loanSummary;
    }

    public static LoanSummary calculateFromMainframe(String loanId) throws Exception {
        LoadLoanSummary loadLoanSummary = new LoadLoanSummary();
        loadLoanSummary.setLoanId(loanId);
        Status status = loadLoanSummary.send(LocalInstance.getConnection());
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
