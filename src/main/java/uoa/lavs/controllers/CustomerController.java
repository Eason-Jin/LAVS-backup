package uoa.lavs.controllers;

import java.util.ArrayList;
import java.util.List;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Pane;

import org.springframework.stereotype.Controller;
import uoa.lavs.Main;
import uoa.lavs.SceneManager.AppScene;
import uoa.lavs.dataoperations.customer.AddressFinder;
import uoa.lavs.dataoperations.customer.CustomerLoader;
import uoa.lavs.dataoperations.customer.EmailFinder;
import uoa.lavs.dataoperations.customer.EmployerFinder;
import uoa.lavs.dataoperations.customer.PhoneFinder;
import uoa.lavs.dataoperations.loan.LoanFinder;
import uoa.lavs.models.Address;
import uoa.lavs.models.Customer;
import uoa.lavs.models.Email;
import uoa.lavs.models.Employer;
import uoa.lavs.models.Loan;
import uoa.lavs.models.Phone;

@Controller
public class CustomerController {
  @FXML private Button homeButton;
  @FXML private Button saveButton;
  @FXML private Button cancelButton;
  @FXML private Button editButton;

  @FXML private Label titleLabel;
  @FXML private Label customerIdLabel;

  @FXML private TabPane detailsTabPane;

  @FXML private Pane generalDetailsPane;

  @FXML private TextField titleField;
  @FXML private TextField nameField;
  @FXML private DatePicker dobPicker;
  @FXML private TextField occupationField;
  @FXML private TextField citizenshipField;
  @FXML private TextField visaField;

  @FXML private TextArea notesArea;

  @FXML private Button addAddressButton;
  @FXML private TableView<Address> addressTable;
  @FXML private TableColumn<Address, String> addressTypeColumn;
  @FXML private TableColumn<Address, String> streetAddressColumn;
  @FXML private TableColumn<Address, String> suburbColumn;
  @FXML private TableColumn<Address, String> cityColumn;
  @FXML private TableColumn<Address, String> postcodeColumn;
  @FXML private TableColumn<Address, String> countryColumn;
  @FXML private TableColumn<Address, String> primaryAddressColumn;
  @FXML private TableColumn<Address, String> mailingAddressColumn;

  @FXML private Button addEmailButton;
  @FXML private TableView<Email> emailTable;
  @FXML private TableColumn<Email, String> emailAddressColumn;
  @FXML private TableColumn<Email, String> primaryEmailColumn;

  @FXML private Button addPhoneButton;
  @FXML private TableView<Phone> phoneTable;
  @FXML private TableColumn<Phone, String> phoneTypeColumn;
  @FXML private TableColumn<Phone, String> prefixColumn;
  @FXML private TableColumn<Phone, String> phoneNumberColumn;
  @FXML private TableColumn<Phone, String> primaryPhoneColumn;
  @FXML private TableColumn<Phone, String> textingPhoneColumn;

  @FXML private Button addEmploymentButton;
  @FXML private TableView<Employer> employmentTable;
  @FXML private TableColumn<Employer, String> nameColumn;
  @FXML private TableColumn<Employer, String> employmentStreetAddressColumn;
  @FXML private TableColumn<Employer, String> employmentPhoneNumberColumn;
  @FXML private TableColumn<Employer, String> employmentEmailAddressColumn;
  @FXML private TableColumn<Employer, String> websiteColumn;
  @FXML private TableColumn<Employer, String> ownerColumn;

  @FXML private Button addLoanButton;
  @FXML private TableView<Loan> loanTable;
  @FXML private TableColumn<Loan, String> loanIdColumn;
  @FXML private TableColumn<Loan, String> statusColumn;
  @FXML private TableColumn<Loan, String> principalColumn;
  @FXML private TableColumn<Loan, String> startDateColumn;
  @FXML private TableColumn<Loan, String> paymentFrequencyColumn;

  private Customer customer;
  private List<Address> addresses;
  private List<Email> emails;
  private List<Phone> phones;
  private List<Employer> employers;
  private List<Loan> loans;

