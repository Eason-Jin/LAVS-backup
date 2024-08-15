package uoa.lavs.controllers;

import java.io.IOException;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Control;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import uoa.lavs.Main;
import uoa.lavs.SceneManager;
import uoa.lavs.dataoperations.AddressUpdater;
import uoa.lavs.dataoperations.CustomerUpdater;
import uoa.lavs.dataoperations.EmailUpdater;
import uoa.lavs.dataoperations.EmployerUpdater;
import uoa.lavs.dataoperations.PhoneUpdater;
import uoa.lavs.models.Address;
import uoa.lavs.models.Customer;
import uoa.lavs.models.Email;
import uoa.lavs.models.Employer;
import uoa.lavs.models.Phone;

public class AddCustomerController {

  @FXML private Button homeButton;
  @FXML private Button saveButton;
  @FXML private Button cancelButton;
  @FXML private Button infoButton;

  @FXML private TextField titleField;
  @FXML private TextField familyNameField;
  @FXML private TextField givenNameField;
  @FXML private DatePicker dobPicker;

  @FXML private TextField citizenshipField;
  @FXML private TextField visaField;

  @FXML private TextField addressTypeField;
  @FXML private TextField address1Field;
  @FXML private TextField address2Field;
  @FXML private TextField suburbField;
  @FXML private TextField cityField;
  @FXML private TextField postcodeField;
  @FXML private TextField countryField;
  @FXML private CheckBox isPrimaryAddress;
  @FXML private CheckBox isMailingAddress;

  @FXML private TextField emailField;
  @FXML private CheckBox isPrimaryEmail;
  @FXML private ComboBox<FXCollections> phoneTypeBox;
  @FXML private TextField prefixField;
  @FXML private TextField numberField;
  @FXML private CheckBox isPrimaryNumber;
  @FXML private CheckBox isTextingNumber;

  @FXML private TextField jobField;
  @FXML private TextField companyNameField;
  @FXML private TextField companyAddress1Field;
  @FXML private TextField companyAddress2Field;
  @FXML private TextField companySuburbField;
  @FXML private TextField companyCityField;
  @FXML private TextField companyPostcodeField;
  @FXML private TextField companyCountryField;
  @FXML private TextField employerPhoneField;
  @FXML private TextField employerEmailField;
  @FXML private TextField companyWebsiteField;
  @FXML private CheckBox isOwner;

  @FXML private TextArea notesArea;

  @FXML
  private void onClickHome(ActionEvent event) throws IOException {
    Main.setScene(SceneManager.AppScene.START);
  }

  @FXML
  private void onClickSave(ActionEvent event) {
    if (checkFields()) {
      Customer customer =
          new Customer(
              null,
              titleField.getText(),
              givenNameField.getText() + " " + familyNameField.getText(),
              dobPicker.getValue(),
              jobField.getText(),
              citizenshipField.getText(),
              visaField.getText(),
              "Active",
              notesArea.getText());
      CustomerUpdater.updateData(null, customer);

      String customerID = customer.getId();

      Address address =
          new Address(
              customerID,
              addressTypeField.getText(),
              address1Field.getText(),
              address2Field.getText(),
              suburbField.getText(),
              cityField.getText(),
              postcodeField.getText(),
              countryField.getText(),
              isPrimaryAddress.isSelected(),
              isMailingAddress.isSelected());
      AddressUpdater.updateData(customerID, address);

      Email email = new Email(customerID, emailField.getText(), isPrimaryEmail.isSelected());
      EmailUpdater.updateData(customerID, email);

      Phone phone =
          new Phone(
              customerID,
              (String)(Object)phoneTypeBox.getValue(),  // Some wild casting here but it works :)
              prefixField.getText(),
              numberField.getText(),
              isPrimaryNumber.isSelected(),
              isTextingNumber.isSelected());
      PhoneUpdater.updateData(customerID, phone);

      Employer employer =
          new Employer(
              customerID,
              companyNameField.getText(),
              companyAddress1Field.getText(),
              companyAddress2Field.getText(),
              companySuburbField.getText(),
              companyCityField.getText(),
              companyPostcodeField.getText(),
              companyCountryField.getText(),
              employerPhoneField.getText(),
              employerEmailField.getText(),
              companyWebsiteField.getText(),
              isOwner.isSelected());
      EmployerUpdater.updateData(customerID, employer);
    }
  }

  @FXML
  private void onClickCancel(ActionEvent event) {
    Alert alert = new Alert(AlertType.CONFIRMATION);
    alert.setTitle("Cancel adding customer");
    alert.setHeaderText("If you cancel, all progress will be lost.");
    alert.setContentText("Are you sure you want to cancel?");
    if (alert.showAndWait().get() == ButtonType.OK) {
      clearAllFields();
      Main.setScene(SceneManager.AppScene.START);
    }
  }

