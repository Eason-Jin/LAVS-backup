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

    public LoanSummary() {}

    public LoanSummary(Double totalInterest, Double totalCost, LocalDate payOffDate) {
        this.totalInterest = totalInterest;
        this.totalCost = totalCost;
        this.payOffDate = payOffDate;
    }

    public LoanSummary(ArrayList<LoanRepayment> repayments) {

        for (LoanRepayment repayment : repayments) {
            totalInterest += repayment.getInterestAmount();
            totalCost += repayment.getPrincipalAmount();
        }
        totalCost += totalInterest;
        this.payOffDate = repayments.get(repayments.size() - 1).getRepaymentDate();
    }

    public Double getTotalInterest() {
        return totalInterest;
    }

    public Double getTotalCost() {
        return totalCost;
    }

    public LocalDate getPayOffDate() { return payOffDate; }
}
