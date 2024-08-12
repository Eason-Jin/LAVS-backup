package uoa.lavs.models;

import java.time.LocalDate;

public class Customer {
  private String id;
  private String title;
  private String name;
  private LocalDate dob;
  private String occupation;
  private String citizenship;
  private String visaType;
  private String status;

  public Customer(
      String id,
      String title,
      String name,
      LocalDate dob,
      String occupation,
      String citizenship,
      String visaType,
      String status) {
    this.id = id;
    this.title = title;
    this.name = name;
    this.dob = dob;
    this.occupation = occupation;
    this.citizenship = citizenship;
    this.visaType = visaType;
    this.status = status;
  }

  public Customer() {}

  // Getters and setters
  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public LocalDate getDob() {
    return dob;
  }

  public void setDob(LocalDate dob) {
    this.dob = dob;
  }

  public String getOccupation() {
    return occupation;
  }

  public void setOccupation(String occupation) {
    this.occupation = occupation;
  }

  public String getCitizenship() {
    return citizenship;
  }

  public void setCitizenship(String citizenship) {
    this.citizenship = citizenship;
  }

  public String getVisaType() {
    return visaType;
  }

  public void setVisaType(String visaType) {
    this.visaType = visaType;
  }

  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }
}