  @FXML
  public void initialize() {
    addressTypeColumn.setCellValueFactory(new PropertyValueFactory<Address, String>("type"));
    streetAddressColumn.setCellValueFactory(
        new PropertyValueFactory<Address, String>("streetAddress"));
    suburbColumn.setCellValueFactory(new PropertyValueFactory<Address, String>("suburb"));
    cityColumn.setCellValueFactory(new PropertyValueFactory<Address, String>("city"));
    postcodeColumn.setCellValueFactory(new PropertyValueFactory<Address, String>("postCode"));
    countryColumn.setCellValueFactory(new PropertyValueFactory<Address, String>("country"));
    primaryAddressColumn.setCellValueFactory(
        new PropertyValueFactory<Address, String>("isPrimary"));
    mailingAddressColumn.setCellValueFactory(
        new PropertyValueFactory<Address, String>("isMailing"));

    emailAddressColumn.setCellValueFactory(new PropertyValueFactory<Email, String>("address"));
    primaryEmailColumn.setCellValueFactory(new PropertyValueFactory<Email, String>("isPrimary"));

    phoneTypeColumn.setCellValueFactory(new PropertyValueFactory<Phone, String>("type"));
    prefixColumn.setCellValueFactory(new PropertyValueFactory<Phone, String>("prefix"));
    phoneNumberColumn.setCellValueFactory(new PropertyValueFactory<Phone, String>("phoneNumber"));
    primaryPhoneColumn.setCellValueFactory(new PropertyValueFactory<Phone, String>("isPrimary"));
    textingPhoneColumn.setCellValueFactory(new PropertyValueFactory<Phone, String>("canSendText"));

    nameColumn.setCellValueFactory(new PropertyValueFactory<Employer, String>("name"));
    employmentStreetAddressColumn.setCellValueFactory(
        new PropertyValueFactory<Employer, String>("streetAddress"));
    employmentPhoneNumberColumn.setCellValueFactory(
        new PropertyValueFactory<Employer, String>("phoneNumber"));
    employmentEmailAddressColumn.setCellValueFactory(
        new PropertyValueFactory<Employer, String>("emailAddress"));
    websiteColumn.setCellValueFactory(new PropertyValueFactory<Employer, String>("website"));
    ownerColumn.setCellValueFactory(new PropertyValueFactory<Employer, String>("isOwner"));

    loanIdColumn.setCellValueFactory(new PropertyValueFactory<Loan, String>("loanId"));
    statusColumn.setCellValueFactory(new PropertyValueFactory<Loan, String>("status"));
    principalColumn.setCellValueFactory(new PropertyValueFactory<Loan, String>("principal"));
    startDateColumn.setCellValueFactory(new PropertyValueFactory<Loan, String>("startDate"));
    paymentFrequencyColumn.setCellValueFactory(
        new PropertyValueFactory<Loan, String>("paymentFrequency"));

    customer = new Customer();
    addresses = new ArrayList<Address>();
    emails = new ArrayList<Email>();
    phones = new ArrayList<Phone>();
    employers = new ArrayList<Employer>();
    loans = new ArrayList<Loan>();
  }

  public void setUpAddCustomer() {
    titleLabel.setText("Add Customer");
    detailsTabPane.getSelectionModel().select(0);
    setDisableForFields(false);
    setVisabilityForButtons(true);
  }

  public void setUpEditCustomer() {
    titleLabel.setText("Edit Customer");
    detailsTabPane.getSelectionModel().select(0);
    setDisableForFields(false);
    setVisabilityForButtons(true);
  }

  public void setUpViewCustomer(String customerId) {
    titleLabel.setText("Customer Details");
    setCustomerDetails(customerId);
    setDisableForFields(true);
    setVisabilityForButtons(false);
  }

  private void setDisableForFields(boolean isDisabled) {
    generalDetailsPane.setDisable(isDisabled);
    notesArea.setDisable(isDisabled);
  }

  private void setVisabilityForButtons(boolean isVisible) {
    saveButton.setVisible(isVisible);
    cancelButton.setVisible(isVisible);
    addAddressButton.setVisible(isVisible);
    addEmailButton.setVisible(isVisible);
    addPhoneButton.setVisible(isVisible);
    addEmploymentButton.setVisible(isVisible);
    addLoanButton.setVisible(!isVisible);
    editButton.setVisible(!isVisible);
  }

  public void setCustomerDetails(String customerId) {
    detailsTabPane.getSelectionModel().select(0);
    customerIdLabel.setText("ID: " + customerId);

    customer = CustomerLoader.loadData(customerId);
    addresses = AddressFinder.findData(customerId);
    emails = EmailFinder.findData(customerId);
    phones = PhoneFinder.findData(customerId);
    employers = EmployerFinder.findData(customerId);
    loans = LoanFinder.findData(customerId);

    titleField.setText(customer.getTitle());
    nameField.setText(customer.getName());
    dobPicker.setValue(customer.getDob());
    occupationField.setText(customer.getOccupation());
    citizenshipField.setText(customer.getCitizenship());
    visaField.setText(customer.getVisaType());
    notesArea.setText(customer.getNotes());

    ObservableList<Address> observableAddresses = FXCollections.observableArrayList(addresses);
    addressTable.setItems(observableAddresses);

    ObservableList<Email> observableEmails = FXCollections.observableArrayList(emails);
    emailTable.setItems(observableEmails);

    ObservableList<Phone> observablePhones = FXCollections.observableArrayList(phones);
    phoneTable.setItems(observablePhones);

    ObservableList<Employer> observableEmployers = FXCollections.observableArrayList(employers);
    employmentTable.setItems(observableEmployers);

    ObservableList<Loan> observableLoans = FXCollections.observableArrayList(loans);
    loanTable.setItems(observableLoans);
  }

  @FXML
  private void onClickAddAddress(ActionEvent event) {
    System.out.println("Add Address clicked");
  }

  @FXML
  private void onClickAddEmail(ActionEvent event) {
    System.out.println("Add Email clicked");
  }

  @FXML
  private void onClickAddPhone(ActionEvent event) {
    System.out.println("Add Phone clicked");
  }

  @FXML
  private void onClickAddEmployment(ActionEvent event) {
    System.out.println("Add Employment clicked");
  }

  @FXML
  private void onClickAddLoan(ActionEvent event) {
    Main.setScene(AppScene.ADD_LOAN);
  }

  @FXML
  private void onClickCancel(ActionEvent event) {
    System.out.println("Cancel clicked");
  }

  @FXML
  private void onClickSave(ActionEvent event) {
    System.out.println("Save clicked");
  }

  @FXML
  private void onClickEdit(ActionEvent event) {
    setUpEditCustomer();
  }

  @FXML
  private void onClickHome(ActionEvent event) {
    Main.setScene(AppScene.START);
  }
}
