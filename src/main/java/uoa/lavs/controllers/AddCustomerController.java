package uoa.lavs.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import uoa.lavs.Main;
import uoa.lavs.SceneManager;

import java.io.IOException;

public class AddCustomerController {
    
    @FXML private Button startButton;

    @FXML
    private void onClickStart(ActionEvent event) throws IOException {
        Main.setScene(SceneManager.AppScene.START);
    }
}
