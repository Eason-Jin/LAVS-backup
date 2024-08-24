package uoa.lavs.controllers;

import java.util.function.Consumer;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
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
  }

  @Override
  @FXML
  public void onClickSave(ActionEvent event) {
    String addressLine1 = addressLine1TextField.getText();
    String addressLine2 = addressLine2TextField.getText();
    String suburb = suburbTextField.getText();
    String city = cityTextField.getText();
    String postcode = postcodeTextField.getText();
    String country = countryTextField.getText();
    String addressType = addressTypeComboBox.getValue();

    if (addressLine1 == null
        || suburb == null
        || city == null
        || postcode == null
        || country == null) {
      return;
    }

    if (!validatePostcodeFormat(postcode)) {
      Alert alert = new Alert(Alert.AlertType.ERROR);
      alert.setTitle("Invalid Postcode");
      alert.setHeaderText("The postcode format is invalid.");
      alert.setContentText("Please enter a valid postcode.");
      alert.showAndWait();
      return;
    }

    // Update the Address object
    this.address.setLine1(addressLine1);
    this.address.setLine2(addressLine2);
    this.address.setSuburb(suburb);
    this.address.setCity(city);
    this.address.setPostCode(postcode);
    this.address.setCountry(country);
    this.address.setType(addressType);
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
