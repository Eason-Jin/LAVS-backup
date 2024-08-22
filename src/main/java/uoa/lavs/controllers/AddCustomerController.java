package uoa.lavs.controllers;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.*;
import org.springframework.stereotype.Controller;
import uoa.lavs.Main;
import uoa.lavs.SceneManager;
import uoa.lavs.controllers.interfaces.CheckEmpty;
import uoa.lavs.controllers.interfaces.CheckLength;
import uoa.lavs.controllers.interfaces.ValidateType;
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
public class AddCustomerController implements ValidateType, CheckLength, CheckEmpty {

  @FXML private Button homeButton;
  @FXML private Button saveButton;
  @FXML private Button cancelButton;
  @FXML private Button infoButton;

  @FXML private TextField titleField;
  @FXML private TextField nameField;
  @FXML private DatePicker dobPicker;
  @FXML private TextField jobField;

  @FXML private TextField citizenshipField;
  @FXML private TextField visaField;

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
  private int numAddresses = 1;
  private int numPhones = 1;
  private int numEmails = 1;
  private int numEmployments = 1;
  private Map<String, Node> customerDetailFields = new HashMap<>();

  private String noBorder = "-fx-border-color: none";
  private String redBorder = "-fx-border-color: red";
  private String suffix;

  private Alert alert;
  private StringBuilder errorString;

  @FXML
  private void initialize() {
    alert = new Alert(AlertType.ERROR);
    alert.setTitle("Error");
    alert.setHeaderText("Please fix the following issues:");
    errorString = new StringBuilder();
    addAllElementsToMap();
    disableAllDeleteButtons();
  }

  private void addAllElementsToMap() {
    customerDetailFields.clear();
    addElementsToMap(addressPane);
    addElementsToMap(emailPane);
    addElementsToMap(phonePane);
    addElementsToMap(employmentPane);
  }

  private void addElementsToMap(Pane pane) {
    for (var node : pane.getChildren()) {
      if (node.getId() != null) {
        customerDetailFields.put(node.getId(), node);
      }
    }
    customerDetailFields.put(pane.getId(), pane);
  }

  @FXML
  private void onClickHome(ActionEvent event) throws IOException {
    resetScene();
    Main.setScene(SceneManager.AppScene.START);
  }

  private Pane addNewField(Pane pane, int counter) {
    List<Node> nodesCopy = new ArrayList<>(pane.getChildrenUnmodifiable());
    String counterString;
    if (counter != 0) {
      counterString = "_" + counter;
    } else {
      counterString = "";
    }
    Pane newPane = new Pane();
    newPane.setId(pane.getId() + counterString);
    newPane.setPrefWidth(pane.getPrefWidth());
    newPane.setPrefHeight(pane.getPrefHeight());

    for (var node : nodesCopy) {
      String newFxId = node.getId() + counterString;
      if (node instanceof TextField) {
        TextField newTextField = new TextField();
        newTextField.setPromptText(((TextField) node).getPromptText());
        newTextField.setLayoutX(node.getLayoutX());
        newTextField.setLayoutY(node.getLayoutY());
        newTextField.setPrefWidth(((TextField) node).getPrefWidth());
        newTextField.setPrefHeight(((TextField) node).getPrefHeight());
        newTextField.setId(newFxId);
        newPane.getChildren().add(newTextField);
        customerDetailFields.put(newFxId, newTextField);
      } else if (node instanceof CheckBox) {
        CheckBox newCheckBox = new CheckBox(((CheckBox) node).getText());
        newCheckBox.setLayoutX(node.getLayoutX());
        newCheckBox.setLayoutY(node.getLayoutY());
        EventHandler<ActionEvent> handler = ((CheckBox) node).getOnAction();
        newCheckBox.setOnAction(handler);
        newCheckBox.setId(newFxId);
        newPane.getChildren().add(newCheckBox);
        customerDetailFields.put(newFxId, newCheckBox);
        if (findSelected((CheckBox) node) != null && ((CheckBox) node).getOnAction() != null) {
          disableCheckboxes((CheckBox) customerDetailFields.get(findSelected((CheckBox) node)));
        }
      } else if (node instanceof ComboBox) {
        ComboBox<FXCollections> newComboBox = new ComboBox<>();
        newComboBox.setPromptText(((ComboBox<FXCollections>) node).getPromptText());
        newComboBox.setItems(
            FXCollections.observableArrayList(((ComboBox<FXCollections>) node).getItems()));
        newComboBox.setLayoutX(node.getLayoutX());
        newComboBox.setLayoutY(node.getLayoutY());
        newComboBox.setPrefWidth(((ComboBox<FXCollections>) node).getPrefWidth());
        newComboBox.setPrefHeight(((ComboBox<FXCollections>) node).getPrefHeight());
        newComboBox.setId(newFxId);
        newPane.getChildren().add(newComboBox);
        customerDetailFields.put(newFxId, newComboBox);
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
        customerDetailFields.put(newFxId, newButton);
      } else {
        continue;
      }
    }
    customerDetailFields.put(newPane.getId(), newPane);
    return newPane;
  }

