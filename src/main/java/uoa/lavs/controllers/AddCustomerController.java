package uoa.lavs.controllers;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.control.Alert.AlertType;
import org.springframework.stereotype.Controller;
import uoa.lavs.Main;
import uoa.lavs.SceneManager;
import uoa.lavs.dataoperations.customer.AddressUpdater;
import uoa.lavs.dataoperations.customer.CustomerUpdater;
import uoa.lavs.dataoperations.customer.EmailUpdater;
import uoa.lavs.dataoperations.customer.EmployerUpdater;
import uoa.lavs.dataoperations.customer.PhoneUpdater;
import uoa.lavs.models.Address;
import uoa.lavs.models.Customer;
import uoa.lavs.models.Email;
import uoa.lavs.models.Employer;
import uoa.lavs.models.Phone;

@Controller
public class AddCustomerController {

  @FXML private Button homeButton;
  @FXML private Button saveButton;
  @FXML private Button cancelButton;
  @FXML private Button infoButton;

  @FXML private TextField titleField;
  @FXML private TextField familyNameField;
  @FXML private TextField givenNameField;
  @FXML private DatePicker dobPicker;

  @FXML private TextField citizenshipField;
  @FXML private TextField visaField;

  @FXML private TextField addressTypeField;
  @FXML private TextField address1Field;
  @FXML private TextField address2Field;
  @FXML private TextField suburbField;
  @FXML private TextField cityField;
  @FXML private TextField postcodeField;
  @FXML private TextField countryField;
  @FXML private CheckBox isPrimaryAddress;
  @FXML private CheckBox isMailingAddress;

  @FXML private TextField emailField;
  @FXML private CheckBox isPrimaryEmail;
  @FXML private ComboBox<FXCollections> phoneTypeBox;
  @FXML private TextField prefixField;
  @FXML private TextField numberField;
  @FXML private CheckBox isPrimaryNumber;
  @FXML private CheckBox isTextingNumber;

  @FXML private TextField jobField;
  @FXML private TextField companyNameField;
  @FXML private TextField companyAddress1Field;
  @FXML private TextField companyAddress2Field;
  @FXML private TextField companySuburbField;
  @FXML private TextField companyCityField;
  @FXML private TextField companyPostcodeField;
  @FXML private TextField companyCountryField;
  @FXML private TextField employerPhoneField;
  @FXML private TextField employerEmailField;
  @FXML private TextField companyWebsiteField;
  @FXML private CheckBox isOwner;

  @FXML private TextArea notesArea;

  @FXML private TabPane detailsTabPane;

  @FXML private FlowPane addressFlowPane;
  @FXML private Pane addressPane;
  @FXML private AnchorPane addressScrollAnchorPane;

  @FXML private FlowPane emailFlowPane;
  @FXML private Pane emailPane;
  @FXML private FlowPane phoneFlowPane;
  @FXML private Pane phonePane;
  @FXML private AnchorPane contactScrollAnchorPane;

  @FXML private FlowPane employmentFlowPane;
  @FXML private Pane employmentPane;
  @FXML private AnchorPane employmentScrollAnchorPane;

  private int initialCounter = 2;
  private int addressCounter = initialCounter;
  private int phoneCounter = initialCounter;
  private int emailCounter = initialCounter;
  private int employmentCounter = initialCounter;
  private Map<String, TextField> textFields = new HashMap<>();
  private Map<String, CheckBox> checkBoxes = new HashMap<>();
  private Map<String, ComboBox<FXCollections>> comboBoxes = new HashMap<>();

  private Alert alert;
  private StringBuilder errorString;

  @FXML
  private void initialize() {
    alert = new Alert(AlertType.ERROR);
    alert.setTitle("Error");
    alert.setHeaderText("Please fix the following issues:");
    errorString = new StringBuilder();

    addElementsToMap(addressPane);
    addElementsToMap(emailPane);
    addElementsToMap(phonePane);
    addElementsToMap(employmentPane);
  }

  private void addElementsToMap(Pane pane) {
    for (var node : pane.getChildren()) {
      if (node instanceof TextField) {
        textFields.put(node.getId(), (TextField) node);
      }
      else if (node instanceof CheckBox) {
        checkBoxes.put(node.getId(), (CheckBox) node);
      }
      else if (node instanceof ComboBox) {
        comboBoxes.put(node.getId(), (ComboBox<FXCollections>) node);
      }
      else {
        continue;
      }
    }
  }

