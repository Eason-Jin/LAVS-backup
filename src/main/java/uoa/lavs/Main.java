package uoa.lavs;

import atlantafx.base.theme.PrimerLight;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import uoa.lavs.SceneManager.SceneUi;
import uoa.lavs.dataoperations.CustomerFinder;
import uoa.lavs.dataoperations.CustomerLoader;
import uoa.lavs.dataoperations.CustomerUpdater;
import uoa.lavs.mainframe.Connection;
import uoa.lavs.mainframe.Status;
import uoa.lavs.mainframe.messages.customer.LoadCustomer;
import uoa.lavs.models.Customer;

public class Main extends Application {

  private static Scene scene;
  private static Stage primaryStage;

  public static void main(String[] args) {

    Customer customer = new Customer();
    customer.setName("Jessica");
    customer.setTitle("Ms");
    customer.setDob(LocalDate.of(2004, 4, 16));
    customer.setOccupation("Student");
    customer.setCitizenship("NZ");

    // Creates customer in database and mainframe
    CustomerUpdater customerUpdater = new CustomerUpdater();
    customerUpdater.updateData(null, customer);
    System.out.println("Customer created");
    System.out.println("Id: " + customer.getId());
    System.out.println("Name: " + customer.getName());
    System.out.println("Title: " + customer.getTitle());
    System.out.println("Date of Birth: " + customer.getDob());
    System.out.println("Occupation: " + customer.getOccupation());
    System.out.println("Citizenship: " + customer.getCitizenship());
    System.out.println("Status: " + customer.getStatus());
    System.out.println();

    Customer customer2 = new Customer();
    customer2.setName("Bob");
    customer2.setTitle("Mr");
    customer2.setDob(LocalDate.of(1990, 4, 16));
    customer2.setOccupation("IT");
    customer2.setCitizenship("NZ");

    // Creates customer in database and mainframe
    customerUpdater.updateData(null, customer2);
    System.out.println("Customer created");
    System.out.println("Id: " + customer2.getId());
    System.out.println("Name: " + customer2.getName());
    System.out.println("Title: " + customer2.getTitle());
    System.out.println("Date of Birth: " + customer2.getDob());
    System.out.println("Occupation: " + customer2.getOccupation());
    System.out.println("Citizenship: " + customer2.getCitizenship());
    System.out.println("Status: " + customer2.getStatus());
    System.out.println();

    // Updates customer in database and mainframe
    Customer updateCustomerRequest = new Customer();
    updateCustomerRequest.setName("Jessica");
    customerUpdater.updateData("2", updateCustomerRequest);

    CustomerFinder customerFinder = new CustomerFinder();
    List<Customer> customers = customerFinder.findData("2");
    System.out.println("Customer with Id: " + customers.get(0).getId() + " updated");
    System.out.println("Name: " + customers.get(0).getName());
    System.out.println("Date of Birth: " + customers.get(0).getDob());
    System.out.println();

    CustomerLoader customerLoader = new CustomerLoader();
    Customer customer3 = customerLoader.loadData("1");
    System.out.println("Load Customer with Id: " + customer3.getId());
    System.out.println("Name: " + customer3.getName());
    System.out.println("Title: " + customer3.getTitle());
    System.out.println("Date of Birth: " + customer3.getDob());
    System.out.println("Occupation: " + customer3.getOccupation());
    System.out.println("Citizenship: " + customer3.getCitizenship());
    System.out.println("Status: " + customer3.getStatus());
    System.out.println();

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
    testMessage.setCustomerId("1");
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