  private String findSelected(CheckBox checkBox) {
    for (String nodeId : customerDetailFields.keySet()) {
      if (nodeId.contains(checkBox.getId())
          && ((CheckBox) customerDetailFields.get(nodeId)).isSelected()) {
        return nodeId;
      }
    }
    return null;
  }

  private void disableCheckboxes(CheckBox checkBox) {
    String selectedFxId = (checkBox).getId();
    String checkingFxId = ((selectedFxId).split("_"))[0];
    for (String nodeId : customerDetailFields.keySet()) {
      if (customerDetailFields
          .get(nodeId)
          .getClass()
          .getName()
          .equals(customerDetailFields.get(selectedFxId).getClass().getName())) {
        if (!(nodeId.equals(selectedFxId)) && nodeId.contains(checkingFxId)) {
          if (checkBox.isSelected()) {
            CheckBox toDisableCheckBox = (CheckBox) customerDetailFields.get(nodeId);
            toDisableCheckBox.setSelected(false);
            toDisableCheckBox.setDisable(true);
            customerDetailFields.replace(nodeId, toDisableCheckBox);
          } else {
            CheckBox disabledCheckBox = (CheckBox) customerDetailFields.get(nodeId);
            disabledCheckBox.setSelected(false);
            disabledCheckBox.setDisable(false);
            customerDetailFields.replace(nodeId, disabledCheckBox);
          }
        }
      }
    }
  }

  @FXML
  private void onUniqueCheckBoxClick(ActionEvent event) {
    CheckBox selectedCheckBox = (CheckBox) event.getSource();
    disableCheckboxes(selectedCheckBox);
  }

  @FXML
  private void onClickDeleteEmail(ActionEvent event) {
    String buttonClickedFxId = ((Button) event.getSource()).getId();
    Pane paneToDelete = deleteField(buttonClickedFxId, emailPane);
    contactScrollAnchorPane.setPrefHeight(
        contactScrollAnchorPane.getPrefHeight()
            - (emailPane.getPrefHeight() + emailFlowPane.getVgap()));
    emailFlowPane.getChildren().remove(paneToDelete);
    numEmails--;
    if (numEmails == 1) {
      disableDeleteButton((buttonClickedFxId.split("_"))[0]);
    }
  }

  @FXML
  private void onClickDeletePhone(ActionEvent event) {
    String buttonClickedFxId = ((Button) event.getSource()).getId();
    Pane paneToDelete = deleteField(buttonClickedFxId, phonePane);
    contactScrollAnchorPane.setPrefHeight(
        contactScrollAnchorPane.getPrefHeight()
            - (phonePane.getPrefHeight() + phoneFlowPane.getVgap()));
    phoneFlowPane.getChildren().remove(paneToDelete);
    numPhones--;
    if (numPhones == 1) {
      disableDeleteButton((buttonClickedFxId.split("_"))[0]);
    }
  }

