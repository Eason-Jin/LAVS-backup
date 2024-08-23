package uoa.lavs.mainframe;

import java.sql.DriverManager;
import java.sql.SQLException;
import uoa.lavs.mainframe.simulator.HttpConnection;

// implements the singleton pattern for a mainframe connection
public class Instance {
  // the URL to the remote server
  // private static final String BASE_URL = "https://cmppp4wk-7110.aue.devtunnels.ms/";
  private static final String BASE_URL = "http://localhost:5000/";
  private static boolean _useTestConnections;

  // private constructor so that this class can only be initialized internally
  private Instance() {}

  // return the underlying connection
  public static Connection getConnection() {
    return SingletonHelper.INSTANCE;
  }

  // internal class to initialize the singleton, this enables lazy-loading
  // for the singleton
  private static class SingletonHelper {
    private static final Connection INSTANCE = new HttpConnection(BASE_URL);
  }

  public static java.sql.Connection getDatabaseConnection() {
    java.sql.Connection connection = null;
    try {
      if (_useTestConnections == true) {
        connection = DriverManager.getConnection("jdbc:sqlite:testDatabase.sqlite");
      } else {
        connection = DriverManager.getConnection("jdbc:sqlite:database.sqlite");
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return connection;
  }
}
