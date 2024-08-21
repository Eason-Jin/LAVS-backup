package uoa.lavs.utility;

import uoa.lavs.mainframe.Frequency;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;

public class LoanSummary {
    private Double totalInterest = 0.0;
    private Double totalCost = 0.0;
    private LocalDate payOffDate;

    public LoanSummary(Double totalInterest, Double totalCost, LocalDate payOffDate) {
        this.totalInterest = totalInterest;
        this.totalCost = totalCost;
        this.payOffDate = payOffDate;
    }

    public LoanSummary(ArrayList<LoanRepayment> repayments, Frequency paymentFrequency) {

        for (LoanRepayment repayment : repayments) {
            totalInterest += repayment.getInterestAmount();
            totalCost += repayment.getPrincipalAmount();
        }
        totalCost += totalInterest;

        // Round totalInterest and totalCost to 2 decimal places using BigDecimal
        totalInterest = new BigDecimal(totalInterest).setScale(2, RoundingMode.HALF_UP).doubleValue();
        totalCost = new BigDecimal(totalCost).setScale(2, RoundingMode.HALF_UP).doubleValue();

        // Adjust the payoff date based on the payment frequency
        LocalDate lastRepaymentDate = repayments.get(repayments.size() - 1).getRepaymentDate();
        switch (paymentFrequency) {
            case Weekly -> this.payOffDate = lastRepaymentDate.plusWeeks(1);
            case Fortnightly -> this.payOffDate = lastRepaymentDate.plusWeeks(2);
            case Monthly -> this.payOffDate = lastRepaymentDate.plusMonths(1);
        }
    }

    public LoanSummary(ArrayList<LoanRepayment> repayments) {

        for (LoanRepayment repayment : repayments) {
            totalInterest += repayment.getInterestAmount();
            totalCost += repayment.getPrincipalAmount();
        }
        totalCost += totalInterest;
    }

    public Double getTotalInterest() {
        return totalInterest;
    }

    public Double getTotalCost() {
        return totalCost;
    }

    public LocalDate getPayOffDate() { return payOffDate; }
}
