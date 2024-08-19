package uoa.lavs.controllers;

import java.io.IOException;
import java.util.List;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyEvent;
import org.springframework.stereotype.Controller;
import uoa.lavs.Main;
import uoa.lavs.SceneManager.AppScene;
import uoa.lavs.dataoperations.customer.CustomerFinder;
import uoa.lavs.models.Customer;

@Controller
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
        searchTable.setPlaceholder(new Label(""));

        searchTable.setRowFactory(tableView -> {
            final TableRow<Customer> row = new TableRow<Customer>();
            row.hoverProperty().addListener((observable) -> {
                if (row.isHover() && !row.isEmpty()) {
                    row.styleProperty().set("-fx-background-color: #f0f0f0");
                } else {
                    row.styleProperty().set("-fx-background-color: none");
                    row.styleProperty().set("-fx-border-width: 5");
                }
            });

            row.selectedProperty().addListener((observable) -> {
                if (row.isSelected() && !row.isEmpty()) {
                    Main.setScene(AppScene.CUSTOMER_DETAILS);
                }
            });
            return row;
        });
    }

    private void searchCustomers() {
        String customerName = searchField.getText();

        if (customerName == "") {
            Platform.runLater(() -> {
                searchTable.getItems().clear();
                searchTable.setPlaceholder(new Label("Please enter a name"));
            });
            return;
        }

        List<Customer> customers = CustomerFinder.findCustomerByName(customerName);

        if (customers.isEmpty()) {
            Platform.runLater(() -> {
                searchTable.getItems().clear();
                searchTable.setPlaceholder(new Label("No customers found"));
            });
        } else {
            ObservableList<Customer> observablecustomers = FXCollections.observableArrayList(customers);
            Platform.runLater(() -> searchTable.setItems(observablecustomers));
        }
    }

    @FXML
    private void keyPressed(KeyEvent event) {
        if (event.getCode().toString().equals("ENTER")) {
            searchCustomers();
        }
    }

    @FXML
    private void onClickStart(ActionEvent event) throws IOException {
        Main.setScene(AppScene.START);
    }

    @FXML
    private void onClickSearch(ActionEvent event) {
        searchCustomers();
    }
}
