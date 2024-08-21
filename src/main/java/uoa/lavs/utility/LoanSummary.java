package uoa.lavs.utility;

import java.time.LocalDate;
import java.util.ArrayList;

public class LoanSummary {
    private Double totalInterest = 0.0;
    private Double totalCost = 0.0;
    private LocalDate payOffDate;
    private PaymentFrequency paymentFrequency;

    public LoanSummary(Double totalInterest, Double totalCost, LocalDate payOffDate) {
        this.totalInterest = totalInterest;
        this.totalCost = totalCost;
        this.payOffDate = payOffDate;
    }

    public LoanSummary(ArrayList<LoanRepayment> repayments, PaymentFrequency paymentFrequency) {

        for (LoanRepayment repayment : repayments) {
            totalInterest += repayment.getInterestAmount();
            totalCost += repayment.getPrincipalAmount();
        }
        totalCost += totalInterest;

        // Adjust the payoff date based on the payment frequency
        LocalDate lastRepaymentDate = repayments.get(repayments.size() - 1).getRepaymentDate();
        switch (paymentFrequency) {
            case Weekly -> this.payOffDate = lastRepaymentDate.plusWeeks(1);
            case Fortnightly -> this.payOffDate = lastRepaymentDate.plusWeeks(2);
            case Monthly -> this.payOffDate = lastRepaymentDate.plusMonths(1);
        }
    }

    public Double getTotalInterest() {
        return totalInterest;
    }

    public Double getTotalCost() {
        return totalCost;
    }

    public LocalDate getPayOffDate() { return payOffDate; }
}
