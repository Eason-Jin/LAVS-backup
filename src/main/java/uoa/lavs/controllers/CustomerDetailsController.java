package uoa.lavs.controllers;

import java.io.IOException;

import org.springframework.stereotype.Controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import uoa.lavs.Main;
import uoa.lavs.SceneManager.AppScene;

@Controller
public class CustomerDetailsController {
    @FXML private Button backButton;
    @FXML private Button homeButton;
    @FXML private Button infoButton;
    @FXML private Button editButton;

    public void setCustomerDetails(String customerId) {

    }

    @FXML
    private void onClickBack(ActionEvent event) throws IOException {
        Main.setScene(AppScene.SEARCH);
    }

    @FXML
    private void onClickHome(ActionEvent event) throws IOException {
        Main.setScene(AppScene.START);
    }

    @FXML
    private void onClickInfo(ActionEvent event) {
        System.out.println("Info button clicked");
    }

    @FXML
    private void onClickEdit(ActionEvent event) {
        System.out.println("Edit button clicked");
    }
}
