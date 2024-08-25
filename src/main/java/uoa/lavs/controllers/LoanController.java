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
import javafx.scene.control.ButtonType;
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
import uoa.lavs.dataoperations.loan.CoborrowerUpdater;
import uoa.lavs.dataoperations.loan.LoanLoader;
import uoa.lavs.dataoperations.loan.LoanPaymentsLoader;
import uoa.lavs.dataoperations.loan.LoanSummaryLoader;
import uoa.lavs.dataoperations.loan.LoanUpdater;
import uoa.lavs.mainframe.Frequency;
import uoa.lavs.mainframe.RateType;
import uoa.lavs.models.Customer;
import uoa.lavs.models.Loan;
import uoa.lavs.utility.LoanRepayment;
import uoa.lavs.utility.LoanSummary;

import javax.xml.transform.Source;

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
  @FXML private TableColumn<Customer, String> coBorrowerIdColumn;
  @FXML private TableColumn<Customer, String> coBorrowerNameColumn;

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
    coBorrowerIdColumn.setReorderable(false);
    coBorrowerNameColumn.setReorderable(false);
    coBorrowerIdColumn.setCellValueFactory(new PropertyValueFactory<Customer, String>("id"));
    coBorrowerNameColumn.setCellValueFactory(new PropertyValueFactory<Customer, String>("name"));

    repaymentDateColumn.setReorderable(false);
    principalColumn.setReorderable(false);
    interestColumn.setReorderable(false);
    remainingColumn.setReorderable(false);
    repaymentDateColumn.setCellValueFactory(
        new PropertyValueFactory<LoanRepayment, String>("repaymentDate"));
    principalColumn.setCellValueFactory(
        new PropertyValueFactory<LoanRepayment, String>("principal"));
    interestColumn.setCellValueFactory(new PropertyValueFactory<LoanRepayment, String>("interest"));
    remainingColumn.setCellValueFactory(
        new PropertyValueFactory<LoanRepayment, String>("remaining"));

    coBorrowersTable.setPlaceholder(new Label("No co-borrowers for this loan"));

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
    principalField.getStyleClass().remove("invalid");
    rateTypeBox.getStyleClass().remove("invalid");
    rateValueField.getStyleClass().remove("invalid");
    startDatePicker.getStyleClass().remove("invalid");
    periodField.getStyleClass().remove("invalid");
    loanTermField.getStyleClass().remove("invalid");
    compoundingBox.getStyleClass().remove("invalid");
    paymentFrequencyBox.getStyleClass().remove("invalid");
    paymentAmountField.getStyleClass().remove("invalid");
  }

  public void setLoanDetails(String loanId) {
    if (loanId != null) {
      loanIdLabel.setText("Loan ID: " + loanId);
    }
    else {
      System.out.println("loanId provided is null");
    }

    loan = LoanLoader.loadData(loanId);

    if (loan != null) {
      primeBorrowerId = loan.getCustomerId();
      LoanSummary loanSummary = LoanSummaryLoader.calculateLoanSummary(loanId);

      principalField.setText(loan.getPrincipalString() != null ? loan.getPrincipalString() : "COULD NOT FIND");
      rateTypeBox.setValue(loan.getRateType() != null ? String.valueOf(loan.getRateType()): "COULD NOT FIND");
      rateValueField.setText(loan.getRateValue() != null ? String.valueOf(loan.getRateValue()) : "COULD NOT FIND");
      startDatePicker.setValue(loan.getStartDate() != null ? loan.getStartDate() : null);
      periodField.setText(loan.getPeriod() != null ? String.valueOf(loan.getPeriod()) : "COULD NOT FIND");
      loanTermField.setText(loan.getTerm() != null ? String.valueOf(loan.getTerm()) : "COULD NOT FIND");
      compoundingBox.setValue(loan.getCompounding() != null ? String.valueOf(loan.getCompounding()) : "COULD NOT FIND");
      paymentFrequencyBox.setValue(loan.getPaymentFrequency() != null ? String.valueOf(loan.getPaymentFrequency()) : "COULD NOT FIND");
      paymentAmountField.setText(loan.getPaymentAmountString() != null ? loan.getPaymentAmountString() : "COULD NOT FIND");
      totalInterestField.setText((loanSummary != null && loanSummary.getTotalInterest() != null) ? ("$" + String.format("%.2f", loanSummary.getTotalInterest())) : "COULD NOT FIND");
      totalCostField.setText((loanSummary != null && loanSummary.getTotalCost() != null) ? ("$" + String.format("%.2f", loanSummary.getTotalCost())) : "COULD NOT FIND");
      payoffDateField.setText((loanSummary != null && loanSummary.getPayOffDate() != null) ? loanSummary.getPayOffDate().toString() : "COULD NOT FIND");
    }
    else {
      principalField.setText("COULD NOT FIND");
      rateTypeBox.setValue("COULD NOT FIND");
      rateValueField.setText("COULD NOT FIND");
      startDatePicker.setValue(null);
      periodField.setText("COULD NOT FIND");
      loanTermField.setText("COULD NOT FIND");
      compoundingBox.setValue("COULD NOT FIND");
      paymentFrequencyBox.setValue("COULD NOT FIND");
      paymentAmountField.setText("COULD NOT FIND");
      totalInterestField.setText("COULD NOT FIND");
      totalCostField.setText("COULD NOT FIND");
      payoffDateField.setText("COULD NOT FIND");
    }

    setCoBorrowersTable(loanId);
    setRepaymentsTable(loanId);
  }

  private void setCoBorrowersTable(String loanId) {
    List<String> coBorrowerIds = CoborrowerLoader.loadData(loanId);

    // Extract the first part of the Loan-ID (before the dash "-")
    String loanIdPrefix = loanId.split("-")[0];
    // Remove the coBorrower ID that matches the first part of the Loan-ID
    coBorrowerIds.removeIf(id -> id.equals(loanIdPrefix) || id.equals(loanIdPrefix + " (Temporary)"));

    for (String id : coBorrowerIds) {
      Customer coBorrower = CustomerLoader.loadData(id);
      if (coBorrower != null) {
        coBorrowers.add(coBorrower);
      }
      else {
        System.out.println("CoBorrower not found: " + id);
      }
    }
    coBorrowersTable.setItems(coBorrowers);
  }

  private void setRepaymentsTable(String loanId) {
    repayments =
        FXCollections.observableArrayList(LoanPaymentsLoader.calculateLoanRepayments(loanId));
    repaymentsTable.setItems(repayments);
  }

  public void addCoBorrower(Customer coBorrower) {
    if (coBorrowers.contains(coBorrower) || coBorrower.getId().equals(primeBorrowerId)) {
      Alert exceptionAlert = new Alert(Alert.AlertType.ERROR);
      exceptionAlert.setTitle("Error");
      exceptionAlert.setHeaderText(
          coBorrowers.contains(coBorrower)
              ? "This customer has already been added as a co-borrower."
              : "This customer is the prime borrower.");
      exceptionAlert.showAndWait();
      return;
    }
    if (coBorrower != null) {
      coBorrowers.add(coBorrower);
    }
    else {
      System.out.println("coBorrower not found: " + coBorrower.getId());
    }
    coBorrowersTable.setItems(coBorrowers);
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

  public boolean checkFields() {
    boolean principalFlag = !isEmpty(principalField);
    boolean rateTypeFlag = !isEmpty(rateTypeBox);
    boolean rateValueFlag = !isEmpty(rateValueField);
    boolean startDateFlag = !isEmpty(startDatePicker);
    boolean periodFlag = !isEmpty(periodField);
    boolean loanTermFlag = !isEmpty(loanTermField);
    boolean compoundingFlag = !isEmpty(compoundingBox);
    boolean paymentFrequencyFlag = !isEmpty(paymentFrequencyBox);
    boolean paymentAmountFlag = !isEmpty(paymentAmountField);

    if (principalFlag
        && rateTypeFlag
        && rateValueFlag
        && startDateFlag
        && periodFlag
        && loanTermFlag
        && compoundingFlag
        && paymentFrequencyFlag
        && paymentAmountFlag) {
      return true;
    }
    errorMessage.append("\tPlease fill in the required fields\n");

    return false;
  }

  public boolean validateFields() {
    boolean principalFlag = validateNumberFormat(principalField, true);
    boolean rateValueFlag = validateNumberFormat(rateValueField, true);
    boolean periodFlag = validateNumberFormat(periodField, false);
    boolean loanTermFlag = validateNumberFormat(loanTermField, false);
    boolean paymentAmountFlag = validateNumberFormat(paymentAmountField, true);
    boolean startDateFlag = validateDateFormat(startDatePicker, false);

    if (!principalFlag) {
      errorMessage.append("\tPrincipal must be a number!\n");
    }
    if (!rateValueFlag) {
      errorMessage.append("\tRate value must be a number!\n");
    }
    if (!periodFlag) {
      errorMessage.append("\tPeriod must be an integer!\n");
    }
    if (!loanTermFlag) {
      errorMessage.append("\tLoan term must be an integer!\n");
    }
    if (!paymentAmountFlag) {
      errorMessage.append("\tPayment amount must be a number!\n");
    }
    if (!startDateFlag) {
      errorMessage.append("\tStart date must be after today!\n");
    }

    return principalFlag
        && rateValueFlag
        && startDateFlag
        && periodFlag
        && loanTermFlag
        && paymentAmountFlag;
  }

  @FXML
  private void onClickSave(ActionEvent event) {
    if (checkFields() && validateFields()) {
      try {
        loan =
            new Loan(
                null,
                primeBorrowerId,
                CustomerLoader.loadData(primeBorrowerId).getName(),
                "Active",
                Double.parseDouble(principalField.getText()),
                Double.parseDouble(rateValueField.getText()),
                RateType.valueOf((rateTypeBox.getValue()).replaceAll("\\s", "")),
                startDatePicker.getValue(),
                Integer.parseInt(periodField.getText()),
                Integer.parseInt(loanTermField.getText()),
                Double.parseDouble(paymentAmountField.getText()),
                Frequency.valueOf(paymentFrequencyBox.getValue()),
                Frequency.valueOf(compoundingBox.getValue()));

        LoanUpdater.updateData(null, loan);

        String loanId = loan.getLoanId();

        List<String> existingCoborrowers = CoborrowerLoader.loadData(loanId);
        for (Customer coborrower : coBorrowers) {
          if (!existingCoborrowers.contains(coborrower.getId())) {
            CoborrowerUpdater.updateData(loanId, coborrower.getId(), null);
          }
        }
        Alert successAlert = new Alert(AlertType.INFORMATION);
        successAlert.setTitle("Success");
        successAlert.setHeaderText("Loan has been added");
        if (successAlert.showAndWait().get() == ButtonType.OK) {
          setUpViewLoan(loanId);
        }
      } catch (Exception e) {
        System.out.println(e.getLocalizedMessage());
        Alert exceptionAlert = new Alert(AlertType.ERROR);
        exceptionAlert.setTitle("Error");
        exceptionAlert.setHeaderText("An error occurred while saving the loan");
        exceptionAlert.setContentText("Please try again later");
        exceptionAlert.showAndWait();
      }
    } else {
      alert.setContentText(errorMessage.toString());
      alert.showAndWait();
      errorMessage = new StringBuilder();
    }
  }

  @FXML
  private void onClickCancel(ActionEvent event) {
    Alert alertCancel = new Alert(AlertType.CONFIRMATION);
    alertCancel.setTitle("Cancel adding loan");
    alertCancel.setHeaderText("If you cancel, all progress will be lost.");
    alertCancel.setContentText("Are you sure you want to cancel?");

    if (setting == Setting.ADD) {
      if (alertCancel.showAndWait().get() == ButtonType.OK) {
        customerController.setUpViewCustomer(primeBorrowerId);
        resetScene();
        Main.setScene(AppScene.CUSTOMER);
      }
    } else {
      customerController.setUpViewCustomer(primeBorrowerId);
      resetScene();
      Main.setScene(AppScene.CUSTOMER);
    }
  }

  @FXML
  private void onClickHome(ActionEvent event) {
    resetScene();
    Main.setScene(AppScene.START);
  }
}
