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

  @Autowired private LoanController loanController;

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
    addressTypeColumn.setReorderable(false);
    streetAddressColumn.setCellValueFactory(
        new PropertyValueFactory<Address, String>("streetAddress"));
    startDateColumn.setReorderable(false);
    suburbColumn.setCellValueFactory(new PropertyValueFactory<Address, String>("suburb"));
    suburbColumn.setReorderable(false);
    cityColumn.setCellValueFactory(new PropertyValueFactory<Address, String>("city"));
    cityColumn.setReorderable(false);
    postcodeColumn.setCellValueFactory(new PropertyValueFactory<Address, String>("postCode"));
    postcodeColumn.setReorderable(false);
    countryColumn.setCellValueFactory(new PropertyValueFactory<Address, String>("country"));
    countryColumn.setReorderable(false);
    primaryAddressColumn.setCellValueFactory(
        new PropertyValueFactory<Address, String>("isPrimary"));
    primaryAddressColumn.setReorderable(false);
    mailingAddressColumn.setCellValueFactory(
        new PropertyValueFactory<Address, String>("isMailing"));
    mailingAddressColumn.setReorderable(false);

    emailAddressColumn.setCellValueFactory(new PropertyValueFactory<Email, String>("address"));
    emailAddressColumn.setReorderable(false);
    primaryEmailColumn.setCellValueFactory(new PropertyValueFactory<Email, String>("isPrimary"));
    primaryEmailColumn.setReorderable(false);

    phoneTypeColumn.setCellValueFactory(new PropertyValueFactory<Phone, String>("type"));
    phoneTypeColumn.setReorderable(false);
    prefixColumn.setCellValueFactory(new PropertyValueFactory<Phone, String>("prefix"));
    prefixColumn.setReorderable(false);
    phoneNumberColumn.setCellValueFactory(new PropertyValueFactory<Phone, String>("phoneNumber"));
    phoneNumberColumn.setReorderable(false);
    primaryPhoneColumn.setCellValueFactory(new PropertyValueFactory<Phone, String>("isPrimary"));
    primaryPhoneColumn.setReorderable(false);
    textingPhoneColumn.setCellValueFactory(new PropertyValueFactory<Phone, String>("canSendText"));
    textingPhoneColumn.setReorderable(false);

    nameColumn.setCellValueFactory(new PropertyValueFactory<Employer, String>("name"));
    nameColumn.setReorderable(false);
    employmentStreetAddressColumn.setCellValueFactory(
        new PropertyValueFactory<Employer, String>("streetAddress"));
    employmentStreetAddressColumn.setReorderable(false);
    employmentPhoneNumberColumn.setCellValueFactory(
        new PropertyValueFactory<Employer, String>("phoneNumber"));
    employmentPhoneNumberColumn.setReorderable(false);
    employmentEmailAddressColumn.setCellValueFactory(
        new PropertyValueFactory<Employer, String>("emailAddress"));
    employmentEmailAddressColumn.setReorderable(false);
    websiteColumn.setCellValueFactory(new PropertyValueFactory<Employer, String>("website"));
    websiteColumn.setReorderable(false);
    ownerColumn.setCellValueFactory(new PropertyValueFactory<Employer, String>("isOwner"));
    ownerColumn.setReorderable(false);

    loanIdColumn.setCellValueFactory(new PropertyValueFactory<Loan, String>("loanId"));
    loanIdColumn.setReorderable(false);
    statusColumn.setCellValueFactory(new PropertyValueFactory<Loan, String>("status"));
    statusColumn.setReorderable(false);
    principalColumn.setCellValueFactory(new PropertyValueFactory<Loan, String>("principalString"));
    principalColumn.setReorderable(false);
    startDateColumn.setCellValueFactory(new PropertyValueFactory<Loan, String>("startDate"));
    startDateColumn.setReorderable(false);
    paymentFrequencyColumn.setCellValueFactory(
        new PropertyValueFactory<Loan, String>("paymentFrequency"));
    paymentFrequencyColumn.setReorderable(false);

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
                  loanController.setUpViewLoan(loanId);
                  resetScene();
                  Main.setScene(AppScene.LOAN);
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
    setDisableForFields(false);
    setVisabilityForButtons(true);
    clearNotFoundFields();
  }

  public void setUpViewCustomer(String customerId) {
    setting = Setting.VIEW;
    titleLabel.setText("Customer Details");
    detailsTabPane.getSelectionModel().select(0);
    setCustomerDetails(customerId);
    setDisableForFields(true);
    setVisabilityForButtons(false);
  }

  private void clearNotFoundFields() {
    titleField.setText(titleField.getText().equals(missingDataMessage) ? "" : titleField.getText());
    nameField.setText(nameField.getText().equals(missingDataMessage) ? "" : nameField.getText());
    if (dobPicker.getValue() == null && dobPicker.getPromptText().equals(missingDataMessage)) {
      dobPicker.setPromptText("");
    } else {
      dobPicker.setValue(dobPicker.getValue());
    }
    occupationField.setText(occupationField.getText().equals(missingDataMessage) ? "" : occupationField.getText());
    citizenshipField.setText(citizenshipField.getText().equals(missingDataMessage) ? "" : citizenshipField.getText());
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
    if (customerId != null) {
      customerIdLabel.setText("ID: " + customerId);
    } else {
      System.out.println("customerId provided is null");
    }

    customer = CustomerLoader.loadData(customerId);
    addresses = FXCollections.observableArrayList(AddressFinder.findData(customerId));
    emails = FXCollections.observableArrayList(EmailFinder.findData(customerId));
    phones = FXCollections.observableArrayList(PhoneFinder.findData(customerId));
    employers = FXCollections.observableArrayList(EmployerFinder.findData(customerId));
    loans = FXCollections.observableArrayList(LoanFinder.findData(customerId));

    if (customer != null) {
      titleField.setText(customer.getTitle() != null ? customer.getTitle() : missingDataMessage);
      nameField.setText(customer.getName() != null ? customer.getName() : missingDataMessage);
      if (customer.getDob() != null) {
        dobPicker.setValue(customer.getDob());
      } else {
        dobPicker.setValue(null);
        dobPicker.setPromptText(missingDataMessage);
      }
      occupationField.setText(customer.getOccupation() != null ? customer.getOccupation() : missingDataMessage);
      citizenshipField.setText(customer.getCitizenship() != null ? customer.getCitizenship() : missingDataMessage);
      visaField.setText(customer.getVisaType());
      notesArea.setText(customer.getNotes() != null ? customer.getNotes() : missingDataMessage);
    } else {
      titleField.setText(missingDataMessage);
      nameField.setText(missingDataMessage);
      dobPicker.setValue(null);
      dobPicker.setPromptText(missingDataMessage);
      occupationField.setText(missingDataMessage);
      citizenshipField.setText(missingDataMessage);
      visaField.setText(missingDataMessage);
      notesArea.setText(missingDataMessage);
    }

    addressTable.setItems(addresses);
    emailTable.setItems(emails);
    phoneTable.setItems(phones);
    employmentTable.setItems(employers);
    loanTable.setItems(loans);
  }

  private void resetScene() {
    resetFieldStyle();
    titleField.clear();
    nameField.clear();
    dobPicker.setValue(null);
    occupationField.clear();
    citizenshipField.clear();
    visaField.clear();
    notesArea.clear();
    customer = new Customer();
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
    employment.setIsOwner(false);
    createEmploymentPopup(currentRoot, employment);
  }

  @FXML
  private void onClickAddLoan(ActionEvent event) {
    loanController.setUpAddLoan(customer.getId(), customer.getName());
    resetScene();
    Main.setScene(AppScene.LOAN);
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
    addressPopupController.setUpPopup(address, this::handleSave, doesPrimaryAddressExist());
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
    employmentPopupController.setUpPopup(employer, this::handleSave, null);
  }

  private boolean checkFields() {
    boolean titleFlag = !isEmpty(titleField);
    boolean nameFlag = !isEmpty(nameField);
    boolean dobFlag = !isEmpty(dobPicker);
    boolean occupationFlag = !isEmpty(occupationField);
    boolean citizenshipFlag = !isEmpty(citizenshipField);

    if (titleFlag && nameFlag && dobFlag && occupationFlag && citizenshipFlag) {
      return true;
    }
    appendErrorMessage("Please fill in all required fields!\n");

    return false;
  }

  private boolean validateFields() {
    boolean dobFlag = validateDateFormat(dobPicker, true);
    if (!dobFlag) {
      dobPicker.getStyleClass().add("invalid");
      appendErrorMessage("Date of birth must be before today!\n");
    }
    boolean addressFlag = addresses.size() >= 1;
    if (!addressFlag) {
      appendErrorMessage("Please add at least one address!\n");
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
    boolean primaryAddressFlag = true;
    if (primaryAddressNum == 0 && addresses.size() > 0) {
      primaryAddressFlag = false;
      appendErrorMessage("Please select a primary address!\n");
    }
    boolean mailingAddressFlag = true;
    if (mailingAddressNum == 0 && addresses.size() > 0) {
      mailingAddressFlag = false;
      appendErrorMessage("Please select a mailing address!\n");
    }
    if (!addressFlag || !primaryAddressFlag || !mailingAddressFlag) {
      addressTable.getStyleClass().add("invalid");
      System.out.println(addressTable.getStyleClass());
    }

    boolean contactDetailsFlag = false;

    boolean primaryEmailFlag = true;
    if (emails.size() > 0) {
      contactDetailsFlag = true;
      primaryEmailFlag = false;
      for (Email email : emails) {
        if (email.getIsPrimary()) {
          primaryEmailFlag = true;
        }
      }
      if (!primaryEmailFlag) {
        emailTable.getStyleClass().add("invalid");
        appendErrorMessage("Please select a primary email!\n");
      }
    }
    boolean primaryPhoneFlag = true;
    boolean textingPhoneFlag = true;
    if (phones.size() > 0) {
      contactDetailsFlag = true;
      primaryPhoneFlag = false;
      textingPhoneFlag = false;
      for (Phone phone : phones) {
        if (phone.getIsPrimary()) {
          primaryPhoneFlag = true;
        }
        if (phone.getCanSendText()) {
          textingPhoneFlag = true;
        }
      }
      if (!primaryPhoneFlag) {
        phoneTable.getStyleClass().add("invalid");
        appendErrorMessage("Please select a primary phone!\n");
      }
      if (!textingPhoneFlag) {
        phoneTable.getStyleClass().add("invalid");
        appendErrorMessage("Please select a texting phone!\n");
      }
    }

    if (!contactDetailsFlag) {
      emailTable.getStyleClass().add("invalid");
      phoneTable.getStyleClass().add("invalid");
      appendErrorMessage("Please add at least one email or phone number!\n");
    }

    return dobFlag
        && addressFlag
        && primaryAddressFlag
        && mailingAddressFlag
        && contactDetailsFlag
        && primaryEmailFlag
        && primaryPhoneFlag
        && textingPhoneFlag;
  }

  private boolean checkLengths() {
    boolean titleFieldFlag = isTooLong(titleField, 10);
    if (titleFieldFlag) {
      titleField.getStyleClass().add("invalid");
      appendErrorMessage("Title must be less than 10 characters\n");
    }
    boolean nameFieldFlag = isTooLong(nameField, 60);
    if (nameFieldFlag) {
      nameField.getStyleClass().add("invalid");
      appendErrorMessage("Name must be less than 60 characters\n");
    }
    boolean occupationFlag = isTooLong(occupationField, 40);
    if (occupationFlag) {
      occupationField.getStyleClass().add("invalid");
      appendErrorMessage("Occupation must be less than 40 characters\n");
    }
    boolean citizenshipFieldFlag = isTooLong(citizenshipField, 40);
    if (citizenshipFieldFlag) {
      citizenshipField.getStyleClass().add("invalid");
      appendErrorMessage("Citizenship must be less than 40 characters\n");
    }
    boolean visaFieldFlag = isTooLong(visaField, 40);
    if (visaFieldFlag) {
      visaField.getStyleClass().add("invalid");
      appendErrorMessage("Visa type must be less than 40 characters\n");
    }
    boolean notesAreaFlag = isTooLong(notesArea, 1330);
    if (notesAreaFlag) {
      notesArea.getStyleClass().add("invalid");
      appendErrorMessage("Notes must be less than 1330 characters\n");
    }

    return !titleFieldFlag
        && !nameFieldFlag
        && !occupationFlag
        && !citizenshipFieldFlag
        && !visaFieldFlag
        && !notesAreaFlag;
  }

  private void resetFieldStyle() {
    titleField.getStyleClass().remove("invalid");
    nameField.getStyleClass().remove("invalid");
    dobPicker.getStyleClass().remove("invalid");
    occupationField.getStyleClass().remove("invalid");
    citizenshipField.getStyleClass().remove("invalid");
    visaField.getStyleClass().remove("invalid");
    addressTable.getStyleClass().remove("invalid");
    emailTable.getStyleClass().remove("invalid");
    phoneTable.getStyleClass().remove("invalid");
    employmentTable.getStyleClass().remove("invalid");
    notesArea.getStyleClass().remove("invalid");
  }

  @FXML
  private void onClickSave(ActionEvent event) {
    resetFieldStyle();
    if (checkFields() && checkLengths() && validateFields()) {
      try {
        customer =
            new Customer(
                customer == null ? null : customer.getId(),
                titleField.getText(),
                nameField.getText(),
                dobPicker.getValue(),
                occupationField.getText(),
                citizenshipField.getText(),
                visaField.getText(),
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
          resetFieldStyle();
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

  private boolean doesTextingPhoneExist() {
    for (Phone phone : phones) {
      if (phone.getCanSendText()) {
        return true;
      }
    }
    return false;
  }
}
