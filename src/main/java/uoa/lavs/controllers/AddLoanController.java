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

    @FXML private ScrollPane coBorrowerScrollpane;
    @FXML private AnchorPane coBorrowerScrollAnchorPane;
    @FXML private FlowPane coBorrowerFlowPane;
    @FXML private Pane coBorrowerPane;

    private int initialCoBorrowerCounter = 2;
    private int coBorrowerCounter = initialCoBorrowerCounter;
    private int numCoBorrowers = 0;
    private HashMap<String, Node> coBorrowerFields = new HashMap<>();

    @FXML
    private void initialize() {
        coBorrowerPane.setVisible(false);
        addInitialCoBorrowerToMap();
    }

    private void addInitialCoBorrowerToMap() {
        for (var node : coBorrowerPane.getChildren()) {
            if (node.getId() != null) {
                coBorrowerFields.put(node.getId(), node);
            }
        }
        coBorrowerFields.put(coBorrowerPane.getId(), coBorrowerPane);
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
        numCoBorrowers--;
        if (numCoBorrowers == 1) {
            disableDeleteButton((buttonClickedFxId.split("_"))[0]);
        }

    }

    private void disableDeleteButton(String s) {
    }

    private void enableDeleteButton() {
    }

    @FXML
    private void onClickAddCoBorrower(ActionEvent event) throws IOException {
//        Main.setScene(SceneManager.AppScene.SEARCH);
        addCoBorrower();
    }

    private void addCoBorrower() {
        numCoBorrowers++;
        if (numCoBorrowers == 1) {
            coBorrowerPane.setVisible(true);
            return;
        }
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
                newTextField.setPromptText(((TextField) node).getPromptText());
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
        coBorrowerFields.put(newPane.getId(), newPane);
        coBorrowerCounter++;
        if (numCoBorrowers >= 2) {
            enableDeleteButton();
        }
        coBorrowerScrollAnchorPane.setPrefHeight(coBorrowerScrollAnchorPane.getPrefHeight()+newPane.getPrefHeight()+coBorrowerFlowPane.getVgap());
        coBorrowerFlowPane.getChildren().add(newPane);
    }

    @FXML
    private void onClickCancel(ActionEvent event) throws IOException {
    }

    @FXML
    private void onClickSave(ActionEvent event) throws IOException {
    }

    @FXML
    private void onClickHome(ActionEvent event) throws IOException {
        Main.setScene(SceneManager.AppScene.START);
    }

    @FXML
    private void onClickInfo(ActionEvent event) throws IOException {
    }
}
