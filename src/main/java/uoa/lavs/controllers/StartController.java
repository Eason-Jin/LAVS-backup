package uoa.lavs.controllers;

import java.io.IOException;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import uoa.lavs.Main;
import uoa.lavs.SceneManager.AppScene;

@Controller
public class StartController {

    @Autowired
    private SearchController searchController;

    @FXML private Button addCustomerButton;
    @FXML private Button searchButton;

    @FXML
    private void onClickAddCustomer(ActionEvent event) throws IOException {
        Main.setScene(AppScene.ADD_CUSTOMER);
    }

    @FXML
    private void onClickSearch(ActionEvent event) throws IOException {
        searchController.clearSearch();
        Main.setScene(AppScene.SEARCH);
    }
}
