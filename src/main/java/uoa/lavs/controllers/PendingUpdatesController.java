package uoa.lavs.controllers;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import uoa.lavs.Main;
import uoa.lavs.SceneManager.AppScene;
import uoa.lavs.dataoperations.customer.*;
import uoa.lavs.dataoperations.loan.CoborrowerUpdater;
import uoa.lavs.dataoperations.loan.LoanUpdater;
import uoa.lavs.models.*;

@Controller
public class PendingUpdatesController {

  @FXML private Button backButton;
  @FXML private Button sendButton;
  @FXML private Label titleLabel;
  @FXML private TableView<PendingUpdateRow> pendingTable;
  @FXML private TableColumn<PendingUpdateRow, String> idColumn;
  @FXML private TableColumn<PendingUpdateRow, Boolean> generalDetailsColumn;
  @FXML private TableColumn<PendingUpdateRow, Boolean> addressColumn;
  @FXML private TableColumn<PendingUpdateRow, Boolean> emailColumn;
  @FXML private TableColumn<PendingUpdateRow, Boolean> phoneColumn;
  @FXML private TableColumn<PendingUpdateRow, Boolean> employerColumn;
  @FXML private TableColumn<PendingUpdateRow, Boolean> loanColumn;
  @FXML private TableColumn<PendingUpdateRow, Boolean> coborrowerColumn;

  @Autowired AddLoanController addLoanController;

  private List<Address> failedAddressUpdates;
  private List<Customer> failedCustomerUpdates;
  private List<String> failedCoborrowerUpdates;
  private List<Loan> failedLoanUpdates;
  private List<Email> failedEmailUpdates;
  private List<Phone> failedPhoneUpdates;
  private List<Employer> failedEmployerUpdates;
  private Set<String> customerIds;

  public void initialize() {
    idColumn.setCellValueFactory(new PropertyValueFactory<>("customerId"));
    setupBooleanColumn(generalDetailsColumn, "generalDetails");
    setupBooleanColumn(addressColumn, "address");
    setupBooleanColumn(emailColumn, "email");
    setupBooleanColumn(phoneColumn, "phoneNumber");
    setupBooleanColumn(employerColumn, "employer");
    setupBooleanColumn(loanColumn, "loan");
    setupBooleanColumn(coborrowerColumn, "coborrower");
  }

  private void setupBooleanColumn(TableColumn<PendingUpdateRow, Boolean> column, String propertyName) {
    column.setCellValueFactory(new PropertyValueFactory<>(propertyName));
    column.setCellFactory(col -> new TableCell<PendingUpdateRow, Boolean>() {
      private final ImageView imageView = new ImageView();
      {
        imageView.setFitHeight(21);
        imageView.setFitWidth(30);
      }

      @Override
      protected void updateItem(Boolean item, boolean empty) {
        super.updateItem(item, empty);
        if (empty) {
          setText(null);
          setGraphic(null);
          setStyle("");
        } else {
          setText(null);
          if (Boolean.TRUE.equals(item)) {
            imageView.setImage(new Image(getClass().getResourceAsStream("/images/pending/push.png")));
            setStyle("-fx-background-color: #2da44e;");
          } else {
            imageView.setImage(new Image(getClass().getResourceAsStream("/images/pending/no-change.png")));
            setStyle("-fx-background-color: #d43943;");
          }
          setGraphic(imageView);
          setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
          setAlignment(Pos.CENTER);
        }
      }
    });
  }


  @FXML
  private void onClickBack(ActionEvent event) throws IOException {
    Main.setScene(AppScene.START);
  }

  @FXML
  private void onClickSend(ActionEvent event) {
    CustomerUpdater.retryFailedUpdates();
    EmailUpdater.retryFailedUpdates();
    AddressUpdater.retryFailedUpdates();
    PhoneUpdater.retryFailedUpdates();
    EmployerUpdater.retryFailedUpdates();
    LoanUpdater.retryFailedUpdates();
    CoborrowerUpdater.retryFailedUpdates();
    populatePendingTable();
    setTitle();
  }

