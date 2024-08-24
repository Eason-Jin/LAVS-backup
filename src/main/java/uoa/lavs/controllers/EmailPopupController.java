package uoa.lavs.controllers;

import java.util.function.Consumer;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
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
    String emailText = emailTextField.getText();

    if (emailText == null) {
      return;
    }

    if (!validateEmailFormat(emailText)) {
      Alert alert = new Alert(Alert.AlertType.ERROR);
      alert.setTitle("Invalid Email");
      alert.setHeaderText("The email address format is invalid.");
      alert.setContentText("Please enter a valid email address.");
      alert.showAndWait();
      return;
    }

    // Update the Email object
    this.email.setAddress(emailText);
    this.email.setIsPrimary(isPrimaryEmailCheckBox.isSelected());

    // Notify the original controller via the Consumer
    if (emailSaveHandler != null) {
      emailSaveHandler.accept(this.email);
    }
    closePopup();
  }

  @Override
  public void setUpPopup(Detail obj, Consumer<Detail> objectSaveHandler, boolean... args) {
    this.email = (Email) obj;
    this.emailSaveHandler = (Consumer<Email>) (Object) objectSaveHandler;
    emailTextField.setText(email.getAddress());
    isPrimaryEmailCheckBox.setDisable((args.length > 0 ? args[0] : false) && !email.getIsPrimary());
    isPrimaryEmailCheckBox.setSelected(email.getIsPrimary());
  }
}
