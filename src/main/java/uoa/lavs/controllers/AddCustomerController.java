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
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import uoa.lavs.Main;
import uoa.lavs.SceneManager;
import uoa.lavs.dataoperations.CustomerUpdater;
import uoa.lavs.models.Customer;

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

  @FXML
  private void onClickHome(ActionEvent event) throws IOException {
    Main.setScene(SceneManager.AppScene.START);
  }

  @FXML
  private void onClickSave(ActionEvent event) {
    // Save the form to the mainframe
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
              "Active");
      CustomerUpdater updater = new CustomerUpdater();
      updater.updateData(null, customer);
    // TODO: add other objects
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
  }

  private boolean checkFields() {
    // Only address line 2 can be empty
    if (titleField.getText().isEmpty()
        || familyNameField.getText().isEmpty()
        || givenNameField.getText().isEmpty()
        || dobPicker.getValue() == null
        || citizenshipField.getText().isEmpty()
        || visaField.getText().isEmpty()
        || address1Field.getText().isEmpty()
        || suburbField.getText().isEmpty()
        || cityField.getText().isEmpty()
        || postcodeField.getText().isEmpty()
        || countryField.getText().isEmpty()
        || emailField.getText().isEmpty()
        || phoneTypeBox.getValue() == null
        || prefixField.getText().isEmpty()
        || numberField.getText().isEmpty()
        || jobField.getText().isEmpty()
        || companyNameField.getText().isEmpty()
        || companyAddress1Field.getText().isEmpty()
        || companySuburbField.getText().isEmpty()
        || companyCityField.getText().isEmpty()
        || companyPostcodeField.getText().isEmpty()
        || companyCountryField.getText().isEmpty()
        || employerPhoneField.getText().isEmpty()
        || employerEmailField.getText().isEmpty()
        || companyWebsiteField.getText().isEmpty()) {
      Alert alert = new Alert(AlertType.ERROR);
      alert.setTitle("Error");
      alert.setHeaderText("Please fill in all required fields");
      alert.showAndWait();
      return false;
    }
    return true;
  }
}
