package uoa.lavs.controllers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import uoa.lavs.Main;
import uoa.lavs.SceneManager;
import uoa.lavs.SceneManager.AppScene;
import uoa.lavs.dataoperations.customer.CustomerLoader;
import uoa.lavs.dataoperations.loan.CoborrowerUpdater;
import uoa.lavs.dataoperations.loan.LoanUpdater;
import uoa.lavs.mainframe.Frequency;
import uoa.lavs.mainframe.RateType;
import uoa.lavs.models.Customer;
import uoa.lavs.models.Loan;

@Controller
public class AddLoanController extends uoa.lavs.controllers.Controller {
  @FXML private AnchorPane coBorrowerScrollAnchorPane;
  @FXML private FlowPane coBorrowerFlowPane;
  @FXML private Pane coBorrowerPane;
  @FXML private Pane loanDetailsPane;
  @FXML private TabPane detailsTabPane;
  @FXML private Label customerNameLabel;
  @FXML private Label titleLabel;

  @FXML private TextField principalField;
  @FXML private TextField rateValueField;
  @FXML private TextField periodField;
  @FXML private TextField loanTermField;
  @FXML private TextField paymentAmountField;
  @FXML private DatePicker startDatePicker;

  private int initialCoBorrowerCounter = 1;
  private int coBorrowerCounter = initialCoBorrowerCounter;
  private int numCoBorrowers = 0;
  private HashMap<String, Node> coBorrowerFields = new HashMap<>();
  private HashMap<String, Node> loanDetailsFields = new HashMap<>();
  private HashSet<String> coBorrowerIds = new HashSet<>();
  private Customer customer;

  @Autowired SearchController searchController;
  @Autowired CustomerController customerController;
  @Autowired LoanDetailsController loanDetailsController;

  @FXML
  private void initialize() {
    coBorrowerFlowPane.getChildren().remove(coBorrowerPane);
    addToMap(loanDetailsFields, loanDetailsPane);
  }

  private void addToMap(HashMap<String, Node> map, Pane pane) {
    for (var node : pane.getChildren()) {
      if (node.getId() != null) {
        map.put(node.getId(), node);
      }
    }
    map.put(pane.getId(), pane);
  }

  @FXML
  private void onClickDeleteCoBorrowers(ActionEvent event) throws IOException {
    String buttonClickedFxId = ((Button) event.getSource()).getId();
    String paneToDeleteFxId;

    if ((buttonClickedFxId.split("_")).length >= 2) {
      paneToDeleteFxId = coBorrowerPane.getId() + "_" + buttonClickedFxId.split("_")[1];
    } else {
      paneToDeleteFxId = coBorrowerPane.getId();
    }
    Pane paneToDelete = (Pane) coBorrowerFields.get(paneToDeleteFxId);
    List<Node> nodesCopy = paneToDelete.getChildrenUnmodifiable();
    String id = "";
    for (var node : nodesCopy) {
      if (node.getId() != null && node.getId().contains("Id")) {
        id = ((TextField) node).getText();
      }
      coBorrowerFields.remove(node.getId());
    }
    coBorrowerFields.remove(paneToDeleteFxId);
    coBorrowerIds.remove(id);

    coBorrowerScrollAnchorPane.setPrefHeight(
        coBorrowerScrollAnchorPane.getPrefHeight()
            - (coBorrowerPane.getPrefHeight() + coBorrowerFlowPane.getVgap()));
    coBorrowerFlowPane.getChildren().remove(paneToDelete);
  }

  @FXML
  private void onClickAddCoBorrower(ActionEvent event) throws IOException {
    searchController.clearSearch();
    searchController.setCoBorrowerSearch(true);
    Main.setScene(SceneManager.AppScene.SEARCH);
  }

  public void setCustomerName() {
    titleLabel.setText("New Loan for " + CustomerLoader.loadData(customer.getId()).getName());
  }

  public void setCustomer(Customer customer) {
    this.customer = customer;
  }

  public void addPrimeBorrower() {
    coBorrowerIds.add(customer.getId());
  }

