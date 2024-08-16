package uoa.lavs.models;

import java.time.LocalDate;
import uoa.lavs.mainframe.Frequency;
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

  public Loan() {}

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
    return customerName;
  }

  public void setCustomerName(String customerName) {
    this.customerName = customerName;
  }

  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }

  public Double getPrincipal() {
    return principal;
  }

  public void setPrincipal(Double principal) {
    this.principal = principal;
  }

  public Double getRateValue() {
    return rateValue;
  }

  public void setRateValue(Double rateValue) {
    this.rateValue = rateValue;
  }

  public RateType getRateType() {
    return rateType;
  }

  public void setRateType(RateType rateType) {
    this.rateType = rateType;
  }

  public LocalDate getStartDate() {
    return startDate;
  }

  public void setStartDate(LocalDate startDate) {
    this.startDate = startDate;
  }

  public Integer getPeriod() {
    return period;
  }

  public void setPeriod(Integer period) {
    this.period = period;
  }

  public Integer getTerm() {
    return term;
  }

  public void setTerm(Integer term) {
    this.term = term;
  }

  public Double getPaymentAmount() {
    return paymentAmount;
  }

  public void setPaymentAmount(Double paymentAmount) {
    this.paymentAmount = paymentAmount;
  }

  public Frequency getPaymentFrequency() {
    return paymentFrequency;
  }

  public void setPaymentFrequency(Frequency paymentFrequency) {
    this.paymentFrequency = paymentFrequency;
  }

  public Frequency getCompounding() {
    return compounding;
  }

  public void setCompounding(Frequency compounding) {
    this.compounding = compounding;
  }
}