  public void retrieveAllFailedUpdates() {
    failedAddressUpdates = AddressUpdater.getFailedUpdates();
    failedCustomerUpdates = CustomerUpdater.getFailedUpdates();
    failedCoborrowerUpdates = CoborrowerUpdater.getFailedUpdates();
    failedLoanUpdates = LoanUpdater.getFailedUpdates();
    failedEmailUpdates = EmailUpdater.getFailedUpdates();
    failedPhoneUpdates = PhoneUpdater.getFailedUpdates();
    failedEmployerUpdates = EmployerUpdater.getFailedUpdates();
  }

  public void populatePendingTable() {
    retrieveAllFailedUpdates();
    ObservableList<PendingUpdateRow> rows = FXCollections.observableArrayList();
    customerIds = retrieveAllFailedIds();
    for (String customerId : customerIds) {

      // Check for failed updates related to this customer
      boolean customerFailed = failedCustomerUpdates.stream().anyMatch(customer -> customer.getId().equals(customerId));
      boolean addressFailed = failedAddressUpdates.stream().anyMatch(address -> address.getCustomerId().equals(customerId));
      boolean emailFailed = failedEmailUpdates.stream().anyMatch(email -> email.getCustomerId().equals(customerId));
      boolean phoneFailed = failedPhoneUpdates.stream().anyMatch(phone -> phone.getCustomerId().equals(customerId));
      boolean employerFailed = failedEmployerUpdates.stream().anyMatch(employer -> employer.getCustomerId().equals(customerId));
      boolean loanFailed = failedLoanUpdates.stream().anyMatch(loan -> loan.getCustomerId().equals(customerId));
      boolean coborrowerFailed = failedCoborrowerUpdates.stream()
              .anyMatch(coborrowerId -> coborrowerId != null && !coborrowerId.isEmpty() && coborrowerId.charAt(0) == customerId.charAt(0));

      // Create a new row with all the relevant information
      PendingUpdateRow row = new PendingUpdateRow(
              customerId,
              customerFailed,
              addressFailed,
              emailFailed,
              phoneFailed,
              employerFailed,
              loanFailed,
              coborrowerFailed
      );
      rows.add(row);
    }

    pendingTable.setItems(rows);

  }

  public Set<String> retrieveAllFailedIds() {
    customerIds = new HashSet<>();
    retrieveAllFailedUpdates();
    for (Customer customer : failedCustomerUpdates) {
      customerIds.add(customer.getId());
    }

    for (Address address : failedAddressUpdates) {
      customerIds.add(address.getCustomerId());
    }

    for (Email email : failedEmailUpdates) {
      customerIds.add(email.getCustomerId());
    }

    for (Phone phone : failedPhoneUpdates) {
      customerIds.add(phone.getCustomerId());
    }

    for (Employer employer : failedEmployerUpdates) {
      customerIds.add(employer.getCustomerId());
    }

    for (Loan loan : failedLoanUpdates) {
      customerIds.add(loan.getCustomerId());
    }

    for (String coborrower : failedCoborrowerUpdates) {
      if (coborrower != null && !coborrower.isEmpty()) {
        // Takes the first character of the Loan ID string, which corresponds to the customer who owns this loan
        // who has had their co-borrower changed
        customerIds.add(String.valueOf(coborrower.charAt(0)));
      }
    }

    return customerIds;
  }

  private int getNumberOfFailedUpdates() {
    int totalSize = customerIds.size();
    return totalSize;
  }

  public void setTitle() {
    int pendingUpdatesCount = getNumberOfFailedUpdates();
    if (pendingUpdatesCount > 0) {
      String updateText = pendingUpdatesCount == 1 ? " Pending Mainframe Update" : " Pending Mainframe Updates";
      titleLabel.setText(pendingUpdatesCount + updateText);
    } else {
      titleLabel.setText("No Pending Mainframe Updates");
    }
  }
}
