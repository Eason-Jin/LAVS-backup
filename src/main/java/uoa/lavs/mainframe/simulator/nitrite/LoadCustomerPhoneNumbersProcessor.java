package uoa.lavs.mainframe.simulator.nitrite;

import org.dizitart.no2.Nitrite;
import org.dizitart.no2.collection.Document;
import uoa.lavs.mainframe.messages.customer.LoadCustomerPhoneNumber;
import uoa.lavs.mainframe.messages.customer.LoadCustomerPhoneNumbers;

import java.util.HashMap;

public class LoadCustomerPhoneNumbersProcessor extends BaseCustomerItemsProcessor {
    public LoadCustomerPhoneNumbersProcessor(Nitrite database) {
        super(database, "phoneNumbers");
    }

    @Override
    protected void copyItemData(String key, HashMap<String, String> data, Integer number, Document doc) {
        data.put(
                String.format(LoadCustomerPhoneNumbers.Fields.NUMBER, number),
                key);
        data.put(
                String.format(LoadCustomerPhoneNumbers.Fields.TYPE, number),
                doc.get(LoadCustomerPhoneNumber.Fields.TYPE, String.class));
        data.put(
                String.format(LoadCustomerPhoneNumbers.Fields.PREFIX, number),
                doc.get(LoadCustomerPhoneNumber.Fields.PREFIX, String.class));
        data.put(
                String.format(LoadCustomerPhoneNumbers.Fields.PHONE_NUMBER, number),
                doc.get(LoadCustomerPhoneNumber.Fields.PHONE_NUMBER, String.class));
        data.put(
                String.format(LoadCustomerPhoneNumbers.Fields.IS_PRIMARY, number),
                doc.get(LoadCustomerPhoneNumber.Fields.IS_PRIMARY, String.class));
    }
}
