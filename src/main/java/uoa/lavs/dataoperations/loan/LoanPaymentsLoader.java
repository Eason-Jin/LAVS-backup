package uoa.lavs.dataoperations.loan;

import uoa.lavs.LocalInstance;
import uoa.lavs.mainframe.Status;
import uoa.lavs.mainframe.messages.loan.LoadLoanPayments;
import uoa.lavs.utility.AmortizingLoanCalculator;
import uoa.lavs.utility.LoanRepayment;
import uoa.lavs.utility.PaymentFrequency;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.time.LocalDate;

public class LoanPaymentsLoader {

    public static List<LoanRepayment> calculateLoanRepayments(String loanId) {
        List<LoanRepayment> loanRepayments = new ArrayList<>();
        try {
            loanRepayments = calculateFromMainframe(loanId);
        } catch (Exception e) {
            System.out.println("Mainframe calculation failed: " + e.getMessage());
            System.out.println("Trying to find and calculate from database");
            try {
                loanRepayments = calculateLocally(loanId);
            } catch (Exception e1) {
                System.out.println("Database find and calculation failed: " + e1.getMessage());
            }
        }
        return loanRepayments;
    }

        public static List<LoanRepayment> calculateLocally(String loanId) throws Exception {
        String query = "SELECT * FROM loan WHERE LoanID = ?;";
        
        try (Connection connection = LocalInstance.getDatabaseConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            
            preparedStatement.setString(1, loanId);
            
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    // Calculate repayments
                    AmortizingLoanCalculator calculator = new AmortizingLoanCalculator();
                    return calculator.generateRepaymentSchedule(
                            resultSet.getDouble("Principal"),
                            resultSet.getDouble("RateValue"),
                            resultSet.getDouble("PaymentAmount"),
                            PaymentFrequency.valueOf(resultSet.getString("PaymentFrequency")),
                            resultSet.getObject("StartDate", LocalDate.class)
                    );
                } else {
                    throw new Exception("Loan not found for ID: " + loanId);
                }
            }
        }
    }

    public static List<LoanRepayment> calculateFromMainframe(String loanId) throws Exception {
        // Initialise LoadLoanPayments message
        LoadLoanPayments loadLoanPayments = new LoadLoanPayments();
        loadLoanPayments.setLoanId(loanId);

        // Send request to get the first page to determine total page count
        loadLoanPayments.setNumber(1);
        Status status = loadLoanPayments.send(LocalInstance.getConnection());
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

        List<LoanRepayment> loanRepayments = new ArrayList<>();

        // Loop over all pages
        for (int pageNumber = 1; pageNumber <= pageCount; pageNumber++) {
            loadLoanPayments.setNumber(pageNumber);
            status = loadLoanPayments.send(LocalInstance.getConnection());
            if (!status.getWasSuccessful()) {
                throw new Exception("Failed to retrieve page");
            }

            // Get the number of payments on this page
            Integer paymentCount = loadLoanPayments.getPaymentCountFromServer();
            if (paymentCount == null) {
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
                    loanRepayments.add(new LoanRepayment(number, month, principal, interest, remaining));
                }
            }
        }
        return loanRepayments;
    }
}
