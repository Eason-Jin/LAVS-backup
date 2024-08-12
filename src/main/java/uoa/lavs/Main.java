package uoa.lavs;

import atlantafx.base.theme.PrimerLight;
import java.io.IOException;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import uoa.lavs.SceneManager.SceneUi;
import uoa.lavs.mainframe.Connection;
import uoa.lavs.mainframe.Instance;
import uoa.lavs.mainframe.Status;
import uoa.lavs.mainframe.messages.customer.LoadCustomer;
import uoa.lavs.mainframe.simulator.RecorderConnection;
import uoa.lavs.mainframe.simulator.SimpleReplayConnection;

public class Main extends Application {

  private static Scene scene;
  private static Stage primaryStage;

  public static void main(String[] args) {

    // The following shows two ways of using the mainframe interface
    // Approach #1: Use the singleton instance - this way is recommended as it provides a single
    // configuration
    // location (and is easy for the testers to change when needed).
    Connection connection = Instance.getConnection();
    executeTestMessage(connection);

    // You can use another approach if desired, but make sure you document how the markers can
    // change the
    // connection implementation.
    launch(args);
  }

  @Override
  public void start(Stage stage) throws IOException {
    Application.setUserAgentStylesheet(new PrimerLight().getUserAgentStylesheet());
    addScenes();
    Parent root = setRoot(SceneUi.START);
    scene = new Scene(root, 1200, 680);
    stage.setTitle("JavaFX Application");
    stage.setScene(scene);
    stage.setResizable(false);
    // stage.setMaximized(true);
    stage.show();

    stage.setOnCloseRequest(
        event -> {
          event.consume();
          logout();
        });

    root.requestFocus();
    primaryStage = stage;
  }

  private static void executeTestMessage(Connection connection) {
    LoadCustomer testMessage = new LoadCustomer();
    testMessage.setCustomerId("123456-789");
    Status status = testMessage.send(connection);
    try {
      connection.close();
    } catch (IOException e) {
      System.out.println(
          "Something went wrong - could not close connection! The message is " + e.getMessage());
      return;
    }

    if (status.getWasSuccessful()) {
      System.out.println(
          "The send was successful: the customer name is " + testMessage.getNameFromServer());
    } else {
      System.out.println(
          "Something went wrong - the send failed! The code is " + status.getErrorCode());
    }
  }

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

  public static void logout() {
    primaryStage.close();
    Platform.exit();
    System.exit(0);
  }

  private static Parent loadFxml(final String fxml) throws IOException {
    return new FXMLLoader(Main.class.getResource("/fxml/" + fxml + ".fxml")).load();
  }

  private void addScenes() throws IOException {
    // store string of location of each view
    SceneManager.addStringSceneUi(SceneManager.SceneUi.START, "start");
  }
}