  @FXML
  private void onClickHome(ActionEvent event) throws IOException {
    clearAllFields();
    resetFieldStyle();
    resetAddresses();
    Main.setScene(SceneManager.AppScene.START);
  }

  private Pane addNewField(Pane pane, int counter) {
    List<Node> nodesCopy = new ArrayList<>(pane.getChildrenUnmodifiable());
    Pane newPane = new Pane();
    newPane.setId(pane.getId() + counter);
    newPane.setPrefWidth(pane.getPrefWidth());
    newPane.setPrefHeight(pane.getPrefHeight());
    for (var node : nodesCopy) {
      if (node instanceof TextField) {
        TextField newTextField = new TextField();
        newTextField.setPromptText(((TextField) node).getPromptText());
        newTextField.setLayoutX(node.getLayoutX());
        newTextField.setLayoutY(node.getLayoutY());
        newTextField.setPrefWidth(((TextField) node).getPrefWidth());
        newTextField.setPrefHeight(((TextField) node).getPrefHeight());
        String newFxId = node.getId() + counter;
        newTextField.setId(newFxId);
        newPane.getChildren().add(newTextField);
        textFields.put(newFxId, newTextField);
      }
      else if (node instanceof CheckBox) {
        CheckBox newCheckBox = new CheckBox(((CheckBox) node).getText());
        newCheckBox.setLayoutX(node.getLayoutX());
        newCheckBox.setLayoutY(node.getLayoutY());
        String newFxId = node.getId() + counter;
        newCheckBox.setId(newFxId);
        newPane.getChildren().add(newCheckBox);
        checkBoxes.put(newFxId, newCheckBox);
      }
      else if (node instanceof ComboBox) {
        ComboBox<FXCollections> newComboBox = new ComboBox<>();
        newComboBox.setPromptText(((ComboBox<FXCollections>) node).getPromptText());
        newComboBox.setItems(FXCollections.observableArrayList(((ComboBox<FXCollections>) node).getItems()));
        newComboBox.setLayoutX(node.getLayoutX());
        newComboBox.setLayoutY(node.getLayoutY());
        newComboBox.setPrefWidth(((ComboBox<FXCollections>) node).getPrefWidth());
        newComboBox.setPrefHeight(((ComboBox<FXCollections>) node).getPrefHeight());
        String newFxId = node.getId() + counter;
        newComboBox.setId(newFxId);
        newPane.getChildren().add(newComboBox);
        comboBoxes.put(newFxId, newComboBox);
      }
      else if (node instanceof Separator) {
        Separator newSeparator = new Separator();
        newSeparator.setPrefWidth(((Separator) node).getPrefWidth());
        newSeparator.setPrefHeight(((Separator) node).getPrefHeight());
        newSeparator.setLayoutX(node.getLayoutX());
        newSeparator.setLayoutY(node.getLayoutY());
        newPane.getChildren().add(newSeparator);
      }
      else {
        continue;
      }
    }
    return newPane;
  }

  @FXML
  private void onClickAddAddress(ActionEvent event) throws IOException {
    Pane newAddressPane = addNewField(addressPane, addressCounter);
    addressCounter++;
    addressScrollAnchorPane.setPrefHeight(addressScrollAnchorPane.getPrefHeight()+newAddressPane.getPrefHeight()+addressFlowPane.getVgap());
    addressFlowPane.getChildren().add(newAddressPane);
  }

  @FXML
  private void onClickAddPhone(ActionEvent event) throws IOException {
    Pane newPhonePane = addNewField(phonePane, phoneCounter);
    phoneCounter++;
    contactScrollAnchorPane.setPrefHeight(contactScrollAnchorPane.getPrefHeight()+newPhonePane.getPrefHeight()+phoneFlowPane.getVgap());
    phoneFlowPane.getChildren().add(newPhonePane);
  }

