package uoa.lavs.controllers;

import java.util.function.Consumer;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import uoa.lavs.models.Detail;
import uoa.lavs.models.Employer;

public class EmploymentPopupController extends PopupController {
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

  public void initialize() {
    setPane(employmentPopupPane);
  }

  @Override
  @FXML
  public void onClickSave(ActionEvent event) {
    if (isEmpty(companyNameTextField)
        || isEmpty(companyAddressLine1TextField)
        || isEmpty(companySuburbTextField)
        || isEmpty(companyCityTextField)
        || isEmpty(companyPostcodeTextField)
        || isEmpty(companyCountryTextField)
        || isEmpty(companyPhoneTextField)
        || isEmpty(companyEmailTextField)
        || isEmpty(companyWebsiteTextField)) {
      appendErrorMessage("Please fill in all required fields!\n");
    } else {
      if (!validateNumberFormat(companyPostcodeTextField.getText())) {
        appendErrorMessage("Postcode must be numbers!\n");
      }
      if (!validateEmailFormat(companyEmailTextField.getText())) {
        appendErrorMessage("Email must be in the format of a@b.c!\n");
      }
      if (!validateNumberFormat(companyPhoneTextField.getText())) {
        appendErrorMessage("Phone number must be numbers!\n");
      }
      if (!validateWebsiteFormat(companyWebsiteTextField.getText())) {
        appendErrorMessage("Website must be in the format of a.b!\n");
      }

      if (isTooLong(companyNameTextField.getText(), 60)) {
        appendErrorMessage("Company name must be less than 60 characters!\n");
      }
      if (isTooLong(companyAddressLine1TextField.getText(), 60)) {
        appendErrorMessage("Address line 1 must be less than 60 characters!\n");
      }
      if (isTooLong(companyAddressLine2TextField.getText(), 60)) {
        appendErrorMessage("Address line 2 must be less than 60 characters!\n");
      }
      if (isTooLong(companySuburbTextField.getText(), 30)) {
        appendErrorMessage("Suburb must be less than 30 characters!\n");
      }
      if (isTooLong(companyCityTextField.getText(), 30)) {
        appendErrorMessage("City must be less than 30 characters!\n");
      }
      if (isTooLong(companyPostcodeTextField.getText(), 10)) {
        appendErrorMessage("Postcode must be less than 10 characters!\n");
      }
      if (isTooLong(companyCountryTextField.getText(), 30)) {
        appendErrorMessage("Country must be less than 30 characters!\n");
      }
      if (isTooLong(companyEmailTextField.getText(), 60)) {
        appendErrorMessage("Email must be less than 60 characters!\n");
      }
      if (isTooLong(companyWebsiteTextField.getText(), 60)) {
        appendErrorMessage("Website must be less than 60 characters!\n");
      }
    }

    if (errorMessage.length() > 0) {
      showAlert();
      return;
    }

    // Update the Employer object
    this.employment.setName(companyNameTextField.getText());
    this.employment.setLine1(companyAddressLine1TextField.getText());
    this.employment.setLine2(companyAddressLine2TextField.getText());
    this.employment.setSuburb(companySuburbTextField.getText());
    this.employment.setCity(companyCityTextField.getText());
    this.employment.setPostCode(companyPostcodeTextField.getText());
    this.employment.setCountry(companyCountryTextField.getText());
    this.employment.setPhoneNumber(companyPhoneTextField.getText());
    this.employment.setEmailAddress(companyEmailTextField.getText());
    this.employment.setWebsite(companyWebsiteTextField.getText());
    this.employment.setIsOwner(isOwnerCheckBox.isSelected());

    if (employmentSaveHandler != null) {
      employmentSaveHandler.accept(this.employment);
    }

    closePopup();
  }

  @Override
  public void setUpPopup(Detail obj, Consumer<Detail> objectSaveHandler, boolean... args) {

    this.employment = (Employer) obj;
    this.employmentSaveHandler = (Consumer<Employer>) (Object) objectSaveHandler;

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
}
