package uoa.lavs.mainframe;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.DriverManager;
import java.sql.SQLException;

import uoa.lavs.mainframe.simulator.HttpConnection;
import uoa.lavs.mainframe.simulator.NitriteConnection;
import uoa.lavs.mainframe.simulator.SimpleReplayConnection;

// implements the singleton pattern for a mainframe connection
public class Instance {
  // private constructor so that this class can only be initialized internally
  private Instance() {}

  // the path to the data file
  private static final String dataPath = "http://localhost:5000/";

  private static boolean _useTestConnections;

  // internal class to initialize the singleton, this enables lazy-loading
  // for the singleton
  private static class SingletonHelper {
    private static Connection INSTANCE = new HttpConnection(dataPath);
  }

  // return the underlying connection
  public static Connection getConnection() {
    return SingletonHelper.INSTANCE;
  }
}