  @FXML
  private void onClickAddEmail(ActionEvent event) throws IOException {
    Pane newEmailPane = addNewField(emailPane, emailCounter);
    emailCounter++;
    contactScrollAnchorPane.setPrefHeight(contactScrollAnchorPane.getPrefHeight()+newEmailPane.getPrefHeight()+emailFlowPane.getVgap());
    emailFlowPane.getChildren().add(newEmailPane);
  }

  @FXML
  private void onClickAddEmployment(ActionEvent event) throws IOException {
    Pane newEmploymentPane = addNewField(employmentPane, employmentCounter);
    employmentCounter++;
    employmentScrollAnchorPane.setPrefHeight(employmentScrollAnchorPane.getPrefHeight()+newEmploymentPane.getPrefHeight()+employmentFlowPane.getVgap());
    employmentFlowPane.getChildren().add(newEmploymentPane);
  }

  @FXML
  private void onClickSave(ActionEvent event) {
    if (checkFields() && validateFields()) {
      try {
        Customer customer =
            new Customer(
                null,
                titleField.getText(),
                givenNameField.getText() + " " + familyNameField.getText(),
                dobPicker.getValue(),
                jobField.getText(),
                citizenshipField.getText(),
                visaField.getText().isEmpty() ? null : visaField.getText(),
                "Active",
                notesArea.getText());
        CustomerUpdater.updateData(null, customer);

        String customerID = customer.getId();

        Address address =
            new Address(
                customerID,
                addressTypeField.getText(),
                address1Field.getText(),
                address2Field.getText(),
                suburbField.getText(),
                cityField.getText(),
                postcodeField.getText(),
                countryField.getText(),
                isPrimaryAddress.isSelected(),
                isMailingAddress.isSelected());
        AddressUpdater.updateData(customerID, address);

        Email email = new Email(customerID, emailField.getText(), isPrimaryEmail.isSelected());
        EmailUpdater.updateData(customerID, email);

        Phone phone =
            new Phone(
                customerID,
                (String) (Object) phoneTypeBox.getValue(), // Some wild casting here but it works :)
                prefixField.getText(),
                numberField.getText(),
                isPrimaryNumber.isSelected(),
                isTextingNumber.isSelected());
        PhoneUpdater.updateData(customerID, phone);

        Employer employer =
            new Employer(
                customerID,
                companyNameField.getText(),
                companyAddress1Field.getText(),
                companyAddress2Field.getText(),
                companySuburbField.getText(),
                companyCityField.getText(),
                companyPostcodeField.getText(),
                companyCountryField.getText(),
                employerPhoneField.getText(),
                employerEmailField.getText(),
                companyWebsiteField.getText(),
                isOwner.isSelected());
        EmployerUpdater.updateData(customerID, employer);

        Alert successAlert = new Alert(AlertType.INFORMATION);
        successAlert.setTitle("Success");
        successAlert.setHeaderText("Customer has been added");
        if (successAlert.showAndWait().get() == ButtonType.OK) {
          Main.setScene(SceneManager.AppScene.START);
        }
      } catch (Exception e) {
        Alert exceptionAlert = new Alert(AlertType.ERROR);
        exceptionAlert.setTitle("Error");
        exceptionAlert.setHeaderText("An error occurred while saving the customer");
        exceptionAlert.setContentText("Please try again later");
        exceptionAlert.showAndWait();
      }
    } else {
      alert.setContentText(errorString.toString());
      alert.showAndWait();
      // Clears error message
      errorString = new StringBuilder();
    }
  }

  @FXML
  private void onClickCancel(ActionEvent event) {
    Alert alertCancel = new Alert(AlertType.CONFIRMATION);
    alertCancel.setTitle("Cancel adding customer");
    alertCancel.setHeaderText("If you cancel, all progress will be lost.");
    alertCancel.setContentText("Are you sure you want to cancel?");
    if (alertCancel.showAndWait().get() == ButtonType.OK) {
      clearAllFields();
      resetFieldStyle();
      Main.setScene(SceneManager.AppScene.START);
    }
  }

  @FXML
  private void onClickInfo(ActionEvent event) {
    System.out.println("Info button clicked");
  }

  private void resetAddresses() {
    Pane temp = addressPane;
    addressFlowPane.getChildren().clear();
    addressFlowPane.getChildren().add(temp);
    addressScrollAnchorPane.setPrefHeight(addressScrollAnchorPane.getPrefHeight()-(addressCounter- initialCounter)*(addressPane.getPrefHeight()+addressFlowPane.getVgap()));
    addressCounter = 1;
    detailsTabPane.getSelectionModel().select(0);
  }

