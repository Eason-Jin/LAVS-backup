package uoa.lavs.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.layout.Pane;

public class AddEmailPopupController {
    @FXML private Pane addEmailPane;

    @FXML
    private void onClickCloseAddEmail(ActionEvent event) {
        Pane currentRoot = (Pane) addEmailPane.getScene().getRoot();
        currentRoot.getChildren().remove(addEmailPane);
    }
}