  @FXML
  private void onClickInfo(ActionEvent event) {
    System.out.println("Info button clicked");
  }

  private void clearAllFields() {
    titleField.clear();
    familyNameField.clear();
    givenNameField.clear();
    dobPicker.setValue(null);
    jobField.clear();
    citizenshipField.clear();
    visaField.clear();
    addressTypeField.clear();
    address1Field.clear();
    address2Field.clear();
    suburbField.clear();
    cityField.clear();
    postcodeField.clear();
    countryField.clear();
    isPrimaryAddress.setSelected(false);
    isMailingAddress.setSelected(false);
    emailField.clear();
    isPrimaryEmail.setSelected(false);
    phoneTypeBox.setValue(null);
    prefixField.clear();
    numberField.clear();
    isPrimaryNumber.setSelected(false);
    isTextingNumber.setSelected(false);
    jobField.clear();
    companyNameField.clear();
    companyAddress1Field.clear();
    companyAddress2Field.clear();
    companySuburbField.clear();
    companyCityField.clear();
    companyPostcodeField.clear();
    companyCountryField.clear();
    employerPhoneField.clear();
    employerEmailField.clear();
    companyWebsiteField.clear();
    isOwner.setSelected(false);
    notesArea.clear();
  }

  private boolean checkFields() {
    boolean titleFieldFlag = checkField(titleField);
    boolean familyNameFieldFlag = checkField(familyNameField);
    boolean givenNameFieldFlag = checkField(givenNameField);
    boolean dobPickerFlag = checkField(dobPicker);
    boolean citizenshipFieldFlag = checkField(citizenshipField);
    boolean visaFieldFlag = checkField(visaField);
    boolean address1FieldFlag = checkField(address1Field);
    boolean suburbFieldFlag = checkField(suburbField);
    boolean cityFieldFlag = checkField(cityField);
    boolean postcodeFieldFlag = checkField(postcodeField);
    boolean countryFieldFlag = checkField(countryField);
    boolean emailFieldFlag = checkField(emailField);
    boolean phoneTypeBoxFlag = checkField(phoneTypeBox);
    boolean prefixFieldFlag = checkField(prefixField);
    boolean numberFieldFlag = checkField(numberField);
    boolean jobFieldFlag = checkField(jobField);
    boolean companyNameFieldFlag = checkField(companyNameField);
    boolean companyAddress1FieldFlag = checkField(companyAddress1Field);
    boolean companySuburbFieldFlag = checkField(companySuburbField);
    boolean companyCityFieldFlag = checkField(companyCityField);
    boolean companyPostcodeFieldFlag = checkField(companyPostcodeField);
    boolean companyCountryFieldFlag = checkField(companyCountryField);
    boolean employerPhoneFieldFlag = checkField(employerPhoneField);
    boolean employerEmailFieldFlag = checkField(employerEmailField);
    boolean companyWebsiteFieldFlag = checkField(companyWebsiteField);

    // Only address line 2 can be empty
    if (titleFieldFlag
        && familyNameFieldFlag
        && givenNameFieldFlag
        && dobPickerFlag
        && citizenshipFieldFlag
        && visaFieldFlag
        && address1FieldFlag
        && suburbFieldFlag
        && cityFieldFlag
        && postcodeFieldFlag
        && countryFieldFlag
        && emailFieldFlag
        && phoneTypeBoxFlag
        && prefixFieldFlag
        && numberFieldFlag
        && jobFieldFlag
        && companyNameFieldFlag
        && companyAddress1FieldFlag
        && companySuburbFieldFlag
        && companyCityFieldFlag
        && companyPostcodeFieldFlag
        && companyCountryFieldFlag
        && employerPhoneFieldFlag
        && employerEmailFieldFlag
        && companyWebsiteFieldFlag) {
      return true;
    }
    Alert alert = new Alert(AlertType.ERROR);
    alert.setTitle("Error");
    alert.setHeaderText("Please fill in all required fields");
    alert.showAndWait();

    return false;
  }

  private boolean checkField(Control ui) {
    ui.setStyle("-fx-border-color: none");
    if (ui instanceof TextField) {
      TextField tf = (TextField) ui;
      if (tf.getText().isEmpty()) {
        tf.setStyle("-fx-border-color: red");
        return false;
      }
    }
    if (ui instanceof ComboBox) {
      ComboBox<FXCollections> cb = (ComboBox<FXCollections>) ui;
      if (cb.getValue() == null) {
        cb.setStyle("-fx-border-color: red");
        return false;
      }
    }
    if (ui instanceof DatePicker) {
      DatePicker dp = (DatePicker) ui;
      if (dp.getValue() == null) {
        dp.setStyle("-fx-border-color: red");
        return false;
      }
    }
    return true;
  }
}
