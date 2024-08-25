package uoa.lavs.controllers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
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
import uoa.lavs.dataoperations.customer.CustomerLoader;
import uoa.lavs.dataoperations.loan.CoborrowerLoader;
import uoa.lavs.dataoperations.loan.LoanLoader;
import uoa.lavs.dataoperations.loan.LoanPaymentsLoader;
import uoa.lavs.dataoperations.loan.LoanSummaryLoader;
import uoa.lavs.models.Customer;
import uoa.lavs.models.Loan;
import uoa.lavs.utility.LoanRepayment;
import uoa.lavs.utility.LoanSummary;

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

  public void setUpAddLoan(String customerId, String customerName) {
    setting = Setting.ADD;
    titleLabel.setText("New Loan for " + customerName);
    loanIdLabel.setText("");
    detailsTabPane.getSelectionModel().select(0);
    addPrimeBorrower(customerId);
    setDisableForFields(false);
    setVisabilityForFields(true);
  }

  public void setUpViewLoan(String loanId) {
    setting = Setting.VIEW;
    titleLabel.setText("Loan Details");
    detailsTabPane.getSelectionModel().select(0);
    setLoanDetails(loanId);
    setDisableForFields(true);
    setVisabilityForFields(false);
  }

  private void setDisableForFields(boolean isDisabled) {
    loanDetailsPane.setDisable(isDisabled);
    coBorrowersTable.setDisable(isDisabled);
    repaymentsTab.setDisable(!isDisabled);
  }

  private void setVisabilityForFields(boolean isVisible) {
    totalInterestLabel.setVisible(!isVisible);
    totalCostLabel.setVisible(!isVisible);
    payoffDateLabel.setVisible(!isVisible);
    totalInterestField.setVisible(!isVisible);
    totalCostField.setVisible(!isVisible);
    payoffDateField.setVisible(!isVisible);
    saveButton.setVisible(isVisible);
    addCoBorrowerButton.setVisible(isVisible);
  }

  private void resetScene() {
    resetFieldStyle();
    principalField.clear();
    rateTypeBox.setValue(null);
    rateValueField.clear();
    startDatePicker.setValue(null);
    periodField.clear();
    loanTermField.clear();
    compoundingBox.setValue(null);
    paymentFrequencyBox.setValue(null);
    paymentAmountField.clear();
    totalInterestField.clear();
    totalCostField.clear();
    payoffDateField.clear();
    loan = new Loan();
    primeBorrowerId = null;
    coBorrowers.clear();
    repayments.clear();
    coBorrowersTable.getItems().clear();
    repaymentsTable.getItems().clear();
  }

  private void resetFieldStyle() {
    principalField.setStyle(normalBorder);
    rateTypeBox.setStyle(normalBorder);
    rateValueField.setStyle(normalBorder);
    startDatePicker.setStyle(normalBorder);
    periodField.setStyle(normalBorder);
    loanTermField.setStyle(normalBorder);
    compoundingBox.setStyle(normalBorder);
    paymentFrequencyBox.setStyle(normalBorder);
    paymentAmountField.setStyle(normalBorder);
  }

  public void setLoanDetails(String loanId) {
    loan = LoanLoader.loadData(loanId);
    primeBorrowerId = loan.getCustomerId();
    LoanSummary loanSummary = LoanSummaryLoader.calculateLoanSummary(loanId);

    loanIdLabel.setText("Loan ID: " + loanId);

    principalField.setText("$" + String.valueOf(loan.getPrincipal()));
    rateTypeBox.setValue(String.valueOf(loan.getRateType()));
    rateValueField.setText(String.valueOf(loan.getRateValue()));
    startDatePicker.setValue(loan.getStartDate());
    periodField.setText(String.valueOf(loan.getPeriod()));
    loanTermField.setText(String.valueOf(loan.getTerm()));
    compoundingBox.setValue(String.valueOf(loan.getCompounding()));
    paymentFrequencyBox.setValue(String.valueOf(loan.getPaymentFrequency()));
    paymentAmountField.setText("$" + String.valueOf(loan.getPaymentAmount()));
    totalInterestField.setText("$" + String.valueOf(loanSummary.getTotalInterest()));
    totalCostField.setText("$" + String.valueOf(loanSummary.getTotalCost()));
    payoffDateField.setText(loanSummary.getPayOffDate().toString());

    setCoBorrowersTable(loanId);
    setRepaymentsTable(loanId);
  }

  private void setCoBorrowersTable(String loanId) {
    List<String> coBorrowerIds = CoborrowerLoader.loadData(loanId);

    // Extract the first part of the Loan-ID (before the dash "-")
    String loanIdPrefix = loanId.split("-")[0];
    // Remove the coBorrower ID that matches the first part of the Loan-ID
    coBorrowerIds.removeIf(id -> id.equals(loanIdPrefix));

    for (String id : coBorrowerIds) {
      Customer coBorrower = CustomerLoader.loadData(id);
      coBorrowers.add(coBorrower);
    }
    coBorrowersTable.setItems(coBorrowers);
  }

  private void setRepaymentsTable(String loanId) {
    repayments =
        FXCollections.observableArrayList(LoanPaymentsLoader.calculateLoanRepayments(loanId));
    repaymentsTable.setItems(repayments);
  }

  public void addPrimeBorrower(String primeBorrowerId) {
    this.primeBorrowerId = primeBorrowerId;
  }

  @FXML
  private void onClickAddCoBorrower(ActionEvent event) throws IOException {
    searchController.clearSearch();
    searchController.setCoBorrowerSearch(true);
    Main.setScene(AppScene.SEARCH);
  }

  @FXML
  private void onClickSave(ActionEvent event) {}

  @FXML
  private void onClickCancel(ActionEvent event) {}

  @FXML
  private void onClickHome(ActionEvent event) {
    resetScene();
    Main.setScene(AppScene.START);
  }
}
