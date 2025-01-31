package uoa.lavs.controllers;

import java.util.function.Consumer;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import uoa.lavs.models.Detail;
import uoa.lavs.models.Phone;

public class PhonePopupController extends PopupController {
  @FXML private Pane phonePopupPane;
  @FXML private ComboBox<String> phoneTypeComboBox;
  @FXML private TextField prefixTextField;
  @FXML private TextField phoneTextField;
  @FXML private CheckBox isPrimaryPhoneCheckBox;
  @FXML private CheckBox sendTextsCheckBox;

  private Phone phone;
  private Consumer<Phone> phoneSaveHandler;

  public void initialize() {
    setPane(phonePopupPane);
  }

  @Override
  @FXML
  public void onClickSave(ActionEvent event) {
    boolean phoneTypeFlag = isEmpty(phoneTypeComboBox);
    boolean prefixFlag = isEmpty(prefixTextField);
    boolean phoneFlag = isEmpty(phoneTextField);
    if (phoneTypeFlag || prefixFlag || phoneFlag) {
      appendErrorMessage("Please fill in all required fields!\n");
    } else {
      boolean prefixLongFlag = isTooLong(prefixTextField, 10);
      if (prefixLongFlag) {
        appendErrorMessage("Prefix must be less than 10 characters!\n");
      }
      boolean phoneLongFlag = isTooLong(phoneTextField, 20);
      if (phoneLongFlag) {
        appendErrorMessage("Phone number must be less than 20 characters!\n");
      }
      if (!(prefixLongFlag || phoneLongFlag)) {
        if (!validatePhoneFormat(prefixTextField, true)) {
          appendErrorMessage("Prefix must be numbers or start with the '+' character!\n");
        }
        if (!validatePhoneFormat(phoneTextField, false)) {
          appendErrorMessage("Phone number must be numbers or '-' characters!\n");
        }
      }
    }

    if (errorMessage.length() > 0) {
      showAlert();
      return;
    }

    // Update the Phone object
    this.phone.setType(phoneTypeComboBox.getValue());
    this.phone.setPrefix(prefixTextField.getText());
    this.phone.setPhoneNumber(phoneTextField.getText());
    this.phone.setIsPrimary(isPrimaryPhoneCheckBox.isSelected());
    this.phone.setCanSendText(sendTextsCheckBox.isSelected());

    // Notify the original controller via the Consumer
    if (phoneSaveHandler != null) {
      phoneSaveHandler.accept(this.phone);
    }
    closePopup();
  }

  @Override
  public void setUpPopup(Detail obj, Consumer<Detail> objectSaveHandler, Boolean isPrimary) {
    this.phone = (Phone) obj;
    this.phoneSaveHandler = (Consumer<Phone>) (Object) objectSaveHandler;
    if (phone.getIsPrimary() == null) {
      phone.setIsPrimary(false);
    }
    if (phone.getCanSendText() == null) {
      phone.setCanSendText(false);
    }
    phoneTypeComboBox.setValue(phone.getType());
    prefixTextField.setText(phone.getPrefix());
    phoneTextField.setText(phone.getPhoneNumber());
    isPrimaryPhoneCheckBox.setDisable(isPrimary && !phone.getIsPrimary());
    isPrimaryPhoneCheckBox.setSelected(phone.getIsPrimary());
    sendTextsCheckBox.setSelected(phone.getCanSendText());
  }
}
