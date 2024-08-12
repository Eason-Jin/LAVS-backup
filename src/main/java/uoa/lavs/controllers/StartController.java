package uoa.lavs.controllers;

import java.io.IOException;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import uoa.lavs.Main;
import uoa.lavs.SceneManager.AppScene;

public class StartController {
    
    @FXML private Button startButton;

    @FXML
    private void onClickStart(ActionEvent event) throws IOException {
        System.out.println("Start button clicked");
        Main.setScene(AppScene.ADD_CUSTOMER);
    }
}
