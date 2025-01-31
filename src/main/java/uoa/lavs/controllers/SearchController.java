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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import uoa.lavs.Main;
import uoa.lavs.SceneManager.AppScene;
import uoa.lavs.dataoperations.customer.CustomerFinder;
import uoa.lavs.models.Customer;

@Controller
public class SearchController {
  @Autowired private CustomerController customerController;

  @FXML private Button backButton;
  @FXML private Button searchButton;
  @FXML private TextField searchField;
  @FXML private TableView<Customer> searchTable;
  @FXML private TableColumn<Customer, String> idColumn;
  @FXML private TableColumn<Customer, String> nameColumn;
  @FXML private TableColumn<Customer, String> dobColumn;
  @FXML private Label searchSceneTitleLabel;

  @Autowired LoanController loanController;

  private boolean isCoBorrowerSearch;

  public void initialize() {
    searchButton.disableProperty().bind(searchField.textProperty().isEmpty());
    idColumn.setCellValueFactory(new PropertyValueFactory<Customer, String>("id"));
    idColumn.setReorderable(false);
    nameColumn.setCellValueFactory(new PropertyValueFactory<Customer, String>("name"));
    nameColumn.setReorderable(false);
    dobColumn.setCellValueFactory(new PropertyValueFactory<Customer, String>("dob"));
    dobColumn.setReorderable(false);
    searchTable.setPlaceholder(new Label(""));

    searchTable.setRowFactory(
        tableView -> {
          final TableRow<Customer> row = new TableRow<Customer>();

          row.setOnMouseClicked(
              event -> {
                if (row.isEmpty()) {
                  return;
                }

                if (isCoBorrowerSearch) {
                  clearSearch();
                  if (loanController.addCoBorrower(row.getItem())) {
                    setCoBorrowerSearch(false);
                    Main.setScene(AppScene.LOAN);
                  }
                } else {
                  String customerId = row.getItem().getId();
                  customerController.setUpViewCustomer(customerId);
                  Main.setScene(AppScene.CUSTOMER);
                }
              });

          row.setOnMouseEntered(
              event -> {
                if (!row.isEmpty()) {
                  row.styleProperty().set("-fx-background-color: #f0f0f0");
                }
              });

          row.setOnMouseExited(
              event -> {
                if (!row.isEmpty()) {
                  row.styleProperty().set("-fx-background-color: none");
                  row.styleProperty().set("-fx-border-width: 5");
                }
              });

          return row;
        });
  }

  public void setIsCoBorrowerSearch(boolean isCoBorrowerSearch) {
    this.isCoBorrowerSearch = isCoBorrowerSearch;
  }

  public void setCoBorrowerSearch(boolean isCoBorrowerSearch) {
    setIsCoBorrowerSearch(isCoBorrowerSearch);
    if (isCoBorrowerSearch) {
      setTitleText("Co-Borrower Search");
    } else {
      setTitleText("Customer Search");
    }
  }

  public void setTitleText(String title) {
    searchSceneTitleLabel.setText(title);
  }

  private void searchCustomers() {
    String customerName = searchField.getText();
    List<Customer> customers = CustomerFinder.findCustomerByName(customerName);

    if (customers.isEmpty()) {
      Platform.runLater(
          () -> {
            searchTable.getItems().clear();
            searchTable.setPlaceholder(new Label("No customers found"));
          });
    } else {
      ObservableList<Customer> observableCustomers = FXCollections.observableArrayList(customers);
      Platform.runLater(() -> searchTable.setItems(observableCustomers));
    }
  }

  public void clearSearch() {
    searchField.clear();
    searchTable.getItems().clear();
    searchTable.setPlaceholder(new Label(""));
  }

  @FXML
  private void keyPressed(KeyEvent event) {
    if (event.getCode().toString().equals("ENTER")) {
      searchCustomers();
    }
  }

  @FXML
  private void onClickBack(ActionEvent event) throws IOException {
    clearSearch();
    if (isCoBorrowerSearch) {
      Main.setScene(AppScene.LOAN);
    } else {
      setCoBorrowerSearch(false);
      Main.setScene(AppScene.START);
    }
  }

  @FXML
  private void onClickSearch(ActionEvent event) {
    searchCustomers();
  }
}