  private void clearAllFields() {
    titleField.clear();
    familyNameField.clear();
    givenNameField.clear();
    dobPicker.setValue(null);
    jobField.clear();
    citizenshipField.clear();
    visaField.clear();
    addressTypeField.clear();
    address1Field.clear();
    address2Field.clear();
    suburbField.clear();
    cityField.clear();
    postcodeField.clear();
    countryField.clear();
    isPrimaryAddress.setSelected(false);
    isMailingAddress.setSelected(false);
    emailField.clear();
    isPrimaryEmail.setSelected(false);
    phoneTypeBox.setValue(null);
    phoneTypeBox.setPromptText("Phone type");
    prefixField.clear();
    numberField.clear();
    isPrimaryNumber.setSelected(false);
    isTextingNumber.setSelected(false);
    jobField.clear();
    companyNameField.clear();
    companyAddress1Field.clear();
    companyAddress2Field.clear();
    companySuburbField.clear();
    companyCityField.clear();
    companyPostcodeField.clear();
    companyCountryField.clear();
    employerPhoneField.clear();
    employerEmailField.clear();
    companyWebsiteField.clear();
    isOwner.setSelected(false);
    notesArea.clear();
  }

  private void resetFieldStyle() {
    titleField.setStyle("-fx-border-color: none");
    familyNameField.setStyle("-fx-border-color: none");
    givenNameField.setStyle("-fx-border-color: none");
    dobPicker.setStyle("-fx-border-color: none");
    citizenshipField.setStyle("-fx-border-color: none");
    visaField.setStyle("-fx-border-color: none");
    addressTypeField.setStyle("-fx-border-color: none");
    address1Field.setStyle("-fx-border-color: none");
    suburbField.setStyle("-fx-border-color: none");
    cityField.setStyle("-fx-border-color: none");
    postcodeField.setStyle("-fx-border-color: none");
    countryField.setStyle("-fx-border-color: none");
    emailField.setStyle("-fx-border-color: none");
    phoneTypeBox.setStyle("-fx-border-color: none");
    prefixField.setStyle("-fx-border-color: none");
    numberField.setStyle("-fx-border-color: none");
    jobField.setStyle("-fx-border-color: none");
    companyNameField.setStyle("-fx-border-color: none");
    companyAddress1Field.setStyle("-fx-border-color: none");
    companySuburbField.setStyle("-fx-border-color: none");
    companyCityField.setStyle("-fx-border-color: none");
    companyPostcodeField.setStyle("-fx-border-color: none");
    companyCountryField.setStyle("-fx-border-color: none");
    employerPhoneField.setStyle("-fx-border-color: none");
    employerEmailField.setStyle("-fx-border-color: none");
    companyWebsiteField.setStyle("-fx-border-color: none");
  }

