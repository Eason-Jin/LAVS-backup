package uoa.lavs;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.DriverManager;
import java.sql.SQLException;
import org.dizitart.no2.Nitrite;
import uoa.lavs.mainframe.Connection;
import uoa.lavs.mainframe.Instance;
import uoa.lavs.mainframe.simulator.NitriteConnection;

public class LocalInstance {

  private LocalInstance() {}

  private static boolean _useTestConnections;

  private static class SingletonHelper {
    private static Connection INSTANCE = Instance.getConnection();
  }

  public static Connection getConnection() {
    return LocalInstance.SingletonHelper.INSTANCE;
  }

  public static java.sql.Connection getDatabaseConnection() {
    java.sql.Connection connection = null;
    try {
      if (_useTestConnections) {
        connection = DriverManager.getConnection("jdbc:sqlite:testDatabase.sqlite");
      } else {
        connection = DriverManager.getConnection("jdbc:sqlite:database.sqlite");
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return connection;
  }

  public static void initializeTestConnections(boolean useTestConnections, Nitrite database) {
    _useTestConnections = useTestConnections;
    SingletonHelper.INSTANCE = new NitriteConnection(database);
    resetTestDatabase();
    resetNitriteDatabase();
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
}