  public void addCoBorrower(TableRow<Customer> row) {
    String coBorrowerName = row.getItem().getName();
    String coBorrowerId = row.getItem().getId();
    if (coBorrowerIds.contains(coBorrowerId)) {
      Alert exceptionAlert = new Alert(Alert.AlertType.ERROR);
      exceptionAlert.setTitle("Error");
      exceptionAlert.setHeaderText("This customer has already been added as a co-borrower.");
      exceptionAlert.showAndWait();
      return;
    }

    List<Node> nodesCopy = new ArrayList<>(coBorrowerPane.getChildrenUnmodifiable());
    String counterString = "_" + coBorrowerCounter;
    Pane newPane = new Pane();
    newPane.setId(coBorrowerPane.getId() + counterString);
    newPane.setPrefWidth(coBorrowerPane.getPrefWidth());
    newPane.setPrefHeight(coBorrowerPane.getPrefHeight());
    for (var node : nodesCopy) {
      String newFxId = node.getId() + counterString;
      if (node instanceof TextField) {
        TextField newTextField = new TextField();
        if (newFxId.contains("Name")) {
          newTextField.setText(coBorrowerName);
        } else if (newFxId.contains("Id")) {
          newTextField.setText(coBorrowerId);
        }
        newTextField.setLayoutX(node.getLayoutX());
        newTextField.setLayoutY(node.getLayoutY());
        newTextField.setPrefWidth(((TextField) node).getPrefWidth());
        newTextField.setPrefHeight(((TextField) node).getPrefHeight());
        newTextField.setEditable(((TextField) node).isEditable());
        newTextField.setId(newFxId);
        newPane.getChildren().add(newTextField);
        coBorrowerFields.put(newFxId, newTextField);
      } else if (node instanceof Separator) {
        Separator newSeparator = new Separator();
        newSeparator.setPrefWidth(((Separator) node).getPrefWidth());
        newSeparator.setPrefHeight(((Separator) node).getPrefHeight());
        newSeparator.setLayoutX(node.getLayoutX());
        newSeparator.setLayoutY(node.getLayoutY());
        newPane.getChildren().add(newSeparator);
      } else if (node instanceof Button) {
        Button newButton = new Button("");
        newButton.setPrefWidth(((Button) node).getPrefWidth());
        newButton.setPrefHeight(((Button) node).getPrefHeight());
        newButton.setLayoutX(node.getLayoutX());
        newButton.setLayoutY(node.getLayoutY());
        newButton.getStyleClass().addAll(node.getStyleClass());
        EventHandler<ActionEvent> handler = ((Button) node).getOnAction();
        newButton.setOnAction(handler);
        newButton.setId(newFxId);
        newPane.getChildren().add(newButton);
        coBorrowerFields.put(newFxId, newButton);
      } else {
        continue;
      }
    }
    coBorrowerFlowPane.getChildren().add(newPane);
    coBorrowerFields.put(newPane.getId(), newPane);
    coBorrowerIds.add(coBorrowerId);
    coBorrowerCounter++;
    numCoBorrowers++;
    coBorrowerScrollAnchorPane.setPrefHeight(
        coBorrowerScrollAnchorPane.getPrefHeight()
            + coBorrowerPane.getPrefHeight()
            + coBorrowerFlowPane.getVgap());
  }

  private void resetScene() {
    resetCoBorrowerFields();
    coBorrowerIds.clear();
    clearAllFields();
    detailsTabPane.getSelectionModel().select(0);
    searchController.setCoBorrowerSearch(false);
  }

  private void clearAllFields() {
    for (String nodeId : loanDetailsFields.keySet()) {
      Node node = loanDetailsFields.get(nodeId);
      if (node instanceof TextField) {
        ((TextField) node).clear();
      } else if (node instanceof DatePicker) {
        ((DatePicker) node).setValue(null);
      } else if (node instanceof ComboBox) {
        remakeComboBox(node);
      } else if (node instanceof CheckBox) {
        ((CheckBox) node).setSelected(false);
      }
    }
  }

  private void remakeComboBox(Node node) {
    loanDetailsPane.getChildren().remove(node);
    ComboBox<FXCollections> newComboBox = new ComboBox<>();
    newComboBox.setPromptText(((ComboBox<FXCollections>) node).getPromptText());
    newComboBox.setItems(
        FXCollections.observableArrayList(((ComboBox<FXCollections>) node).getItems()));
    newComboBox.setLayoutX(node.getLayoutX());
    newComboBox.setLayoutY(node.getLayoutY());
    newComboBox.setPrefWidth(((ComboBox<FXCollections>) node).getPrefWidth());
    newComboBox.setPrefHeight(((ComboBox<FXCollections>) node).getPrefHeight());
    newComboBox.setId(node.getId());
    loanDetailsPane.getChildren().add(newComboBox);
    loanDetailsFields.replace(node.getId(), newComboBox);
  }

