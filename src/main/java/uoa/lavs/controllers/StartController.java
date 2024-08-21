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
    private void onClickAddCustomer(ActionEvent event) throws IOException {
        Main.setScene(AppScene.ADD_CUSTOMER);
    }

    @FXML
    private void onClickSearch(ActionEvent event) throws IOException {
        searchController.clearSearch();
        Main.setScene(AppScene.SEARCH);
    }
}
