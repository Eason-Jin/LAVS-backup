package uoa.lavs.controllers;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import uoa.lavs.Main;
import uoa.lavs.SceneManager;
import uoa.lavs.SceneManager.AppScene;
import uoa.lavs.controllers.interfaces.CheckEmpty;
import uoa.lavs.controllers.interfaces.ValidateType;
import uoa.lavs.dataoperations.customer.CustomerLoader;
import uoa.lavs.dataoperations.loan.CoborrowerUpdater;
import uoa.lavs.dataoperations.loan.LoanLoader;
import uoa.lavs.dataoperations.loan.LoanPaymentsLoader;
import uoa.lavs.dataoperations.loan.LoanUpdater;
import uoa.lavs.mainframe.Frequency;
import uoa.lavs.mainframe.RateType;
import uoa.lavs.models.Customer;
import uoa.lavs.models.Loan;
import uoa.lavs.utility.LoanRepayment;

@Controller
public class LoanDetailsController {

  @FXML private Pane loanDetailsPane;
  @FXML private TabPane detailsTabPane;
  @FXML private Label titleLabel;

  @FXML private TextField principalField;
  @FXML private TextField rateTypeField;
  @FXML private TextField rateValueField;
  @FXML private TextField startDateField;
  @FXML private TextField periodField;
  @FXML private TextField loanTermField;
  @FXML private TextField compoundingField;
  @FXML private TextField paymentFrequencyField;
  @FXML private TextField paymentAmountField;

  @FXML private TableView<Customer> coBorrowersTable;
  @FXML private TableColumn<Customer, String> coBorrowerNameColumn;
  @FXML private TableColumn<Customer, String> coBorrowerIdColumn;

  @FXML private TableView<LoanRepayment> repaymentsTable;
  @FXML private TableColumn<LoanRepayment, String> repaymentDateColumn;
  @FXML private TableColumn<LoanRepayment, String> principalColumn;
  @FXML private TableColumn<LoanRepayment, String> interestColumn;
  @FXML private TableColumn<LoanRepayment, String> remainingColumn;

  @Autowired SearchController searchController;
  @Autowired CustomerDetailsController customerDetailsController;

  @FXML
  private void initialize() {
    coBorrowerNameColumn.setCellValueFactory(new PropertyValueFactory<Customer, String>("coBorrowerName"));
    coBorrowerIdColumn.setCellValueFactory(new PropertyValueFactory<Customer, String>("coBorrowerId"));

    repaymentDateColumn.setCellValueFactory(new PropertyValueFactory<LoanRepayment, String>("repaymentDate"));
    principalColumn.setCellValueFactory(new PropertyValueFactory<LoanRepayment, String>("principal"));
    interestColumn.setCellValueFactory(new PropertyValueFactory<LoanRepayment, String>("interest"));
    remainingColumn.setCellValueFactory(new PropertyValueFactory<LoanRepayment, String>("remaining"));
  }

  public void setLoanDetails(String loanId) {
    Loan loan = LoanLoader.loadData(loanId);

    titleLabel.setText("Loan Details for Loan ID: " + loanId);

    principalField.setText(String.valueOf(loan.getPrincipal()));
    rateTypeField.setText(loan.getRateType().toString());
    rateValueField.setText(String.valueOf(loan.getRateValue()));
    startDateField.setText(loan.getStartDate().toString());
    periodField.setText(String.valueOf(loan.getPeriod()));
    loanTermField.setText(String.valueOf(loan.getTerm()));
    compoundingField.setText(loan.getCompounding().toString());
    paymentFrequencyField.setText(loan.getPaymentFrequency().toString());
    paymentAmountField.setText(String.valueOf(loan.getPaymentAmount()));

    setRepaymentsTable(loanId);
  }

  private void setRepaymentsTable(String loanId) {
    List<LoanRepayment> loanRepayments = LoanPaymentsLoader.calculateLoanRepayments(loanId);
    ObservableList<LoanRepayment> rows = FXCollections.observableArrayList(loanRepayments);
    repaymentsTable.setItems(rows);
  }

  private void resetScene() {
    coBorrowersTable.getItems().clear();
    repaymentsTable.getItems().clear();
    detailsTabPane.getSelectionModel().select(0);
  }

  @FXML
  private void onClickHome(ActionEvent event) throws IOException {
    resetScene();
    Main.setScene(AppScene.START);
  }

  @FXML
  private void onClickBack(ActionEvent event) throws IOException {
    resetScene();
    Main.setScene(AppScene.CUSTOMER_DETAILS);
  }
}