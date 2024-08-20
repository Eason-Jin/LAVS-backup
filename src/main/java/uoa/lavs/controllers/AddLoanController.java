package uoa.lavs.controllers;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import org.springframework.stereotype.Controller;
import uoa.lavs.Main;
import uoa.lavs.SceneManager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Controller
public class AddLoanController {

    @FXML private AnchorPane coBorrowerScrollAnchorPane;
    @FXML private FlowPane coBorrowerFlowPane;
    @FXML private Pane coBorrowerPane;
    @FXML private Pane loanDetailsPane;
    @FXML private TabPane detailsTabPane;


    private int initialCoBorrowerCounter = 1;
    private int coBorrowerCounter = initialCoBorrowerCounter;
    private HashMap<String, Node> coBorrowerFields = new HashMap<>();
    private HashMap<String, Node> loanDetailsFields = new HashMap<>();

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
        }
        else {
            paneToDeleteFxId = coBorrowerPane.getId();
        }
        Pane paneToDelete = (Pane) coBorrowerFields.get(paneToDeleteFxId);
        List<Node> nodesCopy = paneToDelete.getChildrenUnmodifiable();
        for (var node : nodesCopy) {
            coBorrowerFields.remove(node.getId());
        }
        coBorrowerFields.remove(paneToDeleteFxId);

        coBorrowerScrollAnchorPane.setPrefHeight(coBorrowerScrollAnchorPane.getPrefHeight()-(coBorrowerPane.getPrefHeight()+coBorrowerFlowPane.getVgap()));
        coBorrowerFlowPane.getChildren().remove(paneToDelete);
    }

    @FXML
    private void onClickAddCoBorrower(ActionEvent event) throws IOException {
//        Main.setScene(SceneManager.AppScene.SEARCH);
        addCoBorrower();
    }

    private void addCoBorrower() {
        List<Node> nodesCopy = new ArrayList<>(coBorrowerPane.getChildrenUnmodifiable());
        String counterString;
        if (coBorrowerCounter != 0) {
            counterString = "_" + coBorrowerCounter;
        }
        else {
            counterString = "";
        }
        Pane newPane = new Pane();
        newPane.setId(coBorrowerPane.getId() + counterString);
        newPane.setPrefWidth(coBorrowerPane.getPrefWidth());
        newPane.setPrefHeight(coBorrowerPane.getPrefHeight());

        for (var node : nodesCopy) {
            String newFxId = node.getId() + counterString;
            if (node instanceof TextField) {
                TextField newTextField = new TextField();
                newTextField.setText(newFxId);
                newTextField.setLayoutX(node.getLayoutX());
                newTextField.setLayoutY(node.getLayoutY());
                newTextField.setPrefWidth(((TextField) node).getPrefWidth());
                newTextField.setPrefHeight(((TextField) node).getPrefHeight());
                newTextField.setEditable(((TextField) node).isEditable());
                newTextField.setId(newFxId);
                newPane.getChildren().add(newTextField);
                coBorrowerFields.put(newFxId, newTextField);
            }
            else if (node instanceof Separator) {
                Separator newSeparator = new Separator();
                newSeparator.setPrefWidth(((Separator) node).getPrefWidth());
                newSeparator.setPrefHeight(((Separator) node).getPrefHeight());
                newSeparator.setLayoutX(node.getLayoutX());
                newSeparator.setLayoutY(node.getLayoutY());
                newPane.getChildren().add(newSeparator);
            }
            else if (node instanceof Button) {
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
            }
            else {
                continue;
            }
        }
        coBorrowerFlowPane.getChildren().add(newPane);
        coBorrowerFields.put(newPane.getId(), newPane);
        System.out.println(counterString);
        coBorrowerCounter++;
        coBorrowerScrollAnchorPane.setPrefHeight(coBorrowerScrollAnchorPane.getPrefHeight()+coBorrowerPane.getPrefHeight()+coBorrowerFlowPane.getVgap());
    }

    private void resetScene() {
        resetCoBorrowerFields();
        clearAllFields();
        detailsTabPane.getSelectionModel().select(0);
    }

    private void clearAllFields() {
        for (String nodeId : loanDetailsFields.keySet()) {
            Node node = loanDetailsFields.get(nodeId);
            if (node instanceof TextField) {
                ((TextField) node).clear();
            }
            else if (node instanceof DatePicker) {
                ((DatePicker) node).setValue(null);
            }
            else if (node instanceof ComboBox) {
                System.out.println(nodeId);
                remakeComboBox(node);
            }
            else if (node instanceof CheckBox) {
                ((CheckBox) node).setSelected(false);
            }
        }
    }

    private void remakeComboBox(Node node) {
        loanDetailsPane.getChildren().remove(node);
        ComboBox<FXCollections> newComboBox = new ComboBox<>();
        newComboBox.setPromptText(((ComboBox<FXCollections>) node).getPromptText());
        newComboBox.setItems(FXCollections.observableArrayList(((ComboBox<FXCollections>) node).getItems()));
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
    }

    @FXML
    private void onClickSave(ActionEvent event) throws IOException {
    }

    @FXML
    private void onClickHome(ActionEvent event) throws IOException {
        resetScene();
        Main.setScene(SceneManager.AppScene.START);
    }

    @FXML
    private void onClickInfo(ActionEvent event) throws IOException {
    }
}