  @FXML
  private void onClickDeleteAddress(ActionEvent event) {
    String buttonClickedFxId = ((Button) event.getSource()).getId();
    Pane paneToDelete = deleteField(buttonClickedFxId, addressPane);
    addressScrollAnchorPane.setPrefHeight(
        addressScrollAnchorPane.getPrefHeight()
            - (addressPane.getPrefHeight() + addressFlowPane.getVgap()));
    addressFlowPane.getChildren().remove(paneToDelete);
    numAddresses--;
    if (numAddresses == 1) {
      disableDeleteButton((buttonClickedFxId.split("_"))[0]);
    }
  }

  @FXML
  private void onClickDeleteEmployment(ActionEvent event) {
    String buttonClickedFxId = ((Button) event.getSource()).getId();
    Pane paneToDelete = deleteField(buttonClickedFxId, employmentPane);
    employmentScrollAnchorPane.setPrefHeight(
        employmentScrollAnchorPane.getPrefHeight()
            - (employmentPane.getPrefHeight() + employmentFlowPane.getVgap()));
    employmentFlowPane.getChildren().remove(paneToDelete);
    numEmployments--;
    if (numEmployments == 1) {
      disableDeleteButton((buttonClickedFxId.split("_"))[0]);
    }
  }

  private Pane deleteField(String buttonClickedFxId, Pane pane) {
    String paneToDeleteFxId;
    if ((buttonClickedFxId.split("_")).length >= 2) {
      paneToDeleteFxId = pane.getId() + "_" + buttonClickedFxId.split("_")[1];
    } else {
      paneToDeleteFxId = pane.getId();
    }
    Pane paneToDelete = (Pane) customerDetailFields.get(paneToDeleteFxId);
    List<Node> nodesCopy = paneToDelete.getChildrenUnmodifiable();
    for (var node : nodesCopy) {
      if (node instanceof CheckBox && ((CheckBox) node).isSelected()) {
        for (String id : customerDetailFields.keySet()) {
          if (id.contains(((node.getId()).split("_"))[0])) {
            customerDetailFields.get(id).setDisable(false);
          }
        }
      }
      customerDetailFields.remove(node.getId());
    }
    customerDetailFields.remove(paneToDeleteFxId);
    return paneToDelete;
  }

  private void disableDeleteButton(String fxId) {
    for (var nodeId : customerDetailFields.keySet()) {
      if (nodeId.contains(fxId)) {
        Button newButton = (Button) customerDetailFields.get(nodeId);
        newButton.setDisable(true);
        customerDetailFields.replace(nodeId, newButton);
      }
    }
  }

  private void enableDeleteButton(String field) {
    for (String nodeId : customerDetailFields.keySet()) {
      if (nodeId.contains("delete" + field + "Button")) {
        Button newButton = (Button) customerDetailFields.get(nodeId);
        newButton.setDisable(false);
        //        customerDetailFields.replace(nodeId, newButton);
      }
    }
  }

  @FXML
  private void onClickAddAddress(ActionEvent event) throws IOException {
    Pane newAddressPane = addNewField(addressPane, addressCounter);
    addressCounter++;
    numAddresses++;
    if (numAddresses >= 2) {
      enableDeleteButton("Address");
    }
    addressScrollAnchorPane.setPrefHeight(
        addressScrollAnchorPane.getPrefHeight()
            + newAddressPane.getPrefHeight()
            + addressFlowPane.getVgap());
    addressFlowPane.getChildren().add(newAddressPane);
  }

  @FXML
  private void onClickAddPhone(ActionEvent event) throws IOException {
    Pane newPhonePane = addNewField(phonePane, phoneCounter);
    phoneCounter++;
    numPhones++;
    if (numPhones >= 2) {
      enableDeleteButton("Phone");
    }
    contactScrollAnchorPane.setPrefHeight(
        contactScrollAnchorPane.getPrefHeight()
            + newPhonePane.getPrefHeight()
            + phoneFlowPane.getVgap());
    phoneFlowPane.getChildren().add(newPhonePane);
  }

