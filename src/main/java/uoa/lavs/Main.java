package uoa.lavs;

import atlantafx.base.theme.PrimerLight;
import java.io.IOException;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import uoa.lavs.SceneManager.AppScene;
import uoa.lavs.dataoperations.customer.CustomerUpdater;
import uoa.lavs.dataoperations.loan.LoanLoader;
import uoa.lavs.dataoperations.loan.LoanUpdater;
import uoa.lavs.mainframe.Connection;
import uoa.lavs.mainframe.Frequency;
import uoa.lavs.mainframe.Instance;
import uoa.lavs.mainframe.RateType;
import uoa.lavs.mainframe.Status;
import uoa.lavs.mainframe.messages.customer.LoadCustomer;
import uoa.lavs.mainframe.messages.loan.UpdateLoan;
import uoa.lavs.models.Customer;
import uoa.lavs.models.Loan;

import java.time.LocalDate;

public class Main extends Application {

  private static Scene currentScene;
  private static Stage currentStage;

  public static void main(String[] args) {

    // The following shows two ways of using the mainframe interface
    // Approach #1: Use the singleton instance - this way is recommended as it provides a single
    // configuration
    // location (and is easy for the testers to change when needed).
    Customer customer = new Customer();
    customer.setName("Jessica");
    customer.setTitle("Ms");
    customer.setDob(LocalDate.of(2004, 4, 16));
    customer.setOccupation("Student");
    customer.setCitizenship("NZ");

    // // Creates customer in database and mainframe
    CustomerUpdater.updateData(null, customer);
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
    CustomerUpdater.updateData(null, customer2);
    System.out.println("Customer created");
    System.out.println("Id: " + customer2.getId());
    System.out.println("Name: " + customer2.getName());
    System.out.println("Title: " + customer2.getTitle());
    System.out.println("Date of Birth: " + customer2.getDob());
    System.out.println("Occupation: " + customer2.getOccupation());
    System.out.println("Citizenship: " + customer2.getCitizenship());
    System.out.println("Status: " + customer2.getStatus());
    System.out.println();

    Loan loan = new Loan(null, "1", "Jessica", "New", 100.0, 0.1, RateType.Fixed, LocalDate.of(2021, 4, 16), 3, 5, 100.0, Frequency.Weekly, Frequency.Monthly);

    LoanUpdater.updateData(null, loan);

    LoanLoader.loadData(loan.getLoanId());
    System.out.println("Loan created");
    System.out.println("Loan Id: " + loan.getLoanId());
    System.out.println("Customer Id: " + loan.getCustomerId());
    System.out.println("Customer Name: " + loan.getCustomerName());
    System.out.println("Loan Type: " + loan.getPaymentAmount());
    System.out.println("Loan Amount: " + loan.getCompounding());
    System.out.println("Interest Rate: " + loan.getPaymentFrequency());
    System.out.println("Rate Type: " + loan.getRateType());
    System.out.println("Start Date: " + loan.getStartDate());
    System.out.println("Term: " + loan.getTerm());
    System.out.println("Status: " + loan.getStatus());
    System.out.println();
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
    currentScene = new Scene(loadLoader("start").load(), 1152, 648);
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
