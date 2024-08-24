package uoa.lavs.controllers;

import java.util.function.Consumer;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import uoa.lavs.models.Email;

public class EmailPopupController {
  @FXML private Pane addEmailPane;
  @FXML private TextField emailTextField;
  @FXML private CheckBox isPrimaryEmailCheckBox;

  private Email email;
  private Consumer<Email> emailSaveHandler;

  public void initialize() {}

  @FXML
  private void onClickCloseAddEmail(ActionEvent event) {
    closePopup();
  }

  @FXML
  private void onClickSaveEmail(ActionEvent event) {
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

  public void setUpEmailPopup(
      Email email, boolean isPrimaryExists, Consumer<Email> emailSaveHandler) {
    this.email = email;
    this.emailSaveHandler = emailSaveHandler;
    emailTextField.setText(email.getAddress());
    isPrimaryEmailCheckBox.setDisable(isPrimaryExists && !email.getIsPrimary());
    isPrimaryEmailCheckBox.setSelected(email.getIsPrimary());
  }

  private boolean validateEmailFormat(String email) {
    // Regex pattern to validate email addresses to be a@b.c
    String emailRegex = "^.+@.+\\..+$";
    return email.matches(emailRegex);
  }

  private void closePopup() {
    Pane currentRoot = (Pane) addEmailPane.getScene().getRoot();
    currentRoot.getChildren().remove(addEmailPane);
  }
}
