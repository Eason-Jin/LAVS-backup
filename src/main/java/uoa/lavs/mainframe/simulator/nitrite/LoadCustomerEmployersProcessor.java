package uoa.lavs.mainframe.simulator.nitrite;

import org.dizitart.no2.Nitrite;
import org.dizitart.no2.collection.Document;
import uoa.lavs.mainframe.messages.customer.LoadCustomerEmployer;
import uoa.lavs.mainframe.messages.customer.LoadCustomerEmployers;
import uoa.lavs.mainframe.simulator.NitriteConnection;

import java.util.HashMap;

public final class LoadCustomerEmployersProcessor extends BaseCustomerItemsProcessor {
    public LoadCustomerEmployersProcessor(Nitrite database) {
        super(database, NitriteConnection.Internal.ITEM_EMPLOYERS);
    }

    @Override
    protected void copyItemData(String key, HashMap<String, String> data, Integer number, Document doc) {
        data.put(
                String.format(LoadCustomerEmployers.Fields.NUMBER, number),
                key);
        data.put(
                String.format(LoadCustomerEmployers.Fields.NAME, number),
                doc.get(LoadCustomerEmployer.Fields.NAME, String.class));
    }
}
