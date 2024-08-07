package uoa.lavs;

import java.io.IOException;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;
import uoa.lavs.SceneManager.SceneUi;

/**
 * This is the entry point of the JavaFX application, while you can change this class, it should
 * remain as the class that runs the JavaFX application.
 */
public class App extends Application {

  private static Scene scene;
  private static Stage primaryStage;

  public static void startApp() {
    launch();
  }

  /**
   * Sets the root of the scene to the specified room unless there is no current scene, in which
   * case the scene will be created in the {@code start} method.
   *
   * @param sceneUi the room the root will be switched to
   * @return the root of the scene to be updated
   * @throws IOException if the string scene UI is not found
   */
  public static Parent setRoot(SceneUi sceneUi) throws IOException {

    Parent root = SceneManager.getParentSceneUi(sceneUi);

    if (root == null) {
      // scene has not been loaded previously
      root = loadFxml(SceneManager.getStringSceneUi(sceneUi));
      SceneManager.addParentSceneUi(sceneUi, root);
    }

    if (scene != null) {
      scene.setRoot(root);
    }

    return root;
  }

  /** This method handles the closing of the application. */
  public static void logout() {

    Alert alert = new Alert(AlertType.CONFIRMATION);
    alert.setTitle("Quit");
    alert.setHeaderText("If you quit, all progress will be lost.");
    alert.setContentText("Are you sure you want to quit?");

    // only close window if 'OK' was pressed
    if (alert.showAndWait().get() == ButtonType.OK) {
      primaryStage.close();
      Platform.exit();
      System.exit(0);
    }
  }

  /**
   * Returns the node associated to the input file. The method expects that the file is located in
   * "src/main/resources/fxml".
   *
   * @param fxml The name of the FXML file (without extension).
   * @return The node of the input file.
   * @throws IOException If the file is not found.
   */
  private static Parent loadFxml(final String fxml) throws IOException {
    return new FXMLLoader(App.class.getResource("/fxml/" + fxml + ".fxml")).load();
  }

  /**
   * This method is invoked when the application starts. It loads and shows the "Start" scene.
   *
   * @param stage the primary stage of the application
   * @throws IOException if "src/main/resources/fxml/start.fxml" is not found
   */
  @Override
  public void start(final Stage stage) throws IOException {
    addScenes();

    Parent root = setRoot(SceneUi.START);
    scene = new Scene(root, 960, 540);
    scene.getStylesheets().add(getClass().getResource("/css/application.css").toExternalForm());

    stage.setTitle("SpAIce EscAIpe");
    stage.setScene(scene);
    stage.setResizable(false);
    stage.show();

    stage.setOnCloseRequest(
        event -> {
          event.consume(); // prevent window from closing when 'Cancel' is pressed
          logout();
        });

    root.requestFocus();
    primaryStage = stage;
  }

  /**
   * This method adds each room to the SceneManager so they can be accessed throughout the game.
   *
   * @throws IOException if "src/main/resources/fxml/_.fxml" is not found
   */
  private void addScenes() throws IOException {
    // store string of location of each view
    SceneManager.addStringSceneUi(SceneUi.START, "start");
  }
}
