package uoa.lavs;

import atlantafx.base.theme.PrimerLight;
import java.io.IOException;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import uoa.lavs.SceneManager.AppScene;
import uoa.lavs.configs.SpringConfig;
import uoa.lavs.mainframe.Connection;
import uoa.lavs.mainframe.Status;
import uoa.lavs.mainframe.messages.customer.LoadCustomer;

public class Main extends Application {

  private static Scene currentScene;
  private static Stage currentStage;
  private static ApplicationContext springContext;

  public static void main(String[] args) {
    launch(args);
  }

  public static Stage getStage() {
    return currentStage;
  }

  private static FXMLLoader loadLoader(String fxml) throws IOException {
    FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("/fxml/" + fxml + ".fxml"));
    fxmlLoader.setControllerFactory(springContext::getBean);
    return fxmlLoader;
  }

  public static void setScene(AppScene scene) {
    currentScene.setRoot(SceneManager.getScene(scene));
  }

  @Override
  public void start(Stage stage) throws IOException {
    springContext = new AnnotationConfigApplicationContext(SpringConfig.class);
    Application.setUserAgentStylesheet(new PrimerLight().getUserAgentStylesheet());
    SceneManager.addScene(AppScene.START, loadLoader("start").load());
    SceneManager.addScene(AppScene.CUSTOMER, loadLoader("customer").load());
    SceneManager.addScene(AppScene.SEARCH, loadLoader("search").load());
    SceneManager.addScene(AppScene.ADD_LOAN, loadLoader("addLoan").load());
    SceneManager.addScene(AppScene.LOAN_DETAILS, loadLoader("loanDetails").load());
    SceneManager.addScene(AppScene.PENDING_UPDATES, loadLoader("pendingUpdates").load());

    currentStage = stage;
    currentScene = new Scene(loadLoader("start").load(), 1152, 648);
    stage.setScene(currentScene);
    stage.show();
    stage.setResizable(false);
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
