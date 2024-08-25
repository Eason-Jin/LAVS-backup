package uoa.lavs.models;

public class Phone extends Detail {
  private String customerId;
  private Integer number;
  private String type;
  private String prefix;
  private String phoneNumber;
  private Boolean isPrimary;
  private Boolean canSendText;

  public Phone(
      String customerId,
      String type,
      String prefix,
      String phoneNumber,
      Boolean isPrimary,
      Boolean canSendText,
      Integer number) {
    this.customerId = customerId;
    this.type = type;
    this.prefix = prefix;
    this.phoneNumber = phoneNumber;
    this.isPrimary = isPrimary;
    this.canSendText = canSendText;
    this.number = number;
  }

  public Phone() {}

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

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public String getPrefix() {
    return prefix;
  }

  public void setPrefix(String prefix) {
    this.prefix = prefix;
  }

  public String getPhoneNumber() {
    return phoneNumber;
  }

  public void setPhoneNumber(String phoneNumber) {
    this.phoneNumber = phoneNumber;
  }

  public Boolean getIsPrimary() {
    return isPrimary;
  }

  public void setIsPrimary(Boolean isPrimary) {
    this.isPrimary = isPrimary;
  }

  public Boolean getCanSendText() {
    return canSendText;
  }

  public void setCanSendText(Boolean canSendText) {
    this.canSendText = canSendText;
  }
}
