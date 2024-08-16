package uoa.lavs.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import uoa.lavs.Main;
import uoa.lavs.SceneManager.AppScene;
import uoa.lavs.dataoperations.customer.CustomerFinder;
import uoa.lavs.models.Customer;

import java.io.IOException;
import java.util.List;

public class SearchController {

    @FXML private Button startButton;
    @FXML private Button searchButton;
    @FXML private TextField searchField;
    @FXML private TableView<Customer> searchTable;
    @FXML private TableColumn<Customer, String> idColumn;
    @FXML private TableColumn<Customer, String> nameColumn;
    @FXML private TableColumn<Customer, String> dobColumn;

    public void initialize() {
        idColumn.setCellValueFactory(new PropertyValueFactory<Customer, String>("id"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<Customer, String>("name"));
        dobColumn.setCellValueFactory(new PropertyValueFactory<Customer, String>("dob"));
    }

    @FXML
    private void onClickStart(ActionEvent event) throws IOException {
        Main.setScene(AppScene.START);
    }

    @FXML
    private void onClickSearch(ActionEvent event) {
        String customerName = searchField.getText();
        List<Customer> customers = CustomerFinder.findCustomerByName(customerName);
        ObservableList<Customer> observablecustomers = FXCollections.observableArrayList(customers);
        searchTable.setItems(observablecustomers);
    }
}
