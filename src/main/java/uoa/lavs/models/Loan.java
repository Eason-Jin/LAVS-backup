package uoa.lavs.models;

import java.time.LocalDate;
import uoa.lavs.mainframe.Frequency;
import uoa.lavs.mainframe.LoanStatus;
import uoa.lavs.mainframe.RateType;

public class Loan {
  // Primary Key
  private String loanId;
  // Foreign Key
  private String customerId;
  private String customerName;
  private String status;
  private Double principal;
  private Double rateValue;
  private RateType rateType;
  private LocalDate startDate;
  private Integer period;
  private Integer term;
  private Double paymentAmount;
  private Frequency paymentFrequency;
  private Frequency compounding;

  public Loan(
      String loanId,
      String customerId,
      String customerName,
      String status,
      Double principal,
      Double rateValue,
      RateType rateType,
      LocalDate startDate,
      Integer period,
      Integer term,
      Double paymentAmount,
      Frequency paymentFrequency,
      Frequency compounding) {
    this.loanId = loanId;
    this.customerId = customerId;
    this.customerName = customerName;
    this.status = status;
    this.principal = principal;
    this.rateValue = rateValue;
    this.rateType = rateType;
    this.startDate = startDate;
    this.period = period;
    this.term = term;
    this.paymentAmount = paymentAmount;
    this.paymentFrequency = paymentFrequency;
    this.compounding = compounding;
  }

  public Loan() {
  }

  public String getLoanId() {
    return loanId;
  }

  public void setLoanId(String loanId) {
    this.loanId = loanId;
  }

  public String getCustomerId() {
    return customerId;
  }

  public void setCustomerId(String customerId) {
    this.customerId = customerId;
  }

  public String getCustomerName() {
    return (customerName == null || customerName == "null") ? "" : customerName;
  }

  public void setCustomerName(String customerName) {
    this.customerName = customerName;
  }

  public String getStatus() {
    try {
      Integer.parseInt(status);
    } catch (NumberFormatException e) {
      return status;
    }

    switch (status) {
      case "1":
        return LoanStatus.New.toString();
      case "2":
        return LoanStatus.Pending.toString();
      case "5":
        return LoanStatus.Active.toString();
      case "8":
        return LoanStatus.Cancelled.toString();
      default:
        return LoanStatus.Unknown.toString();
    }
  }

  public void setStatus(String status) {
    this.status = status;
  }

  public Double getPrincipal() {
    return (principal == null) ? 0.0 : principal;
  }

  public String getPrincipalString() {
    return "$" + String.format("%.2f", getPrincipal());
  }

  public void setPrincipal(Double principal) {
    this.principal = principal;
  }

  public Double getRateValue() {
    return (rateValue == null) ? 0.0 : rateValue;
  }

  public void setRateValue(Double rateValue) {
    this.rateValue = rateValue;
  }

  public RateType getRateType() {
    return (rateType == null) ? RateType.Unknown : rateType;
  }

  public void setRateType(RateType rateType) {
    this.rateType = rateType;
  }

  public LocalDate getStartDate() {
    return (startDate == null) ? LocalDate.of(1, 1, 1) : startDate;
  }

  public void setStartDate(LocalDate startDate) {
    this.startDate = startDate;
  }

  public Integer getPeriod() {
    return (period == null) ? 0 : period;
  }

  public void setPeriod(Integer period) {
    this.period = period;
  }

  public Integer getTerm() {
    return (term == null) ? 0 : term;
  }

  public void setTerm(Integer term) {
    this.term = term;
  }

  public Double getPaymentAmount() {
    return (paymentAmount == null) ? 0.0 : paymentAmount;
  }

  public String getPaymentAmountString() {
    return "$" + String.format("%.2f", getPaymentAmount());
  }

  public void setPaymentAmount(Double paymentAmount) {
    this.paymentAmount = paymentAmount;
  }

  public Frequency getPaymentFrequency() {
    return (paymentFrequency == null) ? Frequency.Unknown : paymentFrequency;
  }

  public void setPaymentFrequency(Frequency paymentFrequency) {
    this.paymentFrequency = paymentFrequency;
  }

  public Frequency getCompounding() {
    return (compounding == null) ? Frequency.Unknown : compounding;
  }

  public void setCompounding(Frequency compounding) {
    this.compounding = compounding;
  }
}
