package uoa.lavs;

import atlantafx.base.theme.PrimerLight;
import java.io.IOException;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import uoa.lavs.SceneManager.AppScene;
import uoa.lavs.mainframe.Connection;
import uoa.lavs.mainframe.Instance;
import uoa.lavs.mainframe.Status;
import uoa.lavs.mainframe.messages.customer.LoadCustomer;
import uoa.lavs.mainframe.simulator.RecorderConnection;
import uoa.lavs.mainframe.simulator.SimpleReplayConnection;

public class Main extends Application {

  private static Scene currentScene;
  private static Stage currentStage;

  public static void main(String[] args) {

    // The following shows two ways of using the mainframe interface
    // Approach #1: Use the singleton instance - this way is recommended as it provides a single
    // configuration
    // location (and is easy for the testers to change when needed).
    Connection connection = Instance.getConnection();
    executeTestMessage(connection);
    launch(args);
  }

  public static Stage getStage() {
    return currentStage;
  }

  private static FXMLLoader loadLoader(String fxml) throws IOException {
    FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("/fxml/" + fxml + ".fxml"));
    return fxmlLoader;
  }

  public static void setScene(AppScene scene) {
    currentScene.setRoot(SceneManager.getScene(scene));
  }

  @Override
  public void start(Stage stage) throws IOException {
    Application.setUserAgentStylesheet(new PrimerLight().getUserAgentStylesheet());
    SceneManager.addScene(AppScene.START, loadLoader("start").load());
    SceneManager.addScene(AppScene.ADD_CUSTOMER, loadLoader("addCustomer").load());
    SceneManager.addScene(AppScene.SEARCH, loadLoader("search").load());

    currentStage = stage;
    currentScene = new Scene(loadLoader("addCustomer").load(), 1152, 648);
    stage.setScene(currentScene);
    stage.show();
    stage.setOnCloseRequest(e -> System.exit(0));
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
}
