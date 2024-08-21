package uoa.lavs.dataoperations.loan;

import uoa.lavs.mainframe.Instance;
import uoa.lavs.mainframe.Status;
import uoa.lavs.mainframe.messages.loan.LoadLoanPayments;
import uoa.lavs.utility.LoanRepayment;

import java.util.ArrayList;
import java.util.List;
import java.time.LocalDate;

public class LoanPaymentsLoader {

//    private static List<LoanPayment> calculateLocally(String loanId, int number) throws Exception {
//        // Calculate locally
//        AmortizingLoanCalculator calculator = new AmortizingLoanCalculator();
//        List<LoanPayment> loanPayments = new ArrayList<>();
//
//        // Load in the loan
//        LoanFinder loanFinder = new LoanFinder();
//
//        loanPayments = calculator.generateRepaymentSchedule();
//
//        return loanPayments;
//    }

    public static List<LoanRepayment> calculateFromMainframe(String loanId) throws Exception {
        // Initialise LoadLoanPayments message
        LoadLoanPayments loadLoanPayments = new LoadLoanPayments();
        loadLoanPayments.setLoanId(loanId);

        // Send request to get the first page to determine total page count
        loadLoanPayments.setNumber(1);
        Status status = loadLoanPayments.send(Instance.getConnection());
        if (!status.getWasSuccessful()) {
            System.out.println(
                    "Something went wrong - the Mainframe send failed! The code is " + status.getErrorCode());
            throw new Exception("Mainframe send failed");
        }

        // Get the total number of pages
        Integer pageCount = loadLoanPayments.getPageCountFromServer();
        if (pageCount == null) {
            throw new Exception("Failed to retrieve page count from server.");
        }

        List<LoanRepayment> loanPayments = new ArrayList<>();

        // Loop over all pages
        for (int pageNumber = 1; pageNumber <= pageCount; pageNumber++) {
            loadLoanPayments.setNumber(pageNumber);
            status = loadLoanPayments.send(Instance.getConnection());
            if (!status.getWasSuccessful()) {
                String errorMessage = "Failed to retrieve page " + pageNumber + ". Error code: " + status.getErrorCode();
                System.out.println(errorMessage);
                throw new Exception(errorMessage);
            }

            // Get the number of payments on this page
            Integer paymentCount = loadLoanPayments.getPaymentCountFromServer();
            if (paymentCount == null) {
                System.out.println("No payments found on page " + pageNumber);
                continue;
            }

            // Loop through each payment on this page and collect the details
            for (int i = 1; i <= paymentCount; i++) {
                Integer number = loadLoanPayments.getPaymentNumberFromServer(i);
                LocalDate month = loadLoanPayments.getPaymentDateFromServer(i);
                Double principal = loadLoanPayments.getPaymentPrincipalFromServer(i);
                Double interest = loadLoanPayments.getPaymentInterestFromServer(i);
                Double remaining = loadLoanPayments.getPaymentRemainingFromServer(i);

                if (month != null && principal != null && interest != null && remaining != null) {
                    loanPayments.add(new LoanRepayment(number, month, principal, interest, remaining));
                } else {
                    System.out.println("Warning: Skipping payment " + i + " on page " + pageNumber + " due to missing data.");
                }
            }
        }

        return loanPayments;
    }
}
