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
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
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
  private void createPopup(ActionEvent event) {
    Button sourceButton = (Button) event.getSource();
    Pane currentRoot = (Pane) sourceButton.getScene().getRoot();

    StackPane dimOverlay = new StackPane();
    dimOverlay.setStyle("-fx-background-color: rgba(0, 0, 0, 0.2);");
    dimOverlay.setPrefSize(currentRoot.getWidth(), currentRoot.getHeight());

    popupPane = new StackPane();
    popupPane.setPrefSize(500, 400);
    Rectangle background = new Rectangle(500, 400);
    background.setFill(Color.WHITE);
    background.setStroke(Color.web("#ddddde"));
    background.setStrokeWidth(1);
    background.setArcHeight(40);
    background.setArcWidth(40);

    Label popupLabel = new Label("About");
    popupLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: #6d7175; -fx-font-size: 25px");

    Label paragraph =
        new Label(
            "The following resources were used in the creation of this application:\n\n"
                + "\u2022 JavaFX \n"
                + "\u2022 JavaFX Maven Plugin \n"
                + "\u2022 AtlantaFX \n"
                + "\u2022 SQLite JDBC \n"
                + "\u2022 Spring Framework \n"
                + "\u2022 FontAwesome Icons");

    paragraph.setStyle("-fx-text-fill: #6d7175; -fx-font-size: 20px;");
    paragraph.setWrapText(true);
    paragraph.setMaxWidth(450);

    paragraph.setStyle("-fx-text-fill: #6d7175; -fx-font-size: 20px;");
    paragraph.setWrapText(true);
    paragraph.setMaxWidth(450);

    Button closeButton = new Button();
    closeButton.setFocusTraversable(false);

    Image closeImage = new Image(getClass().getResourceAsStream("/images/start/cross.png"));
    ImageView closeIcon = new ImageView(closeImage);
    closeIcon.setFitWidth(22);
    closeIcon.setFitHeight(22);

    closeButton.setGraphic(closeIcon);

    closeButton.setStyle("-fx-background-radius: 100; -fx-border-radius: 20; -fx-padding: 15;");
    closeButton.setOnAction(
        e -> {
          popupPane.setVisible(false);
          currentRoot.getChildren().remove(popupPane);
          currentRoot.getChildren().remove(dimOverlay);
        });

    StackPane.setAlignment(closeButton, javafx.geometry.Pos.TOP_LEFT);
    StackPane.setMargin(closeButton, new javafx.geometry.Insets(20, 20, 20, 20));

    VBox content = new VBox(10, popupLabel, paragraph);
    content.setAlignment(javafx.geometry.Pos.CENTER);

    popupPane.getChildren().addAll(background, content, closeButton);

    currentRoot.getChildren().addAll(dimOverlay, popupPane);

    double centerX = (currentRoot.getWidth() - popupPane.getPrefWidth()) / 2;
    double centerY = (currentRoot.getHeight() - popupPane.getPrefHeight()) / 2;

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

  @FXML
  private void onClickPending(ActionEvent event) throws IOException {
    pendingUpdatesController.populatePendingTable();
    pendingUpdatesController.setTitle();
    Main.setScene(AppScene.PENDING_UPDATES);
  }
}
