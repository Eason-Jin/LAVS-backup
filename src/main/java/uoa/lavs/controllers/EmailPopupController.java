package uoa.lavs.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import org.springframework.stereotype.Controller;
import uoa.lavs.models.Email;
import java.util.function.Consumer;

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
        // Update the Email object
        this.email.setAddress(emailTextField.getText());
        this.email.setIsPrimary(isPrimaryEmailCheckBox.isSelected());

        // Notify the original controller via the Consumer
        if (emailSaveHandler != null) {
            emailSaveHandler.accept(this.email);
        }
        closePopup();
    }

    public void setUpEmailPopup(Email email, boolean isPrimaryExists, Consumer<Email> emailSaveHandler) {
        this.email = email;
        this.emailSaveHandler = emailSaveHandler;
        emailTextField.setText(email.getAddress());
        isPrimaryEmailCheckBox.setDisable(isPrimaryExists);
        isPrimaryEmailCheckBox.setSelected(isPrimaryExists);
    }

    private void closePopup() {
        Pane currentRoot = (Pane) addEmailPane.getScene().getRoot();
        currentRoot.getChildren().remove(addEmailPane);
    }
}
