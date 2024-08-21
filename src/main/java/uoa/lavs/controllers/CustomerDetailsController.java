package uoa.lavs.controllers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Pane;
import uoa.lavs.Main;
import uoa.lavs.SceneManager.AppScene;
import uoa.lavs.dataoperations.customer.AddressFinder;
import uoa.lavs.dataoperations.customer.CustomerLoader;
import uoa.lavs.dataoperations.customer.EmailFinder;
import uoa.lavs.dataoperations.customer.EmployerFinder;
import uoa.lavs.dataoperations.customer.PhoneFinder;
import uoa.lavs.mainframe.Frequency;
import uoa.lavs.mainframe.LoanStatus;
import uoa.lavs.models.Address;
import uoa.lavs.models.Customer;
import uoa.lavs.models.Email;
import uoa.lavs.models.Employer;
import uoa.lavs.models.Loan;
import uoa.lavs.models.Phone;

@Controller
public class CustomerDetailsController {
    @FXML private Button backButton;
    @FXML private Button homeButton;
    @FXML private Button infoButton;
    @FXML private Button editButton;

    @FXML private TextField titleField;
    @FXML private TextField nameField;
    @FXML private TextField dateOfBirthField;
    @FXML private TextField occupationField;

    @FXML private TextField citizenshipField;
    @FXML private TextField visaField;

    @FXML private TextArea notesArea;

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

    @FXML private TableView<Loan> loanTable;
    @FXML private TableColumn<Loan, String> loanIdColumn;
    @FXML private TableColumn<Loan, LoanStatus> statusColumn;
    @FXML private TableColumn<Loan, String> principalColumn;
    @FXML private TableColumn<Loan, String> startDateColumn;
    @FXML private TableColumn<Loan, Frequency> paymentFrequencyColumn;
    @FXML private Button addLoanButton;

    private Map<String, Node> customerDetailFields = new HashMap<>();

    @FXML
    private void initialize() {
        addAllElementsToMap();
    }

    public void setCustomerDetails(String customerId) {
        Customer customer = CustomerLoader.loadData(customerId);
        List<Address> addresses = AddressFinder.findData(customerId);
        List<Email> emails = EmailFinder.findData(customerId);
        List<Phone> phones = PhoneFinder.findData(customerId);
        List<Employer> employers = EmployerFinder.findData(customerId);

        titleField.setText(customer.getTitle());
        nameField.setText(customer.getName());
        dateOfBirthField.setText(customer.getDob().toString());
        occupationField.setText(customer.getOccupation());

        citizenshipField.setText(customer.getCitizenship());
        visaField.setText(customer.getVisaType());

        for (int index = 0; index < addresses.size(); index++) {
            generateFields(addressPane, addressScrollAnchorPane, addressFlowPane, addresses.get(index).getListRepresentation(), index);
        }

        for (int index = 0; index < emails.size(); index++) {
            generateFields(emailPane, contactScrollAnchorPane, emailFlowPane, emails.get(index).getListRepresentation(), index);
        }

        for (int index = 0; index < phones.size(); index++) {
            generateFields(phonePane, contactScrollAnchorPane, phoneFlowPane, phones.get(index).getListRepresentation(), index);
        }

        for (int index = 0; index < employers.size(); index++) {
            generateFields(employmentPane, employmentScrollAnchorPane, employmentFlowPane, employers.get(index).getListRepresentation(), index);
        }

        notesArea.setText(customer.getNotes());
    }

    // DUPLICATE CODE
    private void addAllElementsToMap() {
        customerDetailFields.clear();
        addElementsToMap(addressPane);
        addElementsToMap(emailPane);
        addElementsToMap(phonePane);
        addElementsToMap(employmentPane);
    }

    // DUPLICATE CODE
    public void addElementsToMap(Pane pane) {
        for (var node : pane.getChildren()) {
            if (node.getId() != null) {
            customerDetailFields.put(node.getId(), node);
            }
        }
        customerDetailFields.put(pane.getId(), pane);
    }

    // DUPLICATE CODE
    private Pane addNewField(Pane pane, int counter) {
        List<Node> nodesCopy = new ArrayList<>(pane.getChildrenUnmodifiable());
        String counterString;
        if (counter != 0) {
            counterString = "_" + counter;
        }
        else {
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
                newTextField.setMinWidth(((TextField) node).getMinWidth());
                newTextField.setMinHeight(((TextField) node).getMinHeight());
                newTextField.setMaxWidth(((TextField) node).getMaxWidth());
                newTextField.setMaxHeight(((TextField) node).getMaxHeight());
                newTextField.setEditable(((TextField) node).isEditable());
                newTextField.setId(newFxId);
                newPane.getChildren().add(newTextField);
                customerDetailFields.put(newFxId, newTextField);
            } else if (node instanceof CheckBox) {
                CheckBox newCheckBox = new CheckBox(((CheckBox) node).getText());
                newCheckBox.setLayoutX(node.getLayoutX());
                newCheckBox.setLayoutY(node.getLayoutY());
                newCheckBox.setDisable(node.isDisabled());
                newCheckBox.setId(newFxId);
                newPane.getChildren().add(newCheckBox);
                customerDetailFields.put(newFxId, newCheckBox);
            } else {
                continue;
            }
        }
        customerDetailFields.put(newPane.getId(), newPane);
        return newPane;
    }

    private void generateFields(Pane pane, AnchorPane anchorPane, FlowPane flowPane, List<String> fields, int counter) {
        Pane newPane = pane;
        if (counter != 0) {
            newPane = addNewField(pane, counter);
            anchorPane.setPrefHeight(anchorPane.getPrefHeight()+newPane.getPrefHeight()+flowPane.getVgap());
            flowPane.getChildren().add(newPane);
        }
        setFields(newPane.getChildren(), fields);
    }

    private void setFields(List<Node> nodes, List<String> fields) {
        for (int index = 0; index < nodes.size(); index++) {
            Node node = nodes.get(index);
            if (node instanceof TextField) {
                ((TextField) node).setText(fields.get(index));
            } else if (node instanceof CheckBox) {
                ((CheckBox) node).setSelected(Boolean.parseBoolean(fields.get(index)));
            }
        }
    }

    @FXML
    private void onClickBack(ActionEvent event) throws IOException {
        Main.setScene(AppScene.SEARCH);
    }

    @FXML
    private void onClickHome(ActionEvent event) throws IOException {
        Main.setScene(AppScene.START);
    }

    @FXML
    private void onClickInfo(ActionEvent event) {
        System.out.println("Info button clicked");
    }

    @FXML
    private void onClickEdit(ActionEvent event) {
        System.out.println("Edit button clicked");
    }

    @FXML
    private void onClickAddLoan(ActionEvent event) {
        // Main.setScene(AppScene.ADD_LOAN);
    }
}
