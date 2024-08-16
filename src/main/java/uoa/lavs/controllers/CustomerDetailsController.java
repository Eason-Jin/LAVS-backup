package uoa.lavs.controllers;

import java.io.IOException;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import uoa.lavs.Main;
import uoa.lavs.SceneManager.AppScene;

public class CustomerDetailsController {
    @FXML private Button backButton;

    @FXML
    private void onClickBack(ActionEvent event) throws IOException {
        Main.setScene(AppScene.SEARCH);
    }
}
