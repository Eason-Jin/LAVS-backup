package uoa.lavs.controllers;

import java.io.IOException;
import java.util.List;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import uoa.lavs.Main;
import uoa.lavs.SceneManager.AppScene;
import uoa.lavs.dataoperations.customer.AddressUpdater;
import uoa.lavs.dataoperations.customer.CustomerUpdater;
import uoa.lavs.dataoperations.customer.EmailUpdater;
import uoa.lavs.dataoperations.customer.EmployerUpdater;
import uoa.lavs.dataoperations.customer.PhoneUpdater;
import uoa.lavs.dataoperations.loan.CoborrowerUpdater;
import uoa.lavs.dataoperations.loan.LoanUpdater;
import uoa.lavs.models.Address;
import uoa.lavs.models.Customer;
import uoa.lavs.models.Email;
import uoa.lavs.models.Employer;
import uoa.lavs.models.Loan;
import uoa.lavs.models.Phone;

@Controller
public class PendingUpdatesController {

  @FXML private Button backButton;
  @FXML private Button sendButton;
  @FXML private Label titleLabel;

  @Autowired AddLoanController addLoanController;

  public void initialize() {}

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
    CoborrowerUpdater.retryFailedUpdates();
    LoanUpdater.retryFailedUpdates();
  }

  private int getFailedUpdates() {
    List<Address> failedAddressUpdates = AddressUpdater.getFailedUpdates();
    List<Customer> failedCustomerUpdates = CustomerUpdater.getFailedUpdates();
    List<String> failedCoborrowerUpdates = CoborrowerUpdater.getFailedUpdates();
    List<Loan> failedLoanUpdates = LoanUpdater.getFailedUpdates();
    List<Email> failedEmailUpdates = EmailUpdater.getFailedUpdates();
    List<Phone> failedPhoneUpdates = PhoneUpdater.getFailedUpdates();
    List<Employer> failedEmployerUpdates = EmployerUpdater.getFailedUpdates();

    int totalSize =
        failedAddressUpdates.size()
            + failedCustomerUpdates.size()
            + failedCoborrowerUpdates.size()
            + failedLoanUpdates.size()
            + failedEmailUpdates.size()
            + failedPhoneUpdates.size()
            + failedEmployerUpdates.size();

    return totalSize;
  }

  public void setTitle() {
    int pendingUpdatesCount = getFailedUpdates();
    if (pendingUpdatesCount > 0) {
      titleLabel.setText(pendingUpdatesCount + " Pending Mainframe Updates");
    } else {
      titleLabel.setText("No Pending Mainframe Updates");
    }
  }
}
