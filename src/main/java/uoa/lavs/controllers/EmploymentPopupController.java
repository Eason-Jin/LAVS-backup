package uoa.lavs.controllers;

import java.util.function.Consumer;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import uoa.lavs.models.Employer;

public class EmploymentPopupController {
  @FXML private Pane employmentPopupPane;
  @FXML private TextField companyNameTextField;
  @FXML private TextField companyAddressLine1TextField;
  @FXML private TextField companyAddressLine2TextField;
  @FXML private TextField companySuburbTextField;
  @FXML private TextField companyCityTextField;
  @FXML private TextField companyPostcodeTextField;
  @FXML private TextField companyCountryTextField;
  @FXML private TextField companyPhoneTextField;
  @FXML private TextField companyEmailTextField;
  @FXML private TextField companyWebsiteTextField;
  @FXML private CheckBox isOwnerCheckBox;

  private Employer employment;
  private Consumer<Employer> employmentSaveHandler;

  public void initialize() {}

  @FXML
  private void onClickCloseEmploymentPopup(ActionEvent event) {
    closePopup();
  }

  @FXML
  private void onClickSaveEmployment(ActionEvent event) {
    String companyName = companyNameTextField.getText();
    String companyAddressLine1 = companyAddressLine1TextField.getText();
    String companyAddressLine2 = companyAddressLine2TextField.getText();
    String companySuburb = companySuburbTextField.getText();
    String companyCity = companyCityTextField.getText();
    String companyPostcode = companyPostcodeTextField.getText();
    String companyCountry = companyCountryTextField.getText();
    String companyPhone = companyPhoneTextField.getText();
    String companyEmail = companyEmailTextField.getText();
    String companyWebsite = companyWebsiteTextField.getText();

    if (companyName == null
        || companyAddressLine1 == null
        || companySuburb == null
        || companyCity == null
        || companyPostcode == null
        || companyCountry == null
        || companyPhone == null
        || companyEmail == null
        || companyWebsite == null) {
      return;
    }

    // if (!validatePostcodeFormat(companyPostcode)) {
    //   Alert alert = new Alert(Alert.AlertType.ERROR);
    //   alert.setTitle("Invalid Postcode");
    //   alert.setHeaderText("The postcode format is invalid.");
    //   alert.setContentText("Please enter a valid postcode.");
    //   alert.showAndWait();
    //   return;
    // }

    // Update the Employer object
    this.employment.setName(companyName);
    this.employment.setLine1(companyAddressLine1);
    this.employment.setLine2(companyAddressLine2);
    this.employment.setSuburb(companySuburb);
    this.employment.setCity(companyCity);
    this.employment.setPostCode(companyPostcode);
    this.employment.setCountry(companyCountry);
    this.employment.setPhoneNumber(companyWebsite);
    this.employment.setEmailAddress(companyWebsite);
    this.employment.setWebsite(companyWebsite);
    this.employment.setIsOwner(isOwnerCheckBox.isSelected());

    if (employmentSaveHandler != null) {
      employmentSaveHandler.accept(this.employment);
    }

    closePopup();
  }

  public void setUpEmploymentPopup(Employer employment, Consumer<Employer> employmentSaveHandler) {
    this.employment = employment;
    this.employmentSaveHandler = employmentSaveHandler;

    companyNameTextField.setText(employment.getName());
    companyAddressLine1TextField.setText(employment.getLine1());
    companyAddressLine2TextField.setText(
        employment.getLine2() == null ? "" : employment.getLine2());
    companySuburbTextField.setText(employment.getSuburb());
    companyCityTextField.setText(employment.getCity());
    companyPostcodeTextField.setText(employment.getPostCode());
    companyCountryTextField.setText(employment.getCountry());
    companyPhoneTextField.setText(employment.getPhoneNumber());
    companyEmailTextField.setText(employment.getEmailAddress());
    companyWebsiteTextField.setText(employment.getWebsite());
    isOwnerCheckBox.setSelected(employment.getIsOwner() == null ? false : employment.getIsOwner());
  }

  private boolean validatePostcodeFormat(String postcode) {
    try {
      Integer.parseInt(postcode);
    } catch (Exception e) {
      return false;
    }
    return true;
  }

  private boolean validateWebsiteFormat(String website) {
    return website.matches("^.+\\..+$");
  }

  private boolean validateEmailFormat(String email) {
    return email.matches("^.+@.+\\..+$");
  }

  private boolean validatePhoneFormat(String phone) {
    try {
      Integer.parseInt(phone);
    } catch (Exception e) {
      return false;
    }
    return true;
  }

  private void closePopup() {
    Pane currentRoot = (Pane) employmentPopupPane.getScene().getRoot();
    currentRoot.getChildren().remove(employmentPopupPane);
  }
}
