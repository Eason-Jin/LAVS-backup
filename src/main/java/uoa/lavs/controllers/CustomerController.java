package uoa.lavs.controllers;

import java.io.IOException;
import java.util.ArrayList;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Pane;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import uoa.lavs.Main;
import uoa.lavs.SceneManager;
import uoa.lavs.SceneManager.AppScene;
import uoa.lavs.dataoperations.customer.AddressFinder;
import uoa.lavs.dataoperations.customer.AddressUpdater;
import uoa.lavs.dataoperations.customer.CustomerLoader;
import uoa.lavs.dataoperations.customer.CustomerUpdater;
import uoa.lavs.dataoperations.customer.EmailFinder;
import uoa.lavs.dataoperations.customer.EmailUpdater;
import uoa.lavs.dataoperations.customer.EmployerFinder;
import uoa.lavs.dataoperations.customer.EmployerUpdater;
import uoa.lavs.dataoperations.customer.PhoneFinder;
import uoa.lavs.dataoperations.customer.PhoneUpdater;
import uoa.lavs.dataoperations.loan.LoanFinder;
import uoa.lavs.models.Address;
import uoa.lavs.models.Customer;
import uoa.lavs.models.Detail;
import uoa.lavs.models.Email;
import uoa.lavs.models.Employer;
import uoa.lavs.models.Loan;
import uoa.lavs.models.Phone;

@Controller
public class CustomerController extends uoa.lavs.controllers.Controller {
  private enum Setting {
    ADD,
    EDIT,
    VIEW
  }

  private Setting setting;

  @Autowired private AddLoanController addLoanController;
  @Autowired private LoanDetailsController loanDetailsController;

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

  @FXML private Tab loansTab;
  @FXML private Button addLoanButton;
  @FXML private TableView<Loan> loanTable;
  @FXML private TableColumn<Loan, String> loanIdColumn;
  @FXML private TableColumn<Loan, String> statusColumn;
  @FXML private TableColumn<Loan, String> principalColumn;
  @FXML private TableColumn<Loan, String> startDateColumn;
  @FXML private TableColumn<Loan, String> paymentFrequencyColumn;

  private Customer customer;
  private ObservableList<Address> addresses;
  private ObservableList<Email> emails;
  private ObservableList<Phone> phones;
  private ObservableList<Employer> employers;
  private ObservableList<Loan> loans;

  private int addressTableRow = -1;
  private int emailTableRow = -1;
  private int phoneTableRow = -1;
  private int employmentTableRow = -1;