  private boolean checkFields() {
    boolean titleFieldFlag = checkField(titleField);
    boolean familyNameFieldFlag = checkField(familyNameField);
    boolean givenNameFieldFlag = checkField(givenNameField);
    boolean dobPickerFlag = checkField(dobPicker);
    boolean citizenshipFieldFlag = checkField(citizenshipField);
    boolean addressTypeFieldFlag = checkField(addressTypeField);
    boolean address1FieldFlag = checkField(address1Field);
    boolean suburbFieldFlag = checkField(suburbField);
    boolean cityFieldFlag = checkField(cityField);
    boolean postcodeFieldFlag = checkField(postcodeField);
    boolean countryFieldFlag = checkField(countryField);
    boolean emailFieldFlag = checkField(emailField);
    boolean phoneTypeBoxFlag = checkField(phoneTypeBox);
    boolean prefixFieldFlag = checkField(prefixField);
    boolean numberFieldFlag = checkField(numberField);
    boolean jobFieldFlag = checkField(jobField);
    boolean companyNameFieldFlag = checkField(companyNameField);
    boolean companyAddress1FieldFlag = checkField(companyAddress1Field);
    boolean companySuburbFieldFlag = checkField(companySuburbField);
    boolean companyCityFieldFlag = checkField(companyCityField);
    boolean companyPostcodeFieldFlag = checkField(companyPostcodeField);
    boolean companyCountryFieldFlag = checkField(companyCountryField);
    boolean employerPhoneFieldFlag = checkField(employerPhoneField);
    boolean employerEmailFieldFlag = checkField(employerEmailField);
    boolean companyWebsiteFieldFlag = checkField(companyWebsiteField);

    // Only address line 2 can be empty
    if (titleFieldFlag
        && familyNameFieldFlag
        && givenNameFieldFlag
        && dobPickerFlag
        && citizenshipFieldFlag
        && addressTypeFieldFlag
        && address1FieldFlag
        && suburbFieldFlag
        && cityFieldFlag
        && postcodeFieldFlag
        && countryFieldFlag
        && emailFieldFlag
        && phoneTypeBoxFlag
        && prefixFieldFlag
        && numberFieldFlag
        && jobFieldFlag
        && companyNameFieldFlag
        && companyAddress1FieldFlag
        && companySuburbFieldFlag
        && companyCityFieldFlag
        && companyPostcodeFieldFlag
        && companyCountryFieldFlag
        && employerPhoneFieldFlag
        && employerEmailFieldFlag
        && companyWebsiteFieldFlag) {
      return true;
    }
    errorString.append("\tPlease fill in the required fields\n");

    return false;
  }

  private boolean checkField(Control ui) {
    ui.setStyle("-fx-border-color: none");
    if (ui instanceof TextField) {
      TextField tf = (TextField) ui;
      if (tf.getText().isEmpty()) {
        tf.setStyle("-fx-border-color: red");
        return false;
      }
    }
    if (ui instanceof ComboBox) {
      ComboBox<FXCollections> cb = (ComboBox<FXCollections>) ui;
      if (cb.getValue() == null) {
        cb.setStyle("-fx-border-color: red");
        return false;
      }
    }
    if (ui instanceof DatePicker) {
      DatePicker dp = (DatePicker) ui;
      if (dp.getValue() == null) {
        dp.setStyle("-fx-border-color: red");
        return false;
      }
    }
    return true;
  }

  private boolean validateFields() {
    boolean dobFlag = validate(dobPicker, Type.DATE);
    boolean emailFlag = validate(emailField, Type.EMAIL);
    boolean employerEmailFlag = validate(employerEmailField, Type.EMAIL);
    boolean phonePrefixFlag = validate(prefixField, Type.PHONE);
    boolean phoneFlag = validate(numberField, Type.PHONE);
    boolean websiteFlag = validate(companyWebsiteField, Type.WEBSITE);
    return dobFlag && emailFlag && employerEmailFlag && phonePrefixFlag && phoneFlag && websiteFlag;
  }

  private boolean validate(TextField ui, Type type) {
    boolean flag;

    if (type == Type.EMAIL) {
      // Emails should be in the format of a@b.c
      flag = ui.getText().matches("^.+@.+\\..+$");
      if (!flag) {
        ui.setStyle("-fx-border-color: red");
        errorString.append("\tInvalid email format\n");
      }
    } else if (type == Type.PHONE) {
      // Phone should be numbers
      try {
        Long.parseLong(ui.getText());
        flag = true;
      } catch (Exception e) {
        flag = false;
        ui.setStyle("-fx-border-color: red");
        errorString.append("\tPhone should only contain numbers\n");
      }
    } else if (type == Type.WEBSITE) {
      // Website should start with www. or https://
      flag = ui.getText().matches("^(www\\..+|https://.+)$");
      if (!flag) {
        ui.setStyle("-fx-border-color: red");
        errorString.append("\tInvalid website format\n");
      }
    } else {
      flag = false;
    }

    return flag;
  }

  private boolean validate(DatePicker ui, Type type) {
    boolean flag;
    LocalDate today = LocalDate.now();
    if (today.isBefore((LocalDate) (Object) ui.getValue())) {
      flag = false;
      ui.setStyle("-fx-border-color: red");
      errorString.append("\tDate cannot be before today\n");
    } else {
      flag = true;
    }

    return flag;
  }

  private enum Type {
    DATE,
    EMAIL,
    PHONE,
    WEBSITE
  }
}
