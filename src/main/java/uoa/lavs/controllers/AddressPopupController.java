package uoa.lavs.controllers;

import java.util.function.Consumer;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import uoa.lavs.models.Address;
import uoa.lavs.models.Detail;

public class AddressPopupController extends PopupController {
  @FXML private Pane addressPopupPane;
  @FXML private TextField addressLine1TextField;
  @FXML private TextField addressLine2TextField;
  @FXML private TextField suburbTextField;
  @FXML private TextField cityTextField;
  @FXML private TextField postcodeTextField;
  @FXML private TextField countryTextField;
  @FXML private ComboBox<String> addressTypeComboBox;
  @FXML private CheckBox isPrimaryAddressCheckBox;
  @FXML private CheckBox isMailingAddressCheckBox;

  private Address address;
  private Consumer<Address> addressSaveHandler;

  public void initialize() {
    setPane(addressPopupPane);
    alert.setHeaderText("Please fix the following issues: ");
  }

  @Override
  @FXML
  public void onClickSave(ActionEvent event) {
    if (isEmpty(addressLine1TextField)
        || isEmpty(suburbTextField)
        || isEmpty(cityTextField)
        || isEmpty(postcodeTextField)
        || isEmpty(countryTextField)
        || isEmpty(addressTypeComboBox)) {
      appendErrorMessage("Please fill in all required fields!\n");
    } else {
      if (!validatePostcodeFormat(postcodeTextField.getText())) {
        appendErrorMessage("Postcode must be numbers!\n");
      }

      if (isTooLong(addressTypeComboBox.getValue(), 20)) {
        appendErrorMessage("Address type must be less than 20 characters!\n");
      }
      if (isTooLong(addressLine1TextField.getText(), 60)) {
        appendErrorMessage("Address line 1 must be less than 60 characters!\n");
      }
      if (isTooLong(addressLine2TextField.getText(), 60)) {
        appendErrorMessage("Address line 2 must be less than 60 characters!\n");
      }
      if (isTooLong(suburbTextField.getText(), 30)) {
        appendErrorMessage("Suburb must be less than 30 characters!\n");
      }
      if (isTooLong(cityTextField.getText(), 30)) {
        appendErrorMessage("City must be less than 30 characters!\n");
      }
      if (isTooLong(postcodeTextField.getText(), 10)) {
        appendErrorMessage("Postcode must be less than 10 characters!\n");
      }
      if (isTooLong(countryTextField.getText(), 30)) {
        appendErrorMessage("Country must be less than 30 characters!\n");
      }
    }

    if (errorMessage.length() > 0) {
      showAlert();
      return;
    }

    // Update the Address object
    this.address.setLine1(addressLine1TextField.getText());
    this.address.setLine2(addressLine2TextField.getText());
    this.address.setSuburb(suburbTextField.getText());
    this.address.setCity(cityTextField.getText());
    this.address.setPostCode(postcodeTextField.getText());
    this.address.setCountry(countryTextField.getText());
    this.address.setType(addressTypeComboBox.getValue());
    this.address.setIsPrimary(isPrimaryAddressCheckBox.isSelected());
    this.address.setIsMailing(isMailingAddressCheckBox.isSelected());

    // Notify the original controller via the Consumer
    if (addressSaveHandler != null) {
      addressSaveHandler.accept(this.address);
    }
    closePopup();
  }

  @Override
  public void setUpPopup(Detail obj, Consumer<Detail> objectSaveHandler, boolean... args) {
    this.address = (Address) obj;
    this.addressSaveHandler = (Consumer<Address>) (Object) objectSaveHandler;
    addressLine1TextField.setText(address.getLine1());
    addressLine2TextField.setText(address.getLine2() == null ? "" : address.getLine2());
    suburbTextField.setText(address.getSuburb());
    cityTextField.setText(address.getCity());
    postcodeTextField.setText(address.getPostCode());
    countryTextField.setText(address.getCountry());
    addressTypeComboBox.setValue(address.getType());
    isPrimaryAddressCheckBox.setDisable(
        (args.length > 0 ? args[0] : false) && !address.getIsPrimary());
    isPrimaryAddressCheckBox.setSelected(address.getIsPrimary());
    isMailingAddressCheckBox.setDisable(
        (args.length > 1 ? args[1] : false) && !address.getIsMailing());
    isMailingAddressCheckBox.setSelected(address.getIsMailing());
  }

  private boolean validatePostcodeFormat(String postcode) {
    try {
      Integer.parseInt(postcode);
    } catch (Exception e) {
      return false;
    }
    return true;
  }
}