  @FXML
  public void initialize() {
    addressTable.setPlaceholder(new Label("No addresses for this customer"));
    emailTable.setPlaceholder(new Label("No emails for this customer"));
    phoneTable.setPlaceholder(new Label("No phones for this customer"));
    employmentTable.setPlaceholder(new Label("No employments for this customer"));
    loanTable.setPlaceholder(new Label("No loans for this customer"));

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

    addressTable.setRowFactory(
        tableView -> {
          final TableRow<Address> row = new TableRow<Address>();

          row.setOnMouseClicked(
              event -> {
                if (!row.isEmpty() && setting != Setting.VIEW) {
                  try {
                    addressTableRow = row.getIndex();
                    createAddressPopup((Pane) addressTable.getScene().getRoot(), row.getItem());
                  } catch (IOException e) {
                    e.printStackTrace();
                  }
                }
              });
          row.setOnMouseEntered(
                  event -> {
                    if (!row.isEmpty() && setting != Setting.VIEW) {
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

    emailTable.setRowFactory(
        tableView -> {
          final TableRow<Email> row = new TableRow<Email>();

          row.setOnMouseClicked(
              event -> {
                if (!row.isEmpty() && setting != Setting.VIEW) {
                  try {
                    emailTableRow = row.getIndex();
                    createEmailPopup((Pane) emailTable.getScene().getRoot(), row.getItem());
                  } catch (IOException e) {
                    e.printStackTrace();
                  }
                }
              });
          row.setOnMouseEntered(
                  event -> {
                    if (!row.isEmpty() && setting != Setting.VIEW) {
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

    phoneTable.setRowFactory(
        tableView -> {
          final TableRow<Phone> row = new TableRow<Phone>();

          row.setOnMouseClicked(
              event -> {
                if (!row.isEmpty() && setting != Setting.VIEW) {
                  try {
                    phoneTableRow = row.getIndex();
                    createPhonePopup((Pane) phoneTable.getScene().getRoot(), row.getItem());
                  } catch (IOException e) {
                    e.printStackTrace();
                  }
                }
              });
          row.setOnMouseEntered(
                  event -> {
                    if (!row.isEmpty() && setting != Setting.VIEW) {
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

    employmentTable.setRowFactory(
        tableView -> {
          final TableRow<Employer> row = new TableRow<Employer>();

          row.setOnMouseClicked(
              event -> {
                if (!row.isEmpty() && setting != Setting.VIEW) {
                  try {
                    employmentTableRow = row.getIndex();
                    createEmploymentPopup(
                        (Pane) employmentTable.getScene().getRoot(), row.getItem());
                  } catch (IOException e) {
                    e.printStackTrace();
                  }
                }
              });
          row.setOnMouseEntered(
                  event -> {
                    if (!row.isEmpty() && setting != Setting.VIEW) {
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

    loanTable.setRowFactory(
        tableView -> {
          final TableRow<Loan> row = new TableRow<Loan>();

          row.setOnMouseClicked(
              event -> {
                if (!row.isEmpty()) {
                  String loanId = row.getItem().getLoanId();
                  System.out.println("Loan clicked with ID: " + loanId);
                  loanDetailsController.setLoanDetails(loanId);
                  Main.setScene(AppScene.LOAN_DETAILS);
                }
              });
          row.setOnMouseEntered(
                  event -> {
                    if (!row.isEmpty() && setting != Setting.VIEW) {
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

    customer = new Customer();
    addresses = FXCollections.observableArrayList(new ArrayList<Address>());
    emails = FXCollections.observableArrayList(new ArrayList<Email>());
    phones = FXCollections.observableArrayList(new ArrayList<Phone>());
    employers = FXCollections.observableArrayList(new ArrayList<Employer>());
    loans = FXCollections.observableArrayList(new ArrayList<Loan>());
  }

  public void setUpAddCustomer() {
    setting = Setting.ADD;
    titleLabel.setText("Add Customer");
    customerIdLabel.setText("");
    detailsTabPane.getSelectionModel().select(0);
    setDisableForFields(false);
    setVisabilityForButtons(true);
  }

  public void setUpEditCustomer() {
    setting = Setting.EDIT;
    titleLabel.setText("Edit Customer");
    // detailsTabPane.getSelectionModel().select(0);
    setDisableForFields(false);
    setVisabilityForButtons(true);
  }

  public void setUpViewCustomer(String customerId) {
    setting = Setting.VIEW;
    titleLabel.setText("Customer Details");
    detailsTabPane.getSelectionModel().select(0);
    setCustomerDetails(customerId);
    setDisableForFields(true);
    setVisabilityForButtons(false);
  }

  private void setDisableForFields(boolean isDisabled) {
    generalDetailsPane.setDisable(isDisabled);
    notesArea.setDisable(isDisabled);
    loansTab.setDisable(!isDisabled);
  }

  private void setVisabilityForButtons(boolean isVisible) {
    saveButton.setVisible(isVisible);
    addAddressButton.setVisible(isVisible);
    addEmailButton.setVisible(isVisible);
    addPhoneButton.setVisible(isVisible);
    addEmploymentButton.setVisible(isVisible);
    addLoanButton.setVisible(!isVisible);
    editButton.setVisible(!isVisible);
  }

  public void setCustomerDetails(String customerId) {
    customerIdLabel.setText("ID: " + customerId);

    customer = CustomerLoader.loadData(customerId);
    addresses = FXCollections.observableArrayList(AddressFinder.findData(customerId));
    emails = FXCollections.observableArrayList(EmailFinder.findData(customerId));
    phones = FXCollections.observableArrayList(PhoneFinder.findData(customerId));
    employers = FXCollections.observableArrayList(EmployerFinder.findData(customerId));
    loans = FXCollections.observableArrayList(LoanFinder.findData(customerId));

    titleField.setText(customer.getTitle());
    nameField.setText(customer.getName());
    dobPicker.setValue(customer.getDob());
    occupationField.setText(customer.getOccupation());
    citizenshipField.setText(customer.getCitizenship());
    visaField.setText(customer.getVisaType());
    notesArea.setText(customer.getNotes());

    addressTable.setItems(addresses);
    emailTable.setItems(emails);
    phoneTable.setItems(phones);
    employmentTable.setItems(employers);
    loanTable.setItems(loans);
  }

  private void resetScene() {
    // resetFieldStyle();
    titleField.clear();
    nameField.clear();
    dobPicker.setValue(null);
    occupationField.clear();
    citizenshipField.clear();
    visaField.clear();
    notesArea.clear();
    customer = null;
    addresses.clear();
    emails.clear();
    phones.clear();
    employers.clear();
    loans.clear();
    addressTable.getItems().clear();
    emailTable.getItems().clear();
    phoneTable.getItems().clear();
    employmentTable.getItems().clear();
    loanTable.getItems().clear();
  }

  @FXML
  private void onClickAddAddress(ActionEvent event) throws IOException {
    Button sourceButton = (Button) event.getSource();
    Pane currentRoot = (Pane) sourceButton.getScene().getRoot();
    Address address = new Address();
    address.setIsPrimary(false);
    address.setIsMailing(false);
    createAddressPopup(currentRoot, address);
  }

  @FXML
  private void onClickAddEmail(ActionEvent event) throws IOException {
    Button sourceButton = (Button) event.getSource();
    Pane currentRoot = (Pane) sourceButton.getScene().getRoot();
    Email email = new Email();
    email.setIsPrimary(false);
    createEmailPopup(currentRoot, email);
  }

  @FXML
  private void onClickAddPhone(ActionEvent event) throws IOException {
    Button sourceButton = (Button) event.getSource();
    Pane currentRoot = (Pane) sourceButton.getScene().getRoot();
    Phone phone = new Phone();
    phone.setIsPrimary(false);
    phone.setCanSendText(false);
    createPhonePopup(currentRoot, phone);
  }

  @FXML
  private void onClickAddEmployment(ActionEvent event) throws IOException {
    Button sourceButton = (Button) event.getSource();
    Pane currentRoot = (Pane) sourceButton.getScene().getRoot();
    Employer employment = new Employer();
    createEmploymentPopup(currentRoot, employment);
  }

  @FXML
  private void onClickAddLoan(ActionEvent event) {
    addLoanController.setCustomer(customer);
    addLoanController.setCustomerName();
    addLoanController.addPrimeBorrower();

    resetScene();
    Main.setScene(AppScene.ADD_LOAN);
  }

  private void handleSave(Detail saved) {
    if (saved instanceof Address) {
      Address address = (Address) saved;
      if (addressTableRow != -1) {
        addresses.set(addressTableRow, address);
      } else {
        addresses.add(address);
      }
      addressTableRow = -1;
      addressTable.setItems(addresses);
    } else if (saved instanceof Email) {
      Email email = (Email) saved;
      if (emailTableRow != -1) {
        emails.set(emailTableRow, email);
      } else {
        emails.add(email);
      }
      emailTableRow = -1;
      emailTable.setItems(emails);
    } else if (saved instanceof Phone) {
      Phone phone = (Phone) saved;
      if (phoneTableRow != -1) {
        phones.set(phoneTableRow, phone);
      } else {
        phones.add(phone);
      }
      phoneTableRow = -1;
      phoneTable.setItems(phones);
    } else if (saved instanceof Employer) {
      Employer employer = (Employer) saved;
      if (employmentTableRow != -1) {
        employers.set(employmentTableRow, employer);
      } else {
        employers.add(employer);
      }
      employmentTableRow = -1;
      employmentTable.setItems(employers);
    }
  }

  private FXMLLoader createPopup(String fxmlPath, Pane currentRoot) throws IOException {
    FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
    Parent popupContent = loader.load();
    currentRoot.getChildren().add(popupContent);
    return loader;
  }

  private void createAddressPopup(Pane currentRoot, Address address) throws IOException {
    FXMLLoader loader = createPopup("/fxml/addressPopup.fxml", currentRoot);
    AddressPopupController addressPopupController = loader.getController();
    addressPopupController.setUpPopup(
        address, this::handleSave, doesPrimaryAddressExist(), doesMailingAddressExist());
  }

  private void createEmailPopup(Pane currentRoot, Email email) throws IOException {
    FXMLLoader loader = createPopup("/fxml/emailPopup.fxml", currentRoot);
    EmailPopupController emailPopupController = loader.getController();
    emailPopupController.setUpPopup(email, this::handleSave, doesPrimaryEmailExist());
  }

  private void createPhonePopup(Pane currentRoot, Phone phone) throws IOException {
    FXMLLoader loader = createPopup("/fxml/phonePopup.fxml", currentRoot);
    PhonePopupController phonePopupController = loader.getController();
    phonePopupController.setUpPopup(phone, this::handleSave, doesPrimaryPhoneExist());
  }

  private void createEmploymentPopup(Pane currentRoot, Employer employer) throws IOException {
    FXMLLoader loader = createPopup("/fxml/employmentPopup.fxml", currentRoot);
    EmploymentPopupController employmentPopupController = loader.getController();
    employmentPopupController.setUpPopup(employer, this::handleSave);
  }

  public boolean checkFields() {
    boolean titleFlag = !isEmpty(titleField);
    boolean nameFlag = !isEmpty(nameField);
    boolean dobFlag = !isEmpty(dobPicker);
    boolean occupationFlag = !isEmpty(occupationField);
    boolean citizenshipFlag = !isEmpty(citizenshipField);

    if (titleFlag && nameFlag && dobFlag && occupationFlag && citizenshipFlag) {
      return true;
    }
    appendErrorMessage("Please fill in all required fields\n");

    return false;
  }

  public boolean validateFields() {
    boolean dobFlag = validateDateFormat(dobPicker.getValue(), true);
    if (!dobFlag) {
      dobPicker.setStyle(fieldRedBorder);
      appendErrorMessage("Date of birth must be before today\n");
    }
    boolean addressFlag = addresses.size() >= 1;
    if (!addressFlag) {
      appendErrorMessage("Please add at least one address\n");
    }

    boolean contactDetailsFlag = emails.size() + phones.size() >= 1;
    if (!contactDetailsFlag) {
      appendErrorMessage("Please add at least one email or phone\n");
    }

    boolean employersFlag = employers.size() >= 1;
    if (!employersFlag) {
      employmentTable.setStyle(tableRedBorder);
      appendErrorMessage("Please add at least one employer\n");
    }

    // Only one address can be primary, need at least one mailing address
    int primaryAddressNum = 0;
    int mailingAddressNum = 0;
    for (Address address : addresses) {
      if (address.getIsPrimary()) {
        primaryAddressNum++;
      }
      if (address.getIsMailing()) {
        mailingAddressNum++;
      }
    }
    if (primaryAddressNum == 0) {
      if (errorMessage.indexOf("Please select a primary address") == -1) {
        errorMessage.append("Please select a primary address\n");
      }
    }
    if (mailingAddressNum == 0) {
      if (errorMessage.indexOf("Please select a mailing address") == -1) {
        errorMessage.append("Please select a mailing address\n");
      }
    }
    if (!addressFlag || primaryAddressNum == 0 || mailingAddressNum == 0) {
      addressTable.setStyle(tableRedBorder);
    }

    // Only one email can be primary
    int primaryEmailNum = 0;
    for (Email email : emails) {
      if (email.getIsPrimary()) {
        primaryEmailNum++;
      }
    }
    if (primaryEmailNum == 0) {
      appendErrorMessage("Please select a primary email\n");
    }
    if (!contactDetailsFlag || primaryEmailNum == 0) {
      emailTable.setStyle(tableRedBorder);
    }

    // Only one phone can be primary, need at least one texting phone
    int primaryPhoneNum = 0;
    int textingPhoneNum = 0;
    for (Phone phone : phones) {
      if (phone.getIsPrimary()) {
        primaryPhoneNum++;
      }
      if (phone.getCanSendText()) {
        textingPhoneNum++;
      }
    }
    if (primaryPhoneNum == 0) {
      appendErrorMessage("Please select a primary phone\n");
    }
    if (textingPhoneNum == 0) {
      appendErrorMessage("Please select a texting phone\n");
    }
    if (!contactDetailsFlag || primaryPhoneNum == 0 || textingPhoneNum == 0) {
      phoneTable.setStyle(tableRedBorder);
    }

    return dobFlag
        && addressFlag
        && contactDetailsFlag
        && employersFlag
        && primaryAddressNum == 1
        && primaryEmailNum == 1
        && primaryPhoneNum == 1
        && mailingAddressNum >= 1
        && textingPhoneNum >= 1;
  }

  public boolean checkLengths() {
    boolean titleFieldFlag = !isTooLong(titleField.getText(), 10);
    if (titleFieldFlag) {
      titleField.setStyle(fieldRedBorder);
      appendErrorMessage("Title must be less than 10 characters\n");
    }
    boolean nameFieldFlag = !isTooLong(nameField.getText(), 60);
    if (nameFieldFlag) {
      nameField.setStyle(fieldRedBorder);
      appendErrorMessage("Name must be less than 60 characters\n");
    }
    boolean occupationFlag = !isTooLong(occupationField.getText(), 40);
    if (occupationFlag) {
      occupationField.setStyle(fieldRedBorder);
      appendErrorMessage("Occupation must be less than 40 characters\n");
    }
    boolean citizenshipFieldFlag = !isTooLong(citizenshipField.getText(), 40);
    if (citizenshipFieldFlag) {
      citizenshipField.setStyle(fieldRedBorder);
      appendErrorMessage("Citizenship must be less than 40 characters\n");
    }
    boolean visaFieldFlag = !isTooLong(visaField.getText(), 40);
    if (visaFieldFlag) {
      visaField.setStyle(fieldRedBorder);
      appendErrorMessage("Visa type must be less than 40 characters\n");
    }
    boolean notesAreaFlag = !isTooLong(notesArea.getText(), 1330);
    if (notesAreaFlag) {
      notesArea.setStyle(fieldRedBorder);
      appendErrorMessage("Notes must be less than 1330 characters\n");
    }

    return titleFieldFlag
        && nameFieldFlag
        && occupationFlag
        && citizenshipFieldFlag
        && visaFieldFlag
        && notesAreaFlag;
  }

  private void resetFieldStyle() {
    titleField.setStyle(fieldNormalBorder);
    nameField.setStyle(fieldNormalBorder);
    dobPicker.setStyle(fieldNormalBorder);
    occupationField.setStyle(fieldNormalBorder);
    citizenshipField.setStyle(fieldNormalBorder);
    visaField.setStyle(fieldNormalBorder);
    addressTable.setStyle(tableNormalBorder);
    emailTable.setStyle(tableNormalBorder);
    phoneTable.setStyle(tableNormalBorder);
    employmentTable.setStyle(tableNormalBorder);
  }

  @FXML
  private void onClickSave(ActionEvent event) {
    if (checkFields() && validateFields() && checkLengths()) {
      try {
        customer =
            new Customer(
                customer == null ? null : customer.getId(),
                titleField.getText(),
                nameField.getText(),
                dobPicker.getValue(),
                occupationField.getText(),
                citizenshipField.getText(),
                visaField.getText().isEmpty() ? null : visaField.getText(),
                "Active",
                notesArea.getText());

        CustomerUpdater.updateData(customer.getId(), customer);

        String customerId = customer.getId();

        for (Address address : addresses) {
          address.setCustomerId(customerId);
          AddressUpdater.updateData(customerId, address);
        }

        for (Email email : emails) {
          email.setCustomerId(customerId);
          EmailUpdater.updateData(customerId, email);
        }

        for (Phone phone : phones) {
          phone.setCustomerId(customerId);
          PhoneUpdater.updateData(customerId, phone);
        }

        for (Employer employer : employers) {
          employer.setCustomerId(customerId);
          EmployerUpdater.updateData(customerId, employer);
        }

        Alert successAlert = new Alert(AlertType.INFORMATION);
        successAlert.setTitle("Success");
        successAlert.setHeaderText(
            setting == Setting.ADD ? "Customer has been added" : "Customer has been updated");
        successAlert.setContentText(CustomerUpdater.message.toString());
        if (successAlert.showAndWait().get() == ButtonType.OK) {
          resetFieldStyle();
          setUpViewCustomer(customerId);
        }
      } catch (Exception e) {
        System.out.println(e.getLocalizedMessage());
        Alert exceptionAlert = new Alert(AlertType.ERROR);
        exceptionAlert.setTitle("Error");
        exceptionAlert.setHeaderText("An error occurred while saving the customer");
        exceptionAlert.setContentText("Please try again later");
        exceptionAlert.showAndWait();
      }
    } else {
      alert.setContentText(errorMessage.toString());
      alert.showAndWait();
      // Clears error message
      errorMessage = new StringBuilder();
    }
  }

  @FXML
  private void onClickCancel(ActionEvent event) {
    Alert alertCancel = new Alert(AlertType.CONFIRMATION);
    alertCancel.setHeaderText("If you cancel, all progress will be lost.");
    alertCancel.setContentText("Are you sure you want to cancel?");

    switch (setting) {
      case ADD:
        alertCancel.setTitle("Cancel adding customer");
        if (alertCancel.showAndWait().get() == ButtonType.OK) {
          resetScene();
          Main.setScene(SceneManager.AppScene.START);
        }
        break;
      case EDIT:
        alertCancel.setTitle("Cancel editing customer");
        if (alertCancel.showAndWait().get() == ButtonType.OK) {
          resetScene();
          setUpViewCustomer(customer.getId());
        }
        break;
      case VIEW:
        resetScene();
        Main.setScene(AppScene.START);
        break;
      default:
        break;
    }
  }

  @FXML
  private void onClickEdit(ActionEvent event) {
    setUpEditCustomer();
  }

  @FXML
  private void onClickHome(ActionEvent event) {
    resetScene();
    Main.setScene(AppScene.START);
  }

  public String getCustomerID() {
    return customer.getId();
  }

  private boolean doesPrimaryAddressExist() {
    for (Address address : addresses) {
      if (address.getIsPrimary()) {
        return true;
      }
    }
    return false;
  }

  private boolean doesMailingAddressExist() {
    for (Address address : addresses) {
      if (address.getIsMailing()) {
        return true;
      }
    }
    return false;
  }

  private boolean doesPrimaryEmailExist() {
    for (Email email : emails) {
      if (email.getIsPrimary()) {
        return true;
      }
    }
    return false;
  }

  private boolean doesPrimaryPhoneExist() {
    for (Phone phone : phones) {
      if (phone.getIsPrimary()) {
        return true;
      }
    }
    return false;
  }
}
