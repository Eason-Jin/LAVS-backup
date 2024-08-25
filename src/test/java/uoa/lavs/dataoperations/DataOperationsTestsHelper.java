package uoa.lavs.dataoperations;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import uoa.lavs.LocalInstance;

public class DataOperationsTestsHelper {

    public static Connection createTestingDatabases() throws SQLException {
        LocalInstance.initializeTestConnections(true);
        Connection connection = LocalInstance.getDatabaseConnection();
        createTables(connection);
        return connection;
    }

    private static void createTables(Connection connection) throws SQLException {
        try (Statement stmt = connection.createStatement()) {
            String customerTable = """
                              CREATE TABLE Customer (
                        CustomerID  TEXT      NOT NULL,
                        Name        TEXT (60) NOT NULL,
                        Title       TEXT (10) NOT NULL,
                        Status      TEXT (30) NOT NULL,
                        Dob         TEXT (30) NOT NULL,
                        Occupation  TEXT (40) NOT NULL,
                        Citizenship TEXT (40) NOT NULL,
                        VisaType    TEXT (40),
                        Note        TEXT,
                        InMainframe INTEGER,
                        PRIMARY KEY (
                            CustomerID
                        )
                    );

                    """;

            String addressTable = """
                                        CREATE TABLE Address (
                        CustomerID  TEXT      NOT NULL,
                        Line1       TEXT (60) NOT NULL,
                        Line2       TEXT,
                        Suburb      TEXT (30) NOT NULL,
                        City        TEXT (30) NOT NULL,
                        Postcode    INTEGER   NOT NULL,
                        Country     TEXT      NOT NULL,
                        IsPrimary   INTEGER   NOT NULL,
                        IsMailing   INTEGER   NOT NULL,
                        Number      INTEGER   NOT NULL,
                        Type        TEXT (20) NOT NULL,
                        InMainframe INTEGER,
                        PRIMARY KEY (
                            CustomerID,
                            Number
                        ),
                        FOREIGN KEY (
                            CustomerID
                        )
                        REFERENCES Customer (CustomerID) ON DELETE CASCADE
                                                         ON UPDATE CASCADE
                    );

                                        """;

            String emailTable = """
                                CREATE TABLE Email (
                        CustomerID  TEXT      NOT NULL,
                        Address     TEXT (60) NOT NULL,
                        IsPrimary   INTEGER   NOT NULL,
                        Number      INTEGER   NOT NULL,
                        InMainframe INTEGER,
                        PRIMARY KEY (
                            CustomerID,
                            Number
                        ),
                        FOREIGN KEY (
                            CustomerID
                        )
                        REFERENCES Customer (CustomerID) ON DELETE CASCADE
                                                         ON UPDATE CASCADE
                    );
                    """;

            String employerTable = """
                                        CREATE TABLE Employer (
                        CustomerID   TEXT      NOT NULL,
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
                        InMainframe  INTEGER,
                        PRIMARY KEY (
                            CustomerID,
                            Number
                        ),
                        FOREIGN KEY (
                            CustomerID
                        )
                        REFERENCES Customer (CustomerID) ON DELETE CASCADE
                                                         ON UPDATE CASCADE
                    );

                                        """;

            String phoneTable = """
                                        CREATE TABLE Phone (
                        CustomerID  TEXT      NOT NULL,
                        Type        TEXT (10) NOT NULL,
                        Prefix      INTEGER   NOT NULL,
                        Number      INTEGER   NOT NULL,
                        IsPrimary   INTEGER   NOT NULL,
                        CanSendText INTEGER   NOT NULL,
                        PhoneNumber TEXT (30) NOT NULL,
                        InMainframe INTEGER,
                        PRIMARY KEY (
                            CustomerID,
                            Number
                        ),
                        FOREIGN KEY (
                            CustomerID
                        )
                        REFERENCES Customer (CustomerID) ON DELETE CASCADE
                                                         ON UPDATE CASCADE
                    );

                                        """;

            String insertCustomer = """
                    INSERT INTO Customer (CustomerID, Name, Title, Status, Dob, Occupation, Citizenship, VisaType, Note, InMainframe)
                    VALUES ('1', 'John Doe', 'Mr', 'Active', '1990-01-01', 'Engineer', 'NZ', 'Work', 'Note', 0);
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
