package uoa.lavs.mainframe.simulator.nitrite;

import org.dizitart.no2.Nitrite;
import org.dizitart.no2.collection.Document;
import uoa.lavs.mainframe.Frequency;
import uoa.lavs.mainframe.messages.loan.LoadLoan;
import uoa.lavs.utility.AmortizingLoanCalculator;
import uoa.lavs.utility.LoanCalculator;
import uoa.lavs.utility.LoanRepayment;
import uoa.lavs.utility.PaymentFrequency;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public abstract class LoanBaseProcessor extends BaseProcessor {

    protected LoanBaseProcessor(Nitrite database) {
        super(database);
    }

    protected ArrayList<LoanRepayment> generateRepaymentSchedule(Document doc) {
        // retrieve the required values from the document
        // WARNING: this block of code is dangerous - we are assuming that all the data in the database is
        //          valid and in the correct format!!
        Double principal = Double.parseDouble(doc.get(LoadLoan.Fields.PRINCIPAL, String.class));
        Double interestRate = Double.parseDouble(doc.get(LoadLoan.Fields.RATE_VALUE, String.class));
        Double paymentAmount = Double.parseDouble(doc.get(LoadLoan.Fields.PAYMENT_AMOUNT, String.class));
        String frequencyValue = doc.get(LoadLoan.Fields.PAYMENT_FREQUENCY, String.class);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        LocalDate startDate = LocalDate.parse(doc.get(LoadLoan.Fields.START_DATE, String.class), formatter);
        PaymentFrequency frequency = PaymentFrequency.Weekly;
        switch (frequencyValue) {
            case "3":
                frequency = PaymentFrequency.Fortnightly;
                break;

            case "4":
                frequency = PaymentFrequency.Monthly;
                break;
        }

        // For now, this has been hard-coded. In the future, we should load it based on the configuration.
        LoanCalculator calculator = new AmortizingLoanCalculator();

        ArrayList<LoanRepayment> repayments = calculator.generateRepaymentSchedule(
                principal,
                interestRate,
                paymentAmount,
                frequency,
                startDate
        );
        return repayments;
    }
}
