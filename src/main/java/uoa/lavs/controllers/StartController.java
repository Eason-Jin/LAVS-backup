package uoa.lavs.controllers;

import java.io.IOException;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

import atlantafx.base.util.Animations;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;

import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import uoa.lavs.Main;
import uoa.lavs.SceneManager.AppScene;

@Controller
public class StartController {

    @Autowired
    private SearchController searchController;

    @FXML private Button addCustomerButton;
    @FXML private Button searchButton;
    @FXML private StackPane stackPane;
    @FXML private Label timeLabel;
    @FXML private HBox statusBar;
    @FXML private AnchorPane rootPane;

    private Pane popupPane;

    public void initialize() {
        pulseStackPane();
        startClock();
        slideInStatusBar();
    }

    private void pulseStackPane() {
        var pulse = Animations.pulse(stackPane);
        pulse.playFromStart();
    }

    private void slideInStatusBar() {
        var slideIn = Animations.slideInDown(statusBar, Duration.millis(1000));
        slideIn.playFromStart();
    }

    private void startClock() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("hh:mm a");

        Timeline clock = new Timeline(new KeyFrame(Duration.ZERO, e -> {
            LocalTime currentTime = LocalTime.now();
            timeLabel.setText(currentTime.format(formatter).toUpperCase());
        }),
                new KeyFrame(Duration.seconds(1)));

        clock.setCycleCount(Timeline.INDEFINITE);
        clock.play();
    }

    @FXML
    private void createPopup() {
        StackPane dimOverlay = new StackPane();
        dimOverlay.setStyle("-fx-background-color: rgba(0, 0, 0, 0.2);");
        dimOverlay.setPrefSize(rootPane.getWidth(), rootPane.getHeight());

        popupPane = new StackPane();
        popupPane.setPrefSize(500, 450);
        Rectangle background = new Rectangle(500, 450);
        background.setFill(Color.WHITE);
        background.setStroke(Color.web("#ddddde"));
        background.setStrokeWidth(1);
        background.setArcHeight(40);
        background.setArcWidth(40);

        Label popupLabel = new Label("About");
        popupLabel.setStyle("-fx-font-weight: bold;");

        Label paragraph = new Label("This is a paragraph that provides additional information about the topic. "
                + "You can add more text here to explain details or provide context.");
        paragraph.setWrapText(true); // Enable text wrapping
        paragraph.setMaxWidth(450); // Set the maximum width for the paragraph

        Button closeButton = new Button("X");
        closeButton.setFocusTraversable(false);
        closeButton.setOnAction(e -> {
            popupPane.setVisible(false);
            rootPane.getChildren().remove(popupPane);
            rootPane.getChildren().remove(dimOverlay);
        });

        StackPane.setAlignment(closeButton, javafx.geometry.Pos.TOP_LEFT);
        StackPane.setMargin(closeButton, new javafx.geometry.Insets(20, 20, 20, 20));

        VBox content = new VBox(10, popupLabel, paragraph);
        content.setAlignment(javafx.geometry.Pos.CENTER);

        popupPane.getChildren().addAll(background, content, closeButton);

        rootPane.getChildren().addAll(dimOverlay, popupPane);

        double centerX = (rootPane.getWidth() - popupPane.getPrefWidth()) / 2;
        double centerY = (rootPane.getHeight() - popupPane.getPrefHeight()) / 2;

        popupPane.setLayoutX(centerX);
        popupPane.setLayoutY(centerY);

        popupPane.setVisible(true);
    }

    @FXML
    private void onClickAddCustomer(ActionEvent event) throws IOException {
        Main.setScene(AppScene.ADD_CUSTOMER);
    }

    @FXML
    private void onClickSearch(ActionEvent event) throws IOException {
        searchController.clearSearch();
        Main.setScene(AppScene.SEARCH);
    }
}
