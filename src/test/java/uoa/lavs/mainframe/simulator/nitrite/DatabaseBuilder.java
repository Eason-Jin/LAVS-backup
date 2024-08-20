package uoa.lavs.mainframe.simulator.nitrite;

import org.dizitart.no2.Nitrite;
import org.dizitart.no2.collection.Document;
import org.dizitart.no2.collection.DocumentCursor;
import uoa.lavs.mainframe.messages.customer.LoadCustomer;
import uoa.lavs.mainframe.messages.loan.LoadLoan;
import uoa.lavs.mainframe.simulator.NitriteConnection;

import static org.dizitart.no2.filters.FluentFilter.where;

public class DatabaseBuilder {
    private final Nitrite database;

    public DatabaseBuilder() {
        database = Nitrite.builder().openOrCreate();
    }

    public DatabaseBuilder addCustomer(String id, String name, String dob) {
        Document customer = Document.createDocument()
                .put(LoadCustomer.Fields.CUSTOMER_ID, id)
                .put(LoadCustomer.Fields.DATE_OF_BIRTH, dob)
                .put(LoadCustomer.Fields.NAME, name)
                .put(NitriteConnection.Internal.NEXT_LOAN_ID, 1);
        database.getCollection(NitriteConnection.Internal.CUSTOMERS_COLLECTION)
                .insert(customer);
        return this;
    }

    public DatabaseBuilder addLoan(String customerId, Double principal, Double rate, Double payment) {
        DocumentCursor cursor = database.getCollection(NitriteConnection.Internal.CUSTOMERS_COLLECTION)
                .find(where("id").eq(customerId));
        Document customer = cursor.firstOrNull();
        if (customer == null) throw new IllegalArgumentException("Unknown customer id: " + customerId);
        Integer nextId = customer.get(NitriteConnection.Internal.NEXT_LOAN_ID, Integer.class);
        String id = String.format("%s-%d", customerId, nextId);
        customer.put(NitriteConnection.Internal.NEXT_LOAN_ID, ++nextId);
        database.getCollection(NitriteConnection.Internal.CUSTOMERS_COLLECTION).update(customer);

        Document loan = Document.createDocument()
                .put(LoadLoan.Fields.COMPOUNDING, "2")
                .put(LoadLoan.Fields.CUSTOMER_ID, customerId)
                .put(LoadLoan.Fields.CUSTOMER_NAME, customer.get(LoadCustomer.Fields.NAME, String.class))
                .put(LoadLoan.Fields.LOAN_ID, id)
                .put(LoadLoan.Fields.PAYMENT_AMOUNT, payment.toString())
                .put(LoadLoan.Fields.PAYMENT_FREQUENCY, "4")
                .put(LoadLoan.Fields.PERIOD, "24")
                .put(LoadLoan.Fields.PRINCIPAL, principal.toString())
                .put(LoadLoan.Fields.RATE_TYPE, "2")
                .put(LoadLoan.Fields.RATE_VALUE, rate.toString())
                .put(LoadLoan.Fields.START_DATE, "03-08-2024")
                .put(LoadLoan.Fields.STATUS, "Active")
                .put(LoadLoan.Fields.TERM, "30");
        database.getCollection(NitriteConnection.Internal.LOANS_COLLECTION)
                .insert(loan);
        return this;
    }

    public Nitrite build() {
        return database;
    }
}
