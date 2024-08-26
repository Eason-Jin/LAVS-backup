package uoa.lavs.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.layout.Pane;

public class AboutPopupController {
    @FXML private Pane aboutPopupPane;

    @FXML
    public void onClickClosePopup(ActionEvent event) {
        ((Pane) aboutPopupPane.getScene().getRoot()).getChildren().remove(aboutPopupPane);
    }
}
