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
    boolean companyNameFlag = isEmpty(companyNameTextField);
    boolean companyAddressLine1Flag = isEmpty(companyAddressLine1TextField);
    boolean companySuburbFlag = isEmpty(companySuburbTextField);
    boolean companyCityFlag = isEmpty(companyCityTextField);
    boolean companyPostcodeFlag = isEmpty(companyPostcodeTextField);
    boolean companyCountryFlag = isEmpty(companyCountryTextField);
    boolean companyPhoneFlag = isEmpty(companyPhoneTextField);
    boolean companyEmailFlag = isEmpty(companyEmailTextField);
    boolean companyWebsiteFlag = isEmpty(companyWebsiteTextField);
    if (companyNameFlag
        || companyAddressLine1Flag
        || companySuburbFlag
        || companyCityFlag
        || companyPostcodeFlag
        || companyCountryFlag
        || companyPhoneFlag
        || companyEmailFlag
        || companyWebsiteFlag) {
      appendErrorMessage("Please fill in all required fields!\n");
    } else {
      boolean companyNameLongFlag = isTooLong(companyNameTextField, 60);
      if (companyNameLongFlag) {
        appendErrorMessage("Company name must be less than 60 characters!\n");
      }
      boolean companyAddressLine1LongFlag = isTooLong(companyAddressLine1TextField, 60);
      if (companyAddressLine1LongFlag) {
        appendErrorMessage("Address line 1 must be less than 60 characters!\n");
      }
      boolean companyAddressLine2LongFlag = isTooLong(companyAddressLine2TextField, 60);
      if (companyAddressLine2LongFlag) {
        appendErrorMessage("Address line 2 must be less than 60 characters!\n");
      }
      boolean companySuburbLongFlag = isTooLong(companySuburbTextField, 30);
      if (companySuburbLongFlag) {
        appendErrorMessage("Suburb must be less than 30 characters!\n");
      }
      boolean companyCityLongFlag = isTooLong(companyCityTextField, 30);
      if (companyCityLongFlag) {
        appendErrorMessage("City must be less than 30 characters!\n");
      }
      boolean companyPostcodeLongFlag = isTooLong(companyPostcodeTextField, 10);
      if (companyPostcodeLongFlag) {
        appendErrorMessage("Postcode must be less than 10 characters!\n");
      }
      boolean companyCountryLongFlag = isTooLong(companyCountryTextField, 30);
      if (companyCountryLongFlag) {
        appendErrorMessage("Country must be less than 30 characters!\n");
      }
      boolean companyPhoneLongFlag = isTooLong(companyPhoneTextField, 20);
      if (companyPhoneLongFlag) {
        appendErrorMessage("Phone number must be less than 30 characters!\n");
      }
      boolean companyEmailLongFlag = isTooLong(companyEmailTextField, 60);
      if (companyEmailLongFlag) {
        appendErrorMessage("Email must be less than 60 characters!\n");
      }
      boolean companyWebsiteLongFlag = isTooLong(companyWebsiteTextField, 20);
      if (companyWebsiteLongFlag) {
        appendErrorMessage("Website must be less than 60 characters!\n");
      }
      if (!(companyNameLongFlag
          || companyAddressLine1LongFlag
          || companyAddressLine2LongFlag
          || companySuburbLongFlag
          || companyCityLongFlag
          || companyPostcodeLongFlag
          || companyCountryLongFlag
          || companyPhoneLongFlag
          || companyEmailLongFlag
          || companyWebsiteLongFlag)) {
        if (!validateNumberFormat(companyPostcodeTextField)) {
          appendErrorMessage("Postcode must be numbers!\n");
        }
        if (!validateEmailFormat(companyEmailTextField)) {
          appendErrorMessage("Email must be in the format of a@b.c!\n");
        }
        if (!validateNumberFormat(companyPhoneTextField)) {
          appendErrorMessage("Phone number must be numbers!\n");
        }
        if (!validateWebsiteFormat(companyWebsiteTextField)) {
          appendErrorMessage("Website must be in the format of a.b!\n");
        }
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
  public void setUpPopup(Detail obj, Consumer<Detail> objectSaveHandler, Boolean isPrimary) {

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
    isOwnerCheckBox.setSelected(employment.getIsOwner());
  }
}
