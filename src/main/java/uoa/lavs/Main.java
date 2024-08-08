package uoa.lavs;

import uoa.lavs.mainframe.Connection;
import uoa.lavs.mainframe.Instance;
import uoa.lavs.mainframe.Status;
import uoa.lavs.mainframe.messages.customer.LoadCustomer;
import uoa.lavs.mainframe.simulator.RecorderConnection;
import uoa.lavs.mainframe.simulator.SimpleReplayConnection;

import javafx.application.Application;
import javafx.stage.Stage;

import java.io.IOException;

public class Main extends Application {

    public static void main(String[] args) {
        // Launch the JavaFX application
        launch(args);

        // The following shows two ways of using the mainframe interface
        // Approach #1: Use the singleton instance - this way is recommended as it provides a single configuration
        // location (and is easy for the testers to change when needed).
        Connection connection = Instance.getConnection();
        executeTestMessage(connection);

        // Approach #2: Dynamically initialize the interface based on some parameters - this way allows the connection
        // to change when needed (e.g., based on a command-line argument.) But it means that the connection must be
        // passed around in the application.
        String dataPath = args.length > 1 ? args[1] : "lavs-data.txt";
        if (args.length > 0 && args[0].equals("record")) {
            connection = new RecorderConnection(dataPath);
        } else {
            connection = new SimpleReplayConnection(dataPath);
        }
        executeTestMessage(connection);

        // You can use another approach if desired, but make sure you document how the markers can change the
        // connection implementation.
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("JavaFX Application");
        primaryStage.setWidth(400);
        primaryStage.setHeight(300);
        primaryStage.show();
    }

    private static void executeTestMessage(Connection connection) {
        LoadCustomer testMessage = new LoadCustomer();
        testMessage.setCustomerId("123456-789");
        Status status = testMessage.send(connection);
        try {
            connection.close();
        } catch (IOException e) {
            System.out.println("Something went wrong - could not close connection! The message is " + e.getMessage());
            return;
        }

        if (status.getWasSuccessful()) {
            System.out.println("The send was successful: the customer name is " + testMessage.getNameFromServer());
        } else {
            System.out.println("Something went wrong - the send failed! The code is " + status.getErrorCode());
        }
    }
}