  @FXML
  private void onClickAddEmail(ActionEvent event) throws IOException {
    Pane newEmailPane = addNewField(emailPane, emailCounter);
    emailCounter++;
    numEmails++;
    if (numEmails >= 2) {
      enableDeleteButton("Email");
    }
    contactScrollAnchorPane.setPrefHeight(
        contactScrollAnchorPane.getPrefHeight()
            + newEmailPane.getPrefHeight()
            + emailFlowPane.getVgap());
    emailFlowPane.getChildren().add(newEmailPane);
  }

  @FXML
  private void onClickAddEmployment(ActionEvent event) throws IOException {
    Pane newEmploymentPane = addNewField(employmentPane, employmentCounter);
    employmentCounter++;
    numEmployments++;
    if (numEmployments >= 2) {
      enableDeleteButton("Employment");
    }
    employmentScrollAnchorPane.setPrefHeight(
        employmentScrollAnchorPane.getPrefHeight()
            + newEmploymentPane.getPrefHeight()
            + employmentFlowPane.getVgap());
    employmentFlowPane.getChildren().add(newEmploymentPane);
  }

  @FXML
  private void onClickSave(ActionEvent event) {
    if (checkFields() && validateFields() && checkLengths()) {
      try {
        Customer customer =
            new Customer(
                null,
                titleField.getText(),
                nameField.getText(),
                dobPicker.getValue(),
                jobField.getText(),
                citizenshipField.getText(),
                visaField.getText().isEmpty() ? null : visaField.getText(),
                "Active",
                notesArea.getText());
        CustomerUpdater.updateData(null, customer);

        String customerID = customer.getId();

        for (int i = 1; i < addressCounter; i++) {
          suffix = setSuffix(i);
          Address address =
              new Address(
                  customerID,
                  ((TextField) customerDetailFields.get("addressTypeField" + suffix)).getText(),
                  ((TextField) customerDetailFields.get("address1Field" + suffix)).getText(),
                  ((TextField) customerDetailFields.get("address2Field" + suffix)).getText(),
                  ((TextField) customerDetailFields.get("suburbField" + suffix)).getText(),
                  ((TextField) customerDetailFields.get("cityField" + suffix)).getText(),
                  ((TextField) customerDetailFields.get("postcodeField" + suffix)).getText(),
                  ((TextField) customerDetailFields.get("countryField" + suffix)).getText(),
                  ((CheckBox) customerDetailFields.get("isPrimaryAddress" + suffix)).isSelected(),
                  ((CheckBox) customerDetailFields.get("isMailingAddress" + suffix)).isSelected(),
                  null);
          AddressUpdater.updateData(customerID, address);
        }

        for (int i = 1; i < emailCounter; i++) {
          suffix = setSuffix(i);
          Email email =
              new Email(
                  customerID,
                  ((TextField) customerDetailFields.get("emailField" + suffix)).getText(),
                  ((CheckBox) customerDetailFields.get("isPrimaryEmail" + suffix)).isSelected(),
                  null);
          EmailUpdater.updateData(customerID, email);
        }

        for (int i = 1; i < phoneCounter; i++) {
          suffix = setSuffix(i);
          Phone phone =
              new Phone(
                  customerID,
                  (String)
                      (Object)
                          ((ComboBox) customerDetailFields.get("phoneTypeBox" + suffix)).getValue(),
                  ((TextField) customerDetailFields.get("prefixField" + suffix)).getText(),
                  ((TextField) customerDetailFields.get("numberField" + suffix)).getText(),
                  ((CheckBox) customerDetailFields.get("isPrimaryNumber" + suffix)).isSelected(),
                  ((CheckBox) customerDetailFields.get("isTextingNumber" + suffix)).isSelected(),
                  null);
          PhoneUpdater.updateData(customerID, phone);
        }

        for (int i = 1; i < employmentCounter; i++) {
          suffix = setSuffix(i);
          Employer employer =
              new Employer(
                  customerID,
                  ((TextField) customerDetailFields.get("companyNameField" + suffix)).getText(),
                  ((TextField) customerDetailFields.get("companyAddress1Field" + suffix)).getText(),
                  ((TextField) customerDetailFields.get("companyAddress2Field" + suffix)).getText(),
                  ((TextField) customerDetailFields.get("companySuburbField" + suffix)).getText(),
                  ((TextField) customerDetailFields.get("companyCityField" + suffix)).getText(),
                  ((TextField) customerDetailFields.get("companyPostcodeField" + suffix)).getText(),
                  ((TextField) customerDetailFields.get("companyCountryField" + suffix)).getText(),
                  ((TextField) customerDetailFields.get("employerPhoneField" + suffix)).getText(),
                  ((TextField) customerDetailFields.get("employerEmailField" + suffix)).getText(),
                  ((TextField) customerDetailFields.get("companyWebsiteField" + suffix)).getText(),
                  ((CheckBox) customerDetailFields.get("isOwner" + suffix)).isSelected(),
                  null);
          EmployerUpdater.updateData(customerID, employer);
        }

        Alert successAlert = new Alert(AlertType.INFORMATION);
        successAlert.setTitle("Success");
        successAlert.setHeaderText("Customer has been added");
        if (successAlert.showAndWait().get() == ButtonType.OK) {
          resetScene();
          Main.setScene(SceneManager.AppScene.START);
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
      resetScene();
      Main.setScene(SceneManager.AppScene.START);
    }
  }

  @FXML
  private void onClickInfo(ActionEvent event) {
    System.out.println("Info button clicked");
  }

  private void resetScene() {
    clearAllFields();
    resetFieldStyle();
    resetAllDetailFields();
    disableAllDeleteButtons();
  }

  private void disableAllDeleteButtons() {
    for (String nodeId : customerDetailFields.keySet()) {
      if (nodeId.contains("delete") && nodeId.contains("Button")) {
        Button deleteButton = (Button) customerDetailFields.get(nodeId);
        deleteButton.setDisable(true);
        customerDetailFields.replace(nodeId, deleteButton);
      }
    }
  }

  private void resetDetailFields(
      Pane pane, FlowPane flowPane, AnchorPane scrollAnchorPane, int counter) {
    Pane temp = addNewField(pane, 0);
    flowPane.getChildren().clear();
    flowPane.getChildren().add(temp);
    if (counter > initialCounter) {
      scrollAnchorPane.setPrefHeight(
          scrollAnchorPane.getPrefHeight()
              - (counter - initialCounter) * (pane.getPrefHeight() + flowPane.getVgap()));
    }
  }

  private void resetAllDetailFields() {
    resetDetailFields(addressPane, addressFlowPane, addressScrollAnchorPane, addressCounter);
    addressCounter = initialCounter;
    numAddresses = 1;
    resetDetailFields(phonePane, phoneFlowPane, contactScrollAnchorPane, phoneCounter);
    phoneCounter = initialCounter;
    numPhones = 1;
    resetDetailFields(emailPane, emailFlowPane, contactScrollAnchorPane, emailCounter);
    emailCounter = initialCounter;
    numEmails = 1;
    resetDetailFields(
        employmentPane, employmentFlowPane, employmentScrollAnchorPane, employmentCounter);
    employmentCounter = initialCounter;
    numEmployments = 1;
    detailsTabPane.getSelectionModel().select(0);
  }

  private void clearAllFields() {
    titleField.clear();
    nameField.clear();
    dobPicker.setValue(null);
    jobField.clear();
    citizenshipField.clear();
    visaField.clear();

    for (Node node : customerDetailFields.values()) {
      if (node instanceof TextField) {
        ((TextField) node).clear();
      } else if (node instanceof CheckBox) {
        ((CheckBox) node).setSelected(false);
      } else if (node instanceof ComboBox) {
        // Right now only phoneTypeBox is a ComboBox
        ((ComboBox) node).setValue(null);
        ((ComboBox) node).setPromptText("Phone type");
      }
    }

    notesArea.clear();
  }

  private void resetFieldStyle() {
    titleField.setStyle(noBorder);
    nameField.setStyle(noBorder);
    dobPicker.setStyle(noBorder);
    jobField.setStyle(noBorder);
    citizenshipField.setStyle(noBorder);
    visaField.setStyle(noBorder);
    for (Node node : customerDetailFields.values()) {
      node.setStyle(noBorder);
    }
  }

  @Override
  public boolean checkFields() {
    boolean titleFieldFlag = checkField(titleField);
    boolean nameFieldFlag = checkField(nameField);
    boolean dobPickerFlag = checkField(dobPicker);
    boolean occupationFlag = checkField(jobField);
    boolean citizenshipFieldFlag = checkField(citizenshipField);

    boolean repeatFlag = true;
    for (Node node : customerDetailFields.values()) {
      // Skip address2Field
      if (node.getId().toLowerCase().contains("address2field")) {
        continue;
      }
      try {
        if (!checkField((Control) node)) {
          repeatFlag = false;
        }
      } catch (Exception e) {
        continue;
      }
    }

    // Only address line 2 can be empty
    if (titleFieldFlag
        && nameFieldFlag
        && dobPickerFlag
        && occupationFlag
        && citizenshipFieldFlag
        && repeatFlag) {
      return true;
    }
    errorString.append("\tPlease fill in the required fields\n");

    return false;
  }

  @Override
  public boolean checkField(Control ui) {
    ui.setStyle(noBorder);
    if (ui instanceof TextField) {
      TextField tf = (TextField) ui;
      if (tf.getText().isEmpty()) {
        tf.setStyle(redBorder);
        return false;
      }
    }
    if (ui instanceof ComboBox) {
      ComboBox<FXCollections> cb = (ComboBox<FXCollections>) ui;
      if (cb.getValue() == null) {
        cb.setStyle(redBorder);
        return false;
      }
    }
    if (ui instanceof DatePicker) {
      DatePicker dp = (DatePicker) ui;
      if (dp.getValue() == null) {
        dp.setStyle(redBorder);
        return false;
      }
    }
    return true;
  }

  @Override
  public boolean validateFields() {
    boolean dobFlag = validate(dobPicker, Type.DATE);

    boolean addressFlag = false;
    for (int i = 1; i < addressCounter; i++) {
      suffix = setSuffix(i);
      if (validate((TextField) customerDetailFields.get("postcodeField" + suffix), Type.NUMBER)) {
        addressFlag = true;
      }
    }

    boolean emailFlag = false;
    for (int i = 1; i < emailCounter; i++) {
      suffix = setSuffix(i);
      if (validate((TextField) customerDetailFields.get("emailField" + suffix), Type.EMAIL)) {
        emailFlag = true;
      }
    }

    boolean employerFlag = false;
    for (int i = 1; i < employmentCounter; i++) {
      suffix = setSuffix(i);
      boolean employerEmailFlag =
          validate((TextField) customerDetailFields.get("employerEmailField" + suffix), Type.EMAIL);
      boolean postCodeFlag =
          validate(
              (TextField) customerDetailFields.get("companyPostcodeField" + suffix), Type.NUMBER);
      boolean phoneFlag =
          validate(
              (TextField) customerDetailFields.get("employerPhoneField" + suffix), Type.NUMBER);
      employerFlag = employerEmailFlag && postCodeFlag && phoneFlag;
    }

    boolean phoneFlag = false;
    for (int i = 1; i < phoneCounter; i++) {
      suffix = setSuffix(i);
      boolean prefixFlag =
          validate((TextField) customerDetailFields.get("prefixField" + suffix), Type.NUMBER);
      boolean numberFlag =
          validate((TextField) customerDetailFields.get("numberField" + suffix), Type.NUMBER);
      phoneFlag = prefixFlag && numberFlag;
    }

    boolean websiteFlag = false;
    for (int i = 1; i < employmentCounter; i++) {
      suffix = setSuffix(i);
      if (validate(
          (TextField) customerDetailFields.get("companyWebsiteField" + suffix), Type.WEBSITE)) {
        websiteFlag = true;
      }
    }

    // Only one address can be primary
    int addressNum = 0;
    for (int i = 1; i < addressCounter; i++) {
      suffix = setSuffix(i);
      if (((CheckBox) customerDetailFields.get("isPrimaryAddress" + suffix)).isSelected()) {
        addressNum++;
      }
    }
    if (addressNum == 0) {
      if (errorString.indexOf("\tPlease select a primary address") == -1) {
        errorString.append("\tPlease select a primary address\n");
      }
    }
    // Only one email can be primary
    int emailNum = 0;
    for (int i = 1; i < emailCounter; i++) {
      suffix = setSuffix(i);
      if (((CheckBox) customerDetailFields.get("isPrimaryEmail" + suffix)).isSelected()) {
        emailNum++;
      }
    }
    if (emailNum == 0) {
      if (errorString.indexOf("\tPlease select a primary email") == -1) {
        errorString.append("\tPlease select a primary email\n");
      }
    }
    // Only one phone can be primary
    int phoneNum = 0;
    for (int i = 1; i < phoneCounter; i++) {
      suffix = setSuffix(i);
      if (((CheckBox) customerDetailFields.get("isPrimaryNumber" + suffix)).isSelected()) {
        phoneNum++;
      }
    }
    if (phoneNum == 0) {
      if (errorString.indexOf("\tPlease select a primary phone") == -1) {
        errorString.append("\tPlease select a primary phone\n");
      }
    }
    // Need at least one mailing address
    int mailingAddressNum = 0;
    for (int i = 1; i < addressCounter; i++) {
      suffix = setSuffix(i);
      if (((CheckBox) customerDetailFields.get("isMailingAddress" + suffix)).isSelected()) {
        mailingAddressNum++;
      }
    }
    if (mailingAddressNum == 0) {
      if (errorString.indexOf("\tPlease select a mailing address") == -1) {
        errorString.append("\tPlease select a mailing address\n");
      }
    }
    // Need at least one texting phone
    int textingPhoneNum = 0;
    for (int i = 1; i < phoneCounter; i++) {
      suffix = setSuffix(i);
      if (((CheckBox) customerDetailFields.get("isTextingNumber" + suffix)).isSelected()) {
        textingPhoneNum++;
      }
    }
    if (textingPhoneNum == 0) {
      if (errorString.indexOf("\tPlease select a texting phone") == -1) {
        errorString.append("\tPlease select a texting phone\n");
      }
    }
    return dobFlag
        && addressFlag
        && emailFlag
        && employerFlag
        && phoneFlag
        && websiteFlag
        && addressNum == 1
        && emailNum == 1
        && phoneNum == 1
        && mailingAddressNum >= 1
        && textingPhoneNum >= 1;
  }

  @Override
  public boolean validate(Control element, Type type) {
    boolean flag = true;
    if (element instanceof TextField) {
      // Check fields have higher priority in error messages
      TextField ui = (TextField) element;
      if (ui.getText().isEmpty()) {
        return flag;
      }

      if (type == Type.EMAIL) {
        // Emails should be in the format of a@b.c
        flag = ui.getText().matches("^.+@.+\\..+$");
        if (!flag) {
          ui.setStyle(redBorder);
          if (errorString.indexOf("\tInvalid email format") == -1) {
            errorString.append("\tInvalid email format\n");
          }
        }
      } else if (type == Type.NUMBER) {
        // Phone should be numbers
        try {
          Long.parseLong(ui.getText());
        } catch (Exception e) {
          flag = false;
          ui.setStyle(redBorder);
          if (errorString.indexOf("\t" + ui.getId() + " should only contain numbers") == -1) {
            errorString.append("\t" + ui.getId() + " should only contain numbers\n");
          }
        }
      } else if (type == Type.WEBSITE) {
        // Website should be "text.text"
        flag = ui.getText().matches("^.+\\..+$");
        if (!flag) {
          ui.setStyle(redBorder);
          if (errorString.indexOf("\tInvalid website format") == -1) {
            errorString.append("\tInvalid website format\n");
          }
        }
      } else {
        flag = false;
      }
    } else if (element instanceof DatePicker) {
      DatePicker ui = (DatePicker) element;
      if (ui.getValue() == null) {
        return flag;
      }
      LocalDate today = LocalDate.now();
      if (today.isBefore((LocalDate) (Object) ui.getValue())) {
        flag = false;
        ui.setStyle(redBorder);
        errorString.append("\tDate cannot be before today\n");
      }
    }

    return flag;
  }

  @Override
  public boolean checkLengths() {
    boolean titleFieldFlag = checkLength(titleField, 10);
    boolean nameFieldFlag = checkLength(nameField, 60);
    boolean occupationFlag = checkLength(jobField, 40);
    boolean citizenshipFieldFlag = checkLength(citizenshipField, 40);
    boolean visaFieldFlag = checkLength(visaField, 40);
    boolean notesAreaFlag = checkLength(notesArea, 1400);

    boolean addressFlag = false;
    for (int i = 1; i < addressCounter; i++) {
      suffix = setSuffix(i);
      boolean addressTypeFlag =
          checkLength((TextField) customerDetailFields.get("addressTypeField" + suffix), 20);
      boolean address1Flag =
          checkLength((TextField) customerDetailFields.get("address1Field" + suffix), 60);
      boolean address2Flag =
          checkLength((TextField) customerDetailFields.get("address2Field" + suffix), 60);
      boolean suburbFlag =
          checkLength((TextField) customerDetailFields.get("suburbField" + suffix), 30);
      boolean cityFlag =
          checkLength((TextField) customerDetailFields.get("cityField" + suffix), 30);
      boolean postCodeFlag =
          checkLength((TextField) customerDetailFields.get("postcodeField" + suffix), 10);
      boolean countryFlag =
          checkLength((TextField) customerDetailFields.get("countryField" + suffix), 30);
      addressFlag =
          addressTypeFlag
              && address1Flag
              && address2Flag
              && suburbFlag
              && cityFlag
              && postCodeFlag
              && countryFlag;
    }

    boolean employerFlag = false;
    for (int i = 1; i < employmentCounter; i++) {
      suffix = setSuffix(i);
      boolean nameFlag =
          checkLength((TextField) customerDetailFields.get("companyNameField" + suffix), 60);
      boolean address1Flag =
          checkLength((TextField) customerDetailFields.get("companyAddress1Field" + suffix), 60);
      boolean address2Flag =
          checkLength((TextField) customerDetailFields.get("companyAddress2Field" + suffix), 60);
      boolean suburbFlag =
          checkLength((TextField) customerDetailFields.get("companySuburbField" + suffix), 30);
      boolean cityFlag =
          checkLength((TextField) customerDetailFields.get("companyCityField" + suffix), 30);
      boolean postCodeFlag =
          checkLength((TextField) customerDetailFields.get("companyPostcodeField" + suffix), 10);
      boolean countryFlag =
          checkLength((TextField) customerDetailFields.get("companyCountryField" + suffix), 30);
      boolean phoneFlag =
          checkLength((TextField) customerDetailFields.get("employerPhoneField" + suffix), 30);
      boolean emailFlag =
          checkLength((TextField) customerDetailFields.get("employerEmailField" + suffix), 60);
      boolean websiteFlag =
          checkLength((TextField) customerDetailFields.get("companyWebsiteField" + suffix), 60);
      employerFlag =
          nameFlag
              && address1Flag
              && address2Flag
              && suburbFlag
              && cityFlag
              && postCodeFlag
              && countryFlag
              && phoneFlag
              && emailFlag
              && websiteFlag;
    }

    return titleFieldFlag
        && nameFieldFlag
        && occupationFlag
        && citizenshipFieldFlag
        && visaFieldFlag
        && notesAreaFlag
        && addressFlag
        && employerFlag;
  }

  @Override
  public boolean checkLength(Control ui, int length) {
    int len;
    if (ui instanceof TextField) {
      len = ((TextField) ui).getText().length();
    } else if (ui instanceof TextArea) {
      len = ((TextArea) ui).getText().length();
    } else {
      return false;
    }
    if (len > length) {
      ui.setStyle(redBorder);
      if (errorString.indexOf(ui.getId() + " is too long") == -1) {
        errorString.append("\t" + ui.getId() + " is too long, the max length is: " + length + "\n");
      }
      return false;
    }
    return true;
  }

  private String setSuffix(int counter) {
    return counter == 1 ? "" : ("_" + counter);
  }

}
