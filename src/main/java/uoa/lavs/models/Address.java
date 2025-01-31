package uoa.lavs.models;

public class Address extends Detail {
  private String customerId;
  private Integer number;
  private String type;
  private String line1;
  private String line2;
  private String suburb;
  private String city;
  private String postCode;
  private String country;
  private Boolean isPrimary;
  private Boolean isMailing;

  public Address(
      String customerId,
      String type,
      String line1,
      String line2,
      String suburb,
      String city,
      String postCode,
      String country,
      Boolean isPrimary,
      Boolean isMailing,
      Integer number) {
    this.customerId = customerId;
    this.type = type;
    this.line1 = line1;
    this.line2 = line2;
    this.suburb = suburb;
    this.city = city;
    this.postCode = postCode;
    this.country = country;
    this.isPrimary = isPrimary;
    this.isMailing = isMailing;
    this.number = number;
  }

  public Address() {}

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

  public String getLine1() {
    return line1;
  }

  public void setLine1(String line1) {
    this.line1 = line1;
  }

  public String getLine2() {
    return line2;
  }

  public void setLine2(String line2) {
    this.line2 = line2;
  }

  public String getSuburb() {
    return suburb;
  }

  public void setSuburb(String suburb) {
    this.suburb = suburb;
  }

  public String getCity() {
    return city;
  }

  public void setCity(String city) {
    this.city = city;
  }

  public String getPostCode() {
    return postCode;
  }

  public void setPostCode(String postCode) {
    this.postCode = postCode;
  }

  public String getCountry() {
    return country;
  }

  public void setCountry(String country) {
    this.country = country;
  }

  public Boolean getIsPrimary() {
    return isPrimary;
  }

  public void setIsPrimary(boolean isPrimary) {
    this.isPrimary = isPrimary;
  }

  public Boolean getIsMailing() {
    return isMailing;
  }

  public void setIsMailing(boolean isMailing) {
    this.isMailing = isMailing;
  }

  public String getStreetAddress() {
    return getLine1() + " " + getLine2();
  }
}
