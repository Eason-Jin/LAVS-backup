package uoa.lavs.dataoperations;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;

import org.dizitart.no2.Nitrite;
import org.dizitart.no2.collection.Document;

import uoa.lavs.LocalInstance;
import uoa.lavs.mainframe.messages.customer.LoadCustomer;
import uoa.lavs.mainframe.messages.customer.LoadCustomerAddress;
import uoa.lavs.mainframe.messages.customer.LoadCustomerEmail;
import uoa.lavs.mainframe.messages.customer.LoadCustomerEmployer;
import uoa.lavs.mainframe.messages.customer.LoadCustomerPhoneNumber;
import uoa.lavs.mainframe.messages.customer.LoadCustomerUpdateStatus;
import uoa.lavs.mainframe.messages.loan.LoadLoan;
import uoa.lavs.mainframe.simulator.NitriteConnection;

public class DataOperationsTestsHelper {

    public static Connection createTestingDatabases() {
        LocalInstance.initializeTestConnections(true, generateNitriteDatabase());
        Connection connection = LocalInstance.getDatabaseConnection();
        try {
            generateSqliteDatabase(connection);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return connection;
    }

    public static Connection createTestingDatabasesForLoans() {
        LocalInstance.initializeTestConnections(true, generateNitriteDatabaseForLoans());
        Connection connection = LocalInstance.getDatabaseConnection();
        try {
            generateSqliteDatabase(connection);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return connection;
    }

    private static void generateSqliteDatabase(Connection connection) throws SQLException {
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
                        PhoneNumber  TEXT   NOT NULL,
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

            String loanTable = """
                    CREATE TABLE Loan (
                        LoanID           TEXT      PRIMARY KEY
                                                   NOT NULL,
                        CustomerID       TEXT      NOT NULL,
                        CustomerName     TEXT (40) NOT NULL,
                        Status           TEXT (30) NOT NULL,
                        Principal        NUMERIC   NOT NULL,
                        RateValue        NUMERIC   NOT NULL,
                        RateType         TEXT (30) NOT NULL,
                        StartDate        TEXT (30) NOT NULL,
                        Period           INTEGER   NOT NULL,
                        Term             INTEGER   NOT NULL,
                        PaymentAmount    NUMERIC   NOT NULL,
                        PaymentFrequency TEXT (30) NOT NULL,
                        Compounding      TEXT (30) NOT NULL,
                        InMainframe      INTEGER,
                        FOREIGN KEY (
                            CustomerID
                        )
                        REFERENCES Customer (CustomerID) ON DELETE CASCADE
                                                         ON UPDATE CASCADE
                    );
                    """;

            String coborrowerTable = """
                    CREATE TABLE Coborrower (
                        LoanID       TEXT    NOT NULL,
                        Id           INTEGER NOT NULL
                                             PRIMARY KEY,
                        CoborrowerID TEXT    NOT NULL,
                        InMainframe  INTEGER,
                        FOREIGN KEY (
                            LoanID
                        )
                        REFERENCES Loan (LoanID) ON DELETE CASCADE
                                                 ON UPDATE CASCADE,
                        FOREIGN KEY (
                            CoborrowerID
                        )
                        REFERENCES Customer (CustomerID) ON DELETE CASCADE
                                                         ON UPDATE CASCADE
                    );
                    """;

            String insertCustomer = """
                    INSERT INTO Customer (CustomerID, Name, Title, Status, Dob, Occupation, Citizenship, VisaType, Note, InMainframe)
                    VALUES ('1', 'Bob Black', 'Mr', 'Active', '1990-01-01', 'Engineer', 'NZ', 'Work', 'Note', 0);
                    """;

            String insertEmail = """
                    INSERT INTO Email (CustomerID, Address, IsPrimary, Number, InMainframe)
                    VALUES ('1', 'john@gmail.com', true, 1, 0);
                    """;

            String insertPhone = """
                    INSERT INTO Phone (CustomerID, Type, Prefix, Number, IsPrimary, CanSendText, PhoneNumber, InMainframe)
                    VALUES ('1', 'Mobile', 64, 1, true, true, '123456789', 0);
                    """;

            String insertAddress = """
                    INSERT INTO Address (CustomerID, Line1, Suburb, City, Postcode, Country, IsPrimary, IsMailing, Number, Type, InMainframe)
                    VALUES ('1', '35 Owens Road', 'Mt Eden', 'Auckland', 1234, 'New Zealand', true, true, 1, 'Residential', 0);
                    """;
            String insertEmployer = """
                    INSERT INTO Employer (CustomerID, Name, Line1, Suburb, City, Postcode, Country, PhoneNumber, EmailAddress, Website, Number, IsOwner, InMainframe)
                    VALUES ('1', 'Tech Corp', '123 Tech Street', 'Tech Suburb', 'Tech City', 1234, 'Tech Country', '0934534345', 'techcorp@tech.com', 'www.techcorp.com', 1, true, 0);
                    """;

            String insertCustomer2 = """
                    INSERT INTO Customer (CustomerID, Name, Title, Status, Dob, Occupation, Citizenship, VisaType, Note, InMainframe)
                    VALUES ('2', 'Bob Black', 'Mr', 'Active', '1990-01-01', 'Engineer', 'NZ', 'Work', 'Note', 0);
                    """;

            String insertLoan = """
                    INSERT INTO Loan (LoanID, CustomerID, CustomerName, Status, Principal, RateValue, RateType, StartDate, Period, Term, PaymentAmount, PaymentFrequency, Compounding, InMainframe)
                    VALUES ('1-01', '1', 'Bob Black', 'Active', 1000, 0.1, 'Fixed', '2021-01-01', 12, 12, 100, 'Monthly', 'Monthly', 0);
                    """;
            String insertEmail2 = """
                    INSERT INTO Email (CustomerID, Address, IsPrimary, Number, InMainframe)
                     VALUES ('1', 'john@gmail.com', true, 2, 0);
                    """;

            String insertPhone2 = """
                    INSERT INTO Phone (CustomerID, Type, Prefix, Number, IsPrimary, CanSendText, PhoneNumber, InMainframe)
                    VALUES ('1', 'Mobile', 64, 2, true, true, '123456789', 0);
                    """;

            String insertAddress2 = """
                    INSERT INTO Address (CustomerID, Line1, Suburb, City, Postcode, Country, IsPrimary, IsMailing, Number, Type, InMainframe)
                    VALUES ('1', '35 Owens Road', 'Mt Eden', 'Auckland', 1234, 'New Zealand', true, true, 2, 'Residential', 0);
                    """;

            String insertEmployer2 = """
                                        INSERT INTO Employer (CustomerID, Name, Line1, Suburb, City, Postcode, Country, PhoneNumber, EmailAddress, Website, Number, IsOwner, InMainframe)
                                        VALUES ('1', 'Tech Corp', '123 Tech Street', 'Tech Suburb', 'Tech City', 1234, 'Tech Country', '0934534345', 'techcorp@tech.com', 'www.techcorp.com', 2, true, 0);
                    """;

            String insertCoborrower = """
                    INSERT INTO Coborrower (LoanID, Id, CoborrowerID, InMainframe)
                    VALUES ('1-01', 1, '2', 0);
                    """;

            stmt.executeUpdate(customerTable);
            stmt.executeUpdate(addressTable);
            stmt.executeUpdate(emailTable);
            stmt.executeUpdate(employerTable);
            stmt.executeUpdate(phoneTable);
            stmt.executeUpdate(loanTable);
            stmt.executeUpdate(coborrowerTable);
            stmt.executeUpdate(insertCustomer);
            stmt.executeUpdate(insertEmail);
            stmt.executeUpdate(insertPhone);
            stmt.executeUpdate(insertAddress);
            stmt.executeUpdate(insertEmployer);
            stmt.executeUpdate(insertLoan);
            stmt.executeUpdate(insertCustomer2);
            stmt.executeUpdate(insertAddress2);
            stmt.executeUpdate(insertEmail2);
            stmt.executeUpdate(insertPhone2);
            stmt.executeUpdate(insertEmployer2);
            stmt.executeUpdate(insertCoborrower);
        }
    }

    private static Nitrite generateNitriteDatabase() {
        Nitrite database = Nitrite.builder().openOrCreate();
        database.getCollection(NitriteConnection.Internal.CUSTOMERS_COLLECTION)
                .insert(generateCustomerDocument("123", "John Doe", true));
        database.getCollection(NitriteConnection.Internal.CUSTOMERS_COLLECTION)
                .insert(generateCustomerDocument("456", "Jane Doe", false));
        database.getCollection(NitriteConnection.Internal.CUSTOMERS_COLLECTION)
                .insert(generateCustomerDetailsOnly("654", "Jane Doe", false));
        database.getCollection(NitriteConnection.Internal.LOANS_COLLECTION)
                .insert(generateLoanDocument());
        database.getCollection(NitriteConnection.Internal.IDS_COLLECTION)
                .insert(Document.createDocument("type", "customer").put("id", 124));
        return database;
    }

    private static Nitrite generateNitriteDatabaseForLoans() {
        Nitrite database = Nitrite.builder().openOrCreate();
        database.getCollection(NitriteConnection.Internal.CUSTOMERS_COLLECTION)
                .insert(generateCustomerDocument("123", "John Doe", true));
        database.getCollection(NitriteConnection.Internal.CUSTOMERS_COLLECTION)
                .insert(generateCustomerDocument("456", "Jane Doe", false));
        database.getCollection(NitriteConnection.Internal.CUSTOMERS_COLLECTION)
                .insert(generateCustomerDetailsOnly("654", "Jane Doe", false));
        database.getCollection(NitriteConnection.Internal.LOANS_COLLECTION)
                .insert(generateLoanDocument());
        database.getCollection(NitriteConnection.Internal.IDS_COLLECTION)
                .insert(Document.createDocument("type", "customer").put("id", 124));
        database.getCollection(NitriteConnection.Internal.CUSTOMERS_COLLECTION)
                .insert(generateCustomerDocument("1", "Bob Black", true));
        database.getCollection(NitriteConnection.Internal.CUSTOMERS_COLLECTION)
                .insert(generateCustomerDocument("2", "Panda Bear", true));
        database.getCollection(NitriteConnection.Internal.LOANS_COLLECTION)
                .insert(generateSecondLoanDocument());
        return database;
    }

    private static Document generateCustomerDocument(String id, String name, boolean includeDates) {
        Document customer = Document.createDocument()
                .put(LoadCustomer.Fields.CITIZENSHIP, "New Zealand")
                .put(LoadCustomer.Fields.CUSTOMER_ID, id)
                .put(LoadCustomer.Fields.DATE_OF_BIRTH, "12-03-1945")
                .put(LoadCustomer.Fields.NAME, name)
                .put(LoadCustomer.Fields.OCCUPATION, "Test dummy")
                .put(LoadCustomer.Fields.STATUS, "Active")
                .put(LoadCustomer.Fields.TITLE, "Mr")
                .put(LoadCustomer.Fields.VISA, "n/a")
                .put(NitriteConnection.Internal.NEXT_LOAN_ID, 10);
        if (includeDates) {
            customer
                    .put(LoadCustomerUpdateStatus.Fields.LAST_ADDRESS_CHANGE, LocalDate.of(2024, 8, 2))
                    .put(LoadCustomerUpdateStatus.Fields.LAST_DETAILS_CHANGE, LocalDate.of(2024, 8, 1))
                    .put(LoadCustomerUpdateStatus.Fields.LAST_EMAIL_CHANGE, LocalDate.of(2024, 8, 3))
                    .put(LoadCustomerUpdateStatus.Fields.LAST_PHONE_NUMBER_CHANGE, LocalDate.of(2024, 8, 4));
        }
        appendCustomerAddress(customer);
        appendCustomerEmail(customer);
        appendCustomerEmployer(customer);
        appendCustomerNote(customer);
        appendCustomerPhoneNumber(customer);
        return customer;
    }

    private static Document generateCustomerDetailsOnly(String id, String name, boolean includeDates) {
        Document customer = Document.createDocument()
                .put(LoadCustomer.Fields.CITIZENSHIP, "New Zealand")
                .put(LoadCustomer.Fields.CUSTOMER_ID, id)
                .put(LoadCustomer.Fields.DATE_OF_BIRTH, "12-03-1945")
                .put(LoadCustomer.Fields.NAME, name)
                .put(LoadCustomer.Fields.OCCUPATION, "Test dummy")
                .put(LoadCustomer.Fields.STATUS, "Active")
                .put(LoadCustomer.Fields.TITLE, "Mr")
                .put(LoadCustomer.Fields.VISA, "n/a")
                .put(NitriteConnection.Internal.NEXT_LOAN_ID, 10);
        return customer;
    }

    private static Document generateLoanDocument() {
        Document customer = Document.createDocument()
                .put(LoadLoan.Fields.COMPOUNDING, "2")
                .put(LoadLoan.Fields.CUSTOMER_ID, "123")
                .put(LoadLoan.Fields.CUSTOMER_NAME, "John Doe")
                .put(LoadLoan.Fields.LOAN_ID, "123-09")
                .put(LoadLoan.Fields.PAYMENT_AMOUNT, "573.00")
                .put(LoadLoan.Fields.PAYMENT_FREQUENCY, "4")
                .put(LoadLoan.Fields.PERIOD, "24")
                .put(LoadLoan.Fields.PRINCIPAL, "10000.00")
                .put(LoadLoan.Fields.RATE_TYPE, "2")
                .put(LoadLoan.Fields.RATE_VALUE, "7.65")
                .put(LoadLoan.Fields.START_DATE, "03-08-2024")
                .put(LoadLoan.Fields.STATUS, "Active")
                .put(LoadLoan.Fields.TERM, "30");
        appendLoanCoborrowers(customer);
        return customer;
    }

    private static Document generateSecondLoanDocument() {
        Document customer = Document.createDocument()
                .put(LoadLoan.Fields.COMPOUNDING, "2")
                .put(LoadLoan.Fields.CUSTOMER_ID, "1")
                .put(LoadLoan.Fields.CUSTOMER_NAME, "John Doe")
                .put(LoadLoan.Fields.LOAN_ID, "1-01")
                .put(LoadLoan.Fields.PAYMENT_AMOUNT, "573.00")
                .put(LoadLoan.Fields.PAYMENT_FREQUENCY, "4")
                .put(LoadLoan.Fields.PERIOD, "24")
                .put(LoadLoan.Fields.PRINCIPAL, "10000.00")
                .put(LoadLoan.Fields.RATE_TYPE, "2")
                .put(LoadLoan.Fields.RATE_VALUE, "7.65")
                .put(LoadLoan.Fields.START_DATE, "03-08-2024")
                .put(LoadLoan.Fields.STATUS, "Active")
                .put(LoadLoan.Fields.TERM, "30");
        appendLoanCoborrowers(customer);
        return customer;
    }

    private static ArrayList<Document> getItemArray(Document document, String key) {
        ArrayList<Document> items = document.get(key, ArrayList.class);
        if (items == null) {
            items = new ArrayList<>();
            document.put(key, items);
        }

        return items;
    }

    private static Document appendLoanCoborrowers(Document document) {
        Document coborrower = Document.createDocument()
                .put(LoadCustomer.Fields.CUSTOMER_ID, "456")
                .put(LoadCustomer.Fields.NAME, "John Doe");
        ArrayList<Document> items = getItemArray(document, NitriteConnection.Internal.ITEM_COBORROWERS);
        items.add(coborrower);
        return document;
    }

    private static Document appendCustomerAddress(Document document) {
        Document address = Document.createDocument()
                .put(LoadCustomerAddress.Fields.TYPE, "Home")
                .put(LoadCustomerAddress.Fields.LINE_1, "5 Somewhere Lane")
                .put(LoadCustomerAddress.Fields.LINE_2, "Nowhere")
                .put(LoadCustomerAddress.Fields.SUBURB, "Important")
                .put(LoadCustomerAddress.Fields.CITY, "Auckland")
                .put(LoadCustomerAddress.Fields.POST_CODE, "1234")
                .put(LoadCustomerAddress.Fields.COUNTRY, "New Zealand")
                .put(LoadCustomerAddress.Fields.IS_PRIMARY, "3");
        ArrayList<Document> items = getItemArray(document, NitriteConnection.Internal.ITEM_ADDRESSES);
        items.add(address);
        return document;
    }

    private static Document appendCustomerEmail(Document document) {
        Document address = Document.createDocument()
                .put(LoadCustomerEmail.Fields.ADDRESS, "noone@nowhere.co.no")
                .put(LoadCustomerEmail.Fields.IS_PRIMARY, "1");
        ArrayList<Document> items = getItemArray(document, NitriteConnection.Internal.ITEM_EMAILS);
        items.add(address);
        return document;
    }

    private static Document appendCustomerEmployer(Document document) {
        Document employer = Document.createDocument()
                .put(LoadCustomerEmployer.Fields.NUMBER, "1")
                .put(LoadCustomerEmployer.Fields.NAME, "The Best Employer")
                .put(LoadCustomerEmployer.Fields.LINE_1, "5 Somewhere Lane")
                .put(LoadCustomerEmployer.Fields.LINE_2, "Nowhere")
                .put(LoadCustomerEmployer.Fields.SUBURB, "Important")
                .put(LoadCustomerEmployer.Fields.CITY, "Auckland")
                .put(LoadCustomerEmployer.Fields.POST_CODE, "1234")
                .put(LoadCustomerEmployer.Fields.COUNTRY, "New Zealand")
                .put(LoadCustomerEmployer.Fields.EMAIL_ADDRESS, "bigboss@nowhere.co.no")
                .put(LoadCustomerEmployer.Fields.PHONE_NUMBER, "+99-123-9876")
                .put(LoadCustomerEmployer.Fields.WEBSITE, "http://nowhere.co.no")
                .put(LoadCustomerEmployer.Fields.IS_OWNER, "1");
        ArrayList<Document> items = getItemArray(document, NitriteConnection.Internal.ITEM_EMPLOYERS);
        items.add(employer);
        return document;
    }

    private static Document appendCustomerNote(Document document) {
        Document note = Document.createDocument()
                .put("[01].line", "Test line #1")
                .put("[02].line", "Test line #2")
                .put("[03].line", "Test line #3")
                .put("[04].line", "Test line #4")
                .put("[05].line", "Test line #5")
                .put("[06].line", null);
        ArrayList<Document> items = getItemArray(document, NitriteConnection.Internal.ITEM_NOTES);
        items.add(note);
        return document;
    }

    private static Document appendCustomerPhoneNumber(Document document) {
        Document phoneNumber = Document.createDocument()
                .put(LoadCustomerPhoneNumber.Fields.TYPE, "Mobile")
                .put(LoadCustomerPhoneNumber.Fields.PREFIX, "+99")
                .put(LoadCustomerPhoneNumber.Fields.PHONE_NUMBER, "123-4567")
                .put(LoadCustomerPhoneNumber.Fields.IS_PRIMARY, "3");
        ArrayList<Document> items = getItemArray(document, NitriteConnection.Internal.ITEM_PHONE_NUMBERS);
        items.add(phoneNumber);
        return document;
    }
}
