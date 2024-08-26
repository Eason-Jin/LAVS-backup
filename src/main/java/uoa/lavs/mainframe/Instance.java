package uoa.lavs.mainframe;


import uoa.lavs.mainframe.simulator.HttpConnection;

// implements the singleton pattern for a mainframe connection
public class Instance {
  // private constructor so that this class can only be initialized internally
  private Instance() {}

  // the path to the data file
  private static final String dataPath = "lavs-data.txt";
  private static final String URL = "http://localhost:5000/";
  private static boolean _useTestConnections;

  // internal class to initialize the singleton, this enables lazy-loading
  // for the singleton
  private static class SingletonHelper {
    // private static Connection INSTANCE = new NitriteConnection(dataPath);
    private static Connection INSTANCE = new HttpConnection(URL);
  }

  // return the underlying connection
  public static Connection getConnection() {
    return SingletonHelper.INSTANCE;
  }
}
