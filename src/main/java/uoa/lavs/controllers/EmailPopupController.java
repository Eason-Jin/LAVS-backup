package uoa.lavs.controllers;

import java.util.function.Consumer;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import uoa.lavs.models.Detail;
import uoa.lavs.models.Email;

public class EmailPopupController extends PopupController {
  @FXML private Pane addEmailPane;
  @FXML private TextField emailTextField;
  @FXML private CheckBox isPrimaryEmailCheckBox;

  private Email email;
  private Consumer<Email> emailSaveHandler;

  public void initialize() {
    setPane(addEmailPane);
  }

  @Override
  @FXML
  public void onClickSave(ActionEvent event) {
    if (isEmpty(emailTextField)) {
      appendErrorMessage("Please fill in all required fields!\n");
    } else {
      if (!validateEmailFormat(emailTextField)) {
        appendErrorMessage("Email must be in the format of a@b.c!\n");
      }

      if (isTooLong(emailTextField, 60)) {
        appendErrorMessage("Email must be less than 60 characters!\n");
      } else {
        if (!validateEmailFormat(emailTextField)) {
          appendErrorMessage("Email must be in the format of a@b.c!\n");
        }
      }
    }

    if (errorMessage.length() > 0) {
      showAlert();
      return;
    }

    // Update the Email object
    this.email.setAddress(emailTextField.getText());
    this.email.setIsPrimary(isPrimaryEmailCheckBox.isSelected());

    // Notify the original controller via the Consumer
    if (emailSaveHandler != null) {
      emailSaveHandler.accept(this.email);
    }
    closePopup();
  }

  @Override
  public void setUpPopup(Detail obj, Consumer<Detail> objectSaveHandler, Boolean isPrimary) {
    this.email = (Email) obj;
    this.emailSaveHandler = (Consumer<Email>) (Object) objectSaveHandler;
    if (email.getIsPrimary() == null) {
      email.setIsPrimary(false);
    }
    emailTextField.setText(email.getAddress());
    isPrimaryEmailCheckBox.setDisable(isPrimary && !email.getIsPrimary());
    isPrimaryEmailCheckBox.setSelected(email.getIsPrimary());
  }
}
