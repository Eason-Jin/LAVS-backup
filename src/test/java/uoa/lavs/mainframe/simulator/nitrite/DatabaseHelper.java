package uoa.lavs.mainframe.simulator.nitrite;

import org.dizitart.no2.Nitrite;
import org.dizitart.no2.collection.Document;
import uoa.lavs.mainframe.messages.customer.*;
import uoa.lavs.mainframe.messages.loan.LoadLoan;
import uoa.lavs.mainframe.simulator.NitriteConnection;

import java.time.LocalDate;
import java.util.ArrayList;

public class DatabaseHelper {
    public static Nitrite generateDefaultDatabase() {
        Nitrite database = Nitrite.builder().openOrCreate();
        database.getCollection(NitriteConnection.Internal.CUSTOMERS_COLLECTION)
                .insert(generateCustomerDocument("123", "John Doe", true));
        database.getCollection(NitriteConnection.Internal.CUSTOMERS_COLLECTION)
                .insert(generateCustomerDocument("456", "Jane Doe", false));
        database.getCollection(NitriteConnection.Internal.LOANS_COLLECTION)
                .insert(generateLoanDocument());
        database.getCollection(NitriteConnection.Internal.IDS_COLLECTION)
                .insert(Document.createDocument("type", "customer").put("id", 124));
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
                .put("lines", "3")
                .put("[01].line", "Test line #1")
                .put("[02].line", "Test line #2")
                .put("[03].line", "Test line #3");
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
