package uoa.lavs.dataoperations.loan;

import uoa.lavs.mainframe.Instance;
import uoa.lavs.mainframe.Status;
import uoa.lavs.mainframe.messages.loan.LoadLoanPayments;
import uoa.lavs.models.Loan;
import uoa.lavs.models.LoanPayment;

import java.util.ArrayList;
import java.util.List;
import java.time.LocalDate;

public class LoanPaymentsLoader {

    private static List<LoanPayment> loadFromMainframe(String loanId, int number) throws Exception {
        // Retrieve from mainframe
        LoadLoanPayments loadLoanPayments = new LoadLoanPayments();
        loadLoanPayments.setLoanId(loanId);
        loadLoanPayments.setNumber(number);
        Status status = loadLoanPayments.send(Instance.getConnection());
        if (!status.getWasSuccessful()) {
            System.out.println(
                    "Something went wrong - the Mainframe send failed! The code is " + status.getErrorCode());
            throw new Exception("Mainframe send failed");
        }

        // Initialising variables from output received from mainframe
        Integer repaymentCount = loadLoanPayments.getPaymentCountFromServer();
        List<LoanPayment> loanPayments = new ArrayList<>();
        for (int i = 0; i < repaymentCount; i++) {
            LocalDate month = loadLoanPayments.getPaymentDateFromServer(i);
            Double principal = loadLoanPayments.getPaymentPrincipalFromServer(i);
            Double interest = loadLoanPayments.getPaymentInterestFromServer(i);
            Double remaining = loadLoanPayments.getPaymentRemainingFromServer(i);
            loanPayments.add(new LoanPayment(month, principal, interest, remaining));
        }

        return loanPayments;
    }
}
