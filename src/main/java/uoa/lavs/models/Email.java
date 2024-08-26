package uoa.lavs.models;

public class Email extends Detail {
  private String customerId;
  private Integer number;
  private String address;
  private Boolean isPrimary;

  public Email(String customerId, String address, Boolean isPrimary, Integer number) {
    this.customerId = customerId;
    this.address = address;
    this.isPrimary = isPrimary;
    this.number = number;
  }


  public Email() {}

  public String getCustomerId() {
    return customerId;
  }

  public void setCustomerId(String customerId) {
    this.customerId = customerId;
  }

  public Integer getNumber() {
    return number;
  }

  public void setNumber(Integer number) {
    this.number = number;
  }

  public String getAddress() {
    return (address == null || address == "null") ? "" : address;
  }

  public void setAddress(String address) {
    this.address = address;
  }

  public Boolean getIsPrimary() {
    return (isPrimary == null) ? false : isPrimary;
  }

  public void setIsPrimary(Boolean isPrimary) {
    this.isPrimary = isPrimary;
  }
}
