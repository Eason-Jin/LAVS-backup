package uoa.lavs.controllers;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Pane;
import uoa.lavs.Main;
import uoa.lavs.SceneManager.AppScene;

@Controller
public class CustomerDetailsController {
    @FXML private Button backButton;
    @FXML private Button homeButton;
    @FXML private Button infoButton;
    @FXML private Button editButton;

    @FXML private TextField titleField;
    @FXML private TextField nameField;
    @FXML private TextField dateOfBirthField;

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

    private Map<String, Node> customerDetailFields = new HashMap<>();

    @FXML
    private void initialize() {
        addAllElementsToMap();
    }

    public void setCustomerDetails(String customerId) {

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
}
