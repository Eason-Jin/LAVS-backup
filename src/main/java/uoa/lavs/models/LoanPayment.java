package uoa.lavs.models;

import java.time.LocalDate;

public class LoanPayment {
    LocalDate month;
    Double principal;
    Double interest;
    Double remaining;

    public LoanPayment(LocalDate month, Double principal, Double interest, Double remaining) {
        this.month = month;
        this.principal = principal;
        this.interest = interest;
        this.remaining = remaining;
    }

    public LocalDate getMonth() {
        return month;
    }

    public void setMonth(LocalDate month) {
        this.month = month;
    }

    public Double getPrincipal() {
        return principal;
    }

    public void setPrincipal(Double principal) {
        this.principal = principal;
    }

    public Double getInterest() {
        return interest;
    }

    public void setInterest(Double interest) {
        this.interest = interest;
    }

    public Double getRemaining() {
        return remaining;
    }

    public void setRemaining(Double remaining) {
        this.remaining = remaining;
    }
}
