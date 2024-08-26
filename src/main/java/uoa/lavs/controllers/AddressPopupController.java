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
    boolean addressLine1Flag = isEmpty(addressLine1TextField);
    boolean suburbFlag = isEmpty(suburbTextField);
    boolean cityFlag = isEmpty(cityTextField);
    boolean postcodeFlag = isEmpty(postcodeTextField);
    boolean countryFlag = isEmpty(countryTextField);
    boolean addressTypeFlag = isEmpty(addressTypeComboBox);
    if (addressLine1Flag || suburbFlag || cityFlag || postcodeFlag || countryFlag || addressTypeFlag) {
      appendErrorMessage("Please fill in all required fields!\n");
    } else {
      boolean addressTypeLongFlag = isTooLong(addressTypeComboBox, 20);
      if (addressTypeLongFlag) {
        appendErrorMessage("Address type must be less than 20 characters!\n");
      }
      boolean addressLine1LongFlag = isTooLong(addressLine1TextField, 60);
      if (addressLine1LongFlag) {
        appendErrorMessage("Address line 1 must be less than 60 characters!\n");
      }
      boolean addressLine2LongFlag = isTooLong(addressLine2TextField, 60);
      if (addressLine2LongFlag) {
        appendErrorMessage("Address line 2 must be less than 60 characters!\n");
      }
      boolean suburbLongFlag = isTooLong(suburbTextField, 30);
      if (suburbLongFlag) {
        appendErrorMessage("Suburb must be less than 30 characters!\n");
      }
      boolean cityLongFlag = isTooLong(cityTextField, 30);
      if (cityLongFlag) {
        appendErrorMessage("City must be less than 30 characters!\n");
      }
      boolean postcodeLongFlag = isTooLong(postcodeTextField, 10);
      if (postcodeLongFlag) {
        appendErrorMessage("Postcode must be less than 10 characters!\n");
      }
      boolean countryLongFlag = isTooLong(countryTextField, 30);
      if (countryLongFlag) {
        appendErrorMessage("Country must be less than 30 characters!\n");
      }
      if (!(addressLine1Flag
          || suburbFlag
          || cityFlag
          || postcodeFlag
          || countryFlag
          || addressTypeFlag
          || addressTypeLongFlag
          || addressLine1LongFlag
          || addressLine2LongFlag
          || suburbLongFlag
          || cityLongFlag
          || postcodeLongFlag
          || countryLongFlag)) {
        if (!validateNumberFormat(postcodeTextField, false)) {
          appendErrorMessage("Postcode must be numbers!\n");
        }
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
  public void setUpPopup(Detail obj, Consumer<Detail> objectSaveHandler, Boolean isPrimary) {
    this.address = (Address) obj;
    this.addressSaveHandler = (Consumer<Address>) (Object) objectSaveHandler;
    if (address.getIsPrimary() == null) {
      address.setIsPrimary(false);
    }
    if (address.getIsMailing() == null) {
      address.setIsMailing(false);
    }
    addressLine1TextField.setText(address.getLine1());
    addressLine2TextField.setText(address.getLine2() == null ? "" : address.getLine2());
    suburbTextField.setText(address.getSuburb());
    cityTextField.setText(address.getCity());
    postcodeTextField.setText(address.getPostCode());
    countryTextField.setText(address.getCountry());
    addressTypeComboBox.setValue(address.getType());
    isPrimaryAddressCheckBox.setDisable(
        isPrimary && !address.getIsPrimary());
    isPrimaryAddressCheckBox.setSelected(address.getIsPrimary());
    isMailingAddressCheckBox.setSelected(address.getIsMailing());
  }
}
