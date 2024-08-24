package uoa.lavs.controllers;

import java.util.function.Consumer;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import uoa.lavs.models.Phone;

public class PhonePopupController {
  @FXML private Pane phonePopupPane;
  @FXML private ComboBox<String> phoneTypeComboBox;
  @FXML private TextField prefixTextField;
  @FXML private TextField phoneTextField;
  @FXML private CheckBox isPrimaryPhoneCheckBox;
  @FXML private CheckBox sendTextsCheckBox;

  private Phone phone;
  private Consumer<Phone> phoneSaveHandler;

  public void initialize() {}

  @FXML
  private void onClickCloseAddPhone() {
    closePopup();
  }

  @FXML
  private void onClickSavePhone() {
    String phoneText = phoneTextField.getText();

    if (phoneText == null) {
      return;
    }

    if (!validatePhoneFormat(phoneText)) {
      Alert alert = new Alert(Alert.AlertType.ERROR);
      alert.setTitle("Invalid Phone");
      alert.setHeaderText("The phone must be numbers.");
      alert.setContentText("Please enter a valid phone number.");
      alert.showAndWait();
      return;
    }

    // Update the Phone object
    this.phone.setType(phoneTypeComboBox.getValue());
    this.phone.setPrefix(prefixTextField.getText());
    this.phone.setPhoneNumber(phoneText);
    this.phone.setIsPrimary(isPrimaryPhoneCheckBox.isSelected());
    this.phone.setCanSendText(sendTextsCheckBox.isSelected());

    // Notify the original controller via the Consumer
    if (phoneSaveHandler != null) {
      phoneSaveHandler.accept(this.phone);
    }
    closePopup();
  }

  public void setUpPhonePopup(
      Phone phone, boolean isPrimaryExists, Consumer<Phone> phoneSaveHandler) {
    this.phone = phone;
    this.phoneSaveHandler = phoneSaveHandler;
    phoneTypeComboBox.setValue(phone.getType());
    ;
    prefixTextField.setText(phone.getPrefix());
    phoneTextField.setText(phone.getPhoneNumber());
    isPrimaryPhoneCheckBox.setDisable(isPrimaryExists && !phone.getIsPrimary());
    isPrimaryPhoneCheckBox.setSelected(isPrimaryExists);
    sendTextsCheckBox.setSelected(phone.getCanSendText());
  }

  private boolean validatePhoneFormat(String phone) {
    boolean isValid = true;
    try {
      Integer.parseInt(phone);
    } catch (Exception e) {
      isValid = false;
    }
    return isValid;
  }

  private void closePopup() {
    Pane currentRoot = (Pane) phonePopupPane.getScene().getRoot();
    currentRoot.getChildren().remove(phonePopupPane);
  }
}
