package uoa.lavs.dataoperations;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import uoa.lavs.LocalInstance;
import uoa.lavs.mainframe.Instance;

public class DataOperationsTestsHelper {

  public static Connection createTestingDatabases() throws SQLException {
    LocalInstance.initializeTestConnections(true);
    Connection connection = LocalInstance.getDatabaseConnection();
    createTables(connection);
    return connection;
  }

  private static void createTables(Connection connection) throws SQLException {
    try (Statement stmt = connection.createStatement()) {
      String addressTable =
              """
              CREATE TABLE Address (
                  CustomerID INTEGER   REFERENCES Customer (CustomerID)
                                       NOT NULL,
                  Line1      TEXT (60) NOT NULL,
                  Line2      TEXT,
                  Suburb     TEXT (30) NOT NULL,
                  City       TEXT (30) NOT NULL,
                  Postcode   INTEGER   NOT NULL,
                  Country    TEXT      NOT NULL,
                  IsPrimary  INTEGER   NOT NULL,
                  IsMailing  INTEGER   NOT NULL,
                  Number     INTEGER   NOT NULL,
                  Type       TEXT (20) NOT NULL,
                  PRIMARY KEY (
                      CustomerID,
                      Number
                  )
              );
              """;

      String customerTable =
              """
              CREATE TABLE Customer (
                  CustomerID  INTEGER   PRIMARY KEY
                                        NOT NULL,
                  Name        TEXT (60) NOT NULL,
                  Title       TEXT (10) NOT NULL,
                  Status      TEXT (30) NOT NULL,
                  Dob         TEXT (30) NOT NULL,
                  Occupation  TEXT (40),
                  Citizenship TEXT (40) NOT NULL,
                  VisaType    TEXT (40),
                  Note        TEXT (70)
              );
              """;

      String emailTable =
              """
                CREATE TABLE Email (
                  CustomerID INTEGER   REFERENCES Customer (CustomerID)
                                       NOT NULL,
                  Address    TEXT (60) NOT NULL,
                  IsPrimary  INTEGER   NOT NULL,
                  Number     INTEGER   NOT NULL,
                  PRIMARY KEY (
                      CustomerID,
                      Number
                  )
              );
              """;

      String employerTable =
              """
              CREATE TABLE Employer (
                  CustomerID   INTEGER   REFERENCES Customer (CustomerID)
                                      NOT NULL,
                  Name         TEXT (60) NOT NULL,
                  Line1        TEXT (60) NOT NULL,
                  Line2        TEXT (60),
                  Suburb       TEXT (30) NOT NULL,
                  City         TEXT (30) NOT NULL,
                  Postcode     INTEGER   NOT NULL,
                  Country      TEXT (30) NOT NULL,
                  PhoneNumber  INTEGER   NOT NULL,
                  EmailAddress TEXT (60) NOT NULL,
                  Website      TEXT (60) NOT NULL,
                  Number       INTEGER   NOT NULL,
                  IsOwner      INTEGER   NOT NULL,
                  PRIMARY KEY (
                      CustomerID,
                      Number
                  )
              );
              """;

      String phoneTable =
              """
              CREATE TABLE Phone (
                  CustomerID  INTEGER   REFERENCES Customer (CustomerID)
                                      NOT NULL,
                  Type        TEXT (10) NOT NULL,
                  Prefix      INTEGER   NOT NULL,
                  Number      INTEGER,
                  IsPrimary   INTEGER   NOT NULL,
                  CanSendText INTEGER   NOT NULL,
                  PhoneNumber TEXT (30) NOT NULL,
                  PRIMARY KEY (
                      CustomerID,
                      Number
                  )
              );
              """;

      String insertCustomer =
              """
    INSERT INTO Customer (CustomerID, Name, Title, Status, Dob, Occupation, Citizenship, VisaType, Note)
    VALUES (1, 'John Doe', 'Mr', 'Active', '1990-01-01', 'Engineer', 'NZ', 'Work', 'Note');
    """;

      stmt.executeUpdate(customerTable);
      stmt.executeUpdate(addressTable);
      stmt.executeUpdate(emailTable);
      stmt.executeUpdate(employerTable);
      stmt.executeUpdate(phoneTable);
      stmt.executeUpdate(insertCustomer);
    }
  }
}