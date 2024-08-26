package uoa.lavs.controllers;

import atlantafx.base.util.Animations;
import java.io.IOException;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.concurrent.ScheduledService;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import uoa.lavs.Main;
import uoa.lavs.SceneManager.AppScene;

@Controller
public class StartController {

  @Autowired private CustomerController customerController;
  @Autowired private SearchController searchController;
  @Autowired private PendingUpdatesController pendingUpdatesController;

  @FXML private StackPane stackPane;
  @FXML private Label timeLabel;
  @FXML private HBox statusBar;

  private Pane popupPane;
  private final StringProperty timeStringProperty = new SimpleStringProperty();
  private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("hh:mm a");

  public void initialize() {
    timeLabel.textProperty().bind(timeStringProperty);
    pulseStackPane();
    startClockService();
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

  private void startClockService() {
    ScheduledService<String> clockService =
        new ScheduledService<>() {
          @Override
          protected Task<String> createTask() {
            return new Task<>() {
              @Override
              protected String call() {
                return LocalTime.now().format(formatter).toUpperCase();
              }
            };
          }
        };

    clockService.setOnSucceeded(event -> timeStringProperty.set(clockService.getValue()));
    clockService.setPeriod(Duration.seconds(1));
    clockService.start();
  }

  @FXML
  private void onClickAbout(ActionEvent event) throws IOException {
    Button sourceButton = (Button) event.getSource();
    Pane currentRoot = (Pane) sourceButton.getScene().getRoot();
    FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/aboutPopup.fxml"));
    Parent popupContent = loader.load();
    currentRoot.getChildren().add(popupContent);
  }

  @FXML
  private void onClickAddCustomer(ActionEvent event) throws IOException {
    customerController.setUpAddCustomer();
    Main.setScene(AppScene.CUSTOMER);
  }

  @FXML
  private void onClickSearch(ActionEvent event) throws IOException {
    searchController.setCoBorrowerSearch(false);
    searchController.clearSearch();
    Main.setScene(AppScene.SEARCH);
  }

  @FXML
  private void onClickPending(ActionEvent event) throws IOException {
    pendingUpdatesController.populatePendingTable();
    pendingUpdatesController.setTitle();
    Main.setScene(AppScene.PENDING_UPDATES);
  }
}
