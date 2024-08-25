package uoa.lavs.controllers;

import java.io.IOException;
import java.util.ArrayList;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Pane;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import uoa.lavs.Main;
import uoa.lavs.SceneManager.AppScene;
import uoa.lavs.models.Customer;
import uoa.lavs.models.Loan;
import uoa.lavs.utility.LoanRepayment;

@Controller
public class LoanController extends uoa.lavs.controllers.Controller {
  private enum Setting {
    VIEW,
    ADD
  }

  private Setting setting;

  @Autowired private CustomerController customerController;
  @Autowired private SearchController searchController;

  @FXML private Button homeButton;
  @FXML private Button saveButton;
  @FXML private Button cancelButton;

  @FXML private TabPane detailsTabPane;
  @FXML private Pane loanDetailsPane;
  @FXML private Label titleLabel;
  @FXML private Label loanIdLabel;

  @FXML private TextField principalField;
  @FXML private ComboBox<String> rateTypeBox;
  @FXML private TextField rateValueField;
  @FXML private DatePicker startDatePicker;
  @FXML private TextField periodField;
  @FXML private TextField loanTermField;
  @FXML private ComboBox<String> compoundingBox;
  @FXML private ComboBox<String> paymentFrequencyBox;
  @FXML private TextField paymentAmountField;
  @FXML private TextField totalInterestField;
  @FXML private TextField totalCostField;
  @FXML private TextField payoffDateField;

  @FXML private Label totalInterestLabel;
  @FXML private Label totalCostLabel;
  @FXML private Label payoffDateLabel;

  @FXML private Button addCoBorrowerButton;
  @FXML private TableView<Customer> coBorrowersTable;
  @FXML private TableColumn<Customer, String> coBorrowerNameColumn;
  @FXML private TableColumn<Customer, String> coBorrowerIdColumn;

  @FXML private Tab repaymentsTab;
  @FXML private TableView<LoanRepayment> repaymentsTable;
  @FXML private TableColumn<LoanRepayment, String> repaymentDateColumn;
  @FXML private TableColumn<LoanRepayment, String> principalColumn;
  @FXML private TableColumn<LoanRepayment, String> interestColumn;
  @FXML private TableColumn<LoanRepayment, String> remainingColumn;

  private Loan loan;
  private String primeBorrowerId;
  private ObservableList<Customer> coBorrowers;
  private ObservableList<LoanRepayment> repayments;

  @FXML
  public void initialize() {
    coBorrowerNameColumn.setCellValueFactory(new PropertyValueFactory<Customer, String>("name"));
    coBorrowerIdColumn.setCellValueFactory(new PropertyValueFactory<Customer, String>("id"));

    repaymentDateColumn.setCellValueFactory(
        new PropertyValueFactory<LoanRepayment, String>("repaymentDate"));
    principalColumn.setCellValueFactory(
        new PropertyValueFactory<LoanRepayment, String>("principal"));
    interestColumn.setCellValueFactory(new PropertyValueFactory<LoanRepayment, String>("interest"));
    remainingColumn.setCellValueFactory(
        new PropertyValueFactory<LoanRepayment, String>("remaining"));

    coBorrowersTable.setPlaceholder(new Label("No co-borrowers for this loan"));

    coBorrowersTable.setRowFactory(
        tableView -> {
          final TableRow<Customer> row = new TableRow<Customer>();

          row.setOnMouseClicked(
              event -> {
                if (!row.isEmpty()) {
                  customerController.setUpViewCustomer(row.getItem().getId());
                  Main.setScene(AppScene.CUSTOMER);
                }
              });

          return row;
        });

    alert = new Alert(AlertType.ERROR);
    alert.setTitle("Error");
    alert.setHeaderText("Please fix the following issues:");
    errorMessage = new StringBuilder();

    loan = new Loan();
    coBorrowers = FXCollections.observableArrayList(new ArrayList<>());
    repayments = FXCollections.observableArrayList(new ArrayList<>());
  }

  public void setUpAddLoan(String customerId, String customerName) {}

  public void setUpViewLoan(String loanId) {}

  @FXML
  private void onClickAddCoBorrower(ActionEvent event) throws IOException {}

  @FXML
  private void onClickSave(ActionEvent event) {}

  @FXML
  private void onClickCancel(ActionEvent event) {}

  @FXML
  private void onClickHome(ActionEvent event) {}
}
