package uoa.lavs.mainframe;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.DriverManager;
import java.sql.SQLException;
import uoa.lavs.mainframe.simulator.NitriteConnection;

// implements the singleton pattern for a mainframe connection
public class Instance {
  // private constructor so that this class can only be initialized internally
  private Instance() {}

  // the path to the data file
  private static final String dataPath = "lavs-data.txt";

  private static boolean _useTestConnections;

  // internal class to initialize the singleton, this enables lazy-loading
  // for the singleton
  private static class SingletonHelper {
    private static Connection INSTANCE = new NitriteConnection(dataPath);
  }

  // return the underlying connection
  public static Connection getConnection() {
    return SingletonHelper.INSTANCE;
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

  public static void initializeTestConnections(boolean useTestConnections) {
    _useTestConnections = useTestConnections;
    SingletonHelper.INSTANCE = new NitriteConnection("testNitriteDatabase.txt");
    resetTestDatabase();
    resetNitriteDatabase();
  }

  private static void resetTestDatabase() {
    Path testDatabasePath = Paths.get("testDatabase.sqlite");
    try {
      if (Files.exists(testDatabasePath)) {
        Files.delete(testDatabasePath);
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  private static void resetNitriteDatabase() {
    Path databasePath = Paths.get("testNitriteDatabase.txt");
    try {
      if (Files.exists(databasePath)) {
        Files.delete(databasePath);
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