  private void resetCoBorrowerFields() {
    coBorrowerFlowPane.getChildren().clear();
    coBorrowerScrollAnchorPane.setPrefHeight(coBorrowerFlowPane.getLayoutY());
    coBorrowerFields.clear();
    coBorrowerCounter = initialCoBorrowerCounter;
  }

  @FXML
  private void onClickCancel(ActionEvent event) throws IOException {
    customerController.setUpViewCustomer(customer.getId());
    resetScene();
    Main.setScene(AppScene.CUSTOMER);
  }

  @FXML
  private void onClickSave(ActionEvent event) throws IOException {
    String customerID = customer.getId();
    if (checkFields() && validateFields()) {
      Loan loan =
          new Loan(
              null,
              customerID,
              CustomerLoader.loadData(customerID).getName(),
              "Active",
              Double.parseDouble(((TextField) loanDetailsFields.get("principalField")).getText()),
              Double.parseDouble(((TextField) loanDetailsFields.get("rateValueField")).getText()),
              RateType.valueOf(
                  (((ComboBox) loanDetailsFields.get("rateTypeBox")).getValue())
                      .toString()
                      .replaceAll("\\s", "")),
              ((DatePicker) loanDetailsFields.get("startDatePicker")).getValue(),
              Integer.parseInt(((TextField) loanDetailsFields.get("periodField")).getText()),
              Integer.parseInt(((TextField) loanDetailsFields.get("loanTermField")).getText()),
              Double.parseDouble(
                  ((TextField) loanDetailsFields.get("paymentAmountField")).getText()),
              Frequency.valueOf(
                  (((ComboBox) loanDetailsFields.get("paymentFrequencyBox")).getValue())
                      .toString()),
              Frequency.valueOf(
                  (((ComboBox) loanDetailsFields.get("compoundingBox")).getValue()).toString()));

      LoanUpdater.updateData(null, loan);

      String loanId = loan.getLoanId();

      for (String id : coBorrowerIds) {
        CoborrowerUpdater.updateData(loanId, id, null);
      }
      Alert successAlert = new Alert(AlertType.INFORMATION);
      successAlert.setTitle("Success");
      successAlert.setHeaderText("Loan has been added");
      if (successAlert.showAndWait().get() == ButtonType.OK) {
        resetScene();
        loanDetailsController.setLoanDetails(loanId);
        Main.setScene(AppScene.LOAN_DETAILS);
      }
    } else {
      showAlert();
    }
  }

  @FXML
  private void onClickHome(ActionEvent event) throws IOException {
    resetScene();
    Main.setScene(SceneManager.AppScene.START);
  }

  @FXML
  private void onClickInfo(ActionEvent event) throws IOException {}

  private boolean checkFields() {
    boolean repeatFlag = true;
    for (Node node : loanDetailsFields.values()) {
      try {
        if (isEmpty((Control) node)) {
          repeatFlag = false;
        }
      } catch (Exception e) {
        continue;
      }
    }
    if (!repeatFlag) {
      appendErrorMessage("Please fill in all required fields!\n");
    }

    return repeatFlag;
  }

  private boolean validateFields() {
    boolean principalFlag = validateNumberFormat(principalField, true);
    if (!principalFlag) {
      appendErrorMessage("Principal must be a number!\n");
    }
    boolean rateValueFlag = validateNumberFormat(rateValueField, true);
    if (!rateValueFlag) {
      appendErrorMessage("Rate value must be a number!\n");
    }
    boolean periodFlag = validateNumberFormat(periodField, false);
    if (!periodFlag) {
      appendErrorMessage("Period must be an integer!\n");
    }
    boolean loanTermFlag = validateNumberFormat(loanTermField, false);
    if (!loanTermFlag) {
      appendErrorMessage("Loan term must be an integer!\n");
    }
    boolean paymentAmountFlag = validateNumberFormat(paymentAmountField, true);
    if (!paymentAmountFlag) {
      appendErrorMessage("Payment amount must be a number!\n");
    }
    boolean startDateFlag = validateDateFormat(startDatePicker, false);
    if (!startDateFlag) {
      appendErrorMessage("Start date must be after today!\n");
    }

    return principalFlag
        && rateValueFlag
        && periodFlag
        && loanTermFlag
        && paymentAmountFlag
        && startDateFlag;
  }
}
