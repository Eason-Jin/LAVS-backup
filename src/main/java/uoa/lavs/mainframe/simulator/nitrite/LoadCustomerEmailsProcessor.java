package uoa.lavs.mainframe.simulator.nitrite;

import org.dizitart.no2.Nitrite;
import org.dizitart.no2.collection.Document;
import uoa.lavs.mainframe.messages.customer.LoadCustomerEmail;
import uoa.lavs.mainframe.messages.customer.LoadCustomerEmails;

import java.util.HashMap;

public final class LoadCustomerEmailsProcessor extends BaseCustomerItemsProcessor {
    public LoadCustomerEmailsProcessor(Nitrite database) {
        super(database, "emails");
    }

    @Override
    protected void copyItemData(String key, HashMap<String, String> data, Integer number, Document doc) {
        data.put(
                String.format(LoadCustomerEmails.Fields.NUMBER, number),
                key);
        data.put(
                String.format(LoadCustomerEmails.Fields.ADDRESS, number),
                doc.get(LoadCustomerEmail.Fields.ADDRESS, String.class));
        data.put(
                String.format(LoadCustomerEmails.Fields.IS_PRIMARY, number),
                doc.get(LoadCustomerEmail.Fields.IS_PRIMARY, String.class));
    }
}
