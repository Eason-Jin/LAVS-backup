package uoa.lavs.models;

public class Employer extends Detail {
  private String customerId;
  private Integer number;
  private String name;
  private String line1;
  private String line2;
  private String suburb;
  private String city;
  private String postCode;
  private String country;
  private String phoneNumber;
  private String emailAddress;
  private String website;
  private Boolean isOwner;

  public Employer(
      String customerId,
      String name,
      String line1,
      String line2,
      String suburb,
      String city,
      String postCode,
      String country,
      String phoneNumber,
      String emailAddress,
      String website,
      Boolean isOwner,
      Integer number) {
    this.customerId = customerId;
    this.name = name;
    this.line1 = line1;
    this.line2 = line2;
    this.suburb = suburb;
    this.city = city;
    this.postCode = postCode;
    this.country = country;
    this.phoneNumber = phoneNumber;
    this.emailAddress = emailAddress;
    this.website = website;
    this.isOwner = isOwner;
    this.number = number;
  }

  public Employer() {}

  // Getters and Setters
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

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
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

  public String getPhoneNumber() {
    return phoneNumber;
  }

  public void setPhoneNumber(String phoneNumber) {
    this.phoneNumber = phoneNumber;
  }

  public String getEmailAddress() {
    return emailAddress;
  }

  public void setEmailAddress(String emailAddress) {
    this.emailAddress = emailAddress;
  }

  public String getWebsite() {
    return website;
  }

  public void setWebsite(String website) {
    this.website = website;
  }

  public Boolean getIsOwner() {
    return isOwner;
  }

  public void setIsOwner(Boolean isOwner) {
    this.isOwner = isOwner;
  }

  public String getStreetAddress() {
    return getLine1() + " " + getLine2();
  }
}
