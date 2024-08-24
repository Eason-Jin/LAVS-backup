package uoa.lavs.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;

public class EmailPopupController {
    @FXML private Pane addEmailPane;
    @FXML private TextField emailTextField;
    @FXML private CheckBox isPrimaryEmailCheckBox;

    @FXML
    private void onClickCloseAddEmail(ActionEvent event) {
        closePopup();
    }

    @FXML
    private void onClickSaveEmail(ActionEvent event) {
        closePopup();
    }

    private void closePopup() {
        Pane currentRoot = (Pane) addEmailPane.getScene().getRoot();
        currentRoot.getChildren().remove(addEmailPane);
    }
}
