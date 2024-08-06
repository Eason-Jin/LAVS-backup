package uoa.lavs.mainframe.simulator.nitrite;

import org.dizitart.no2.Nitrite;
import org.dizitart.no2.collection.Document;
import uoa.lavs.mainframe.Request;
import uoa.lavs.mainframe.Response;
import uoa.lavs.mainframe.Status;
import uoa.lavs.mainframe.messages.customer.LoadCustomerAddresses;

import java.util.ArrayList;
import java.util.HashMap;

public abstract class BaseCustomerItemsProcessor extends BaseCustomerProcessor {
    private final String collectionName;

    protected BaseCustomerItemsProcessor(Nitrite database, String collectionName) {
        super(database);
        this.collectionName = collectionName;
    }

    @Override
    public Response process(Request request, long transactionId) {
        Response response = loadCustomerDocument(request, transactionId);
        if (response != null) return response;

        ArrayList<Document> addresses = getCustomerItems(collectionName);
        HashMap<String, String> data = new HashMap<>();
        data.put(LoadCustomerAddresses.Fields.COUNT, String.valueOf(addresses.size()));
        for (Integer index = 0; index < addresses.size(); index++) {
            String key = String.format("%02d", index + 1);
            Document doc = addresses.get(index);
            copyItemData(key, data, index + 1, doc);
        }

        return new Response(
                new Status(transactionId),
                data);
    }

    protected abstract void copyItemData(String key, HashMap<String, String> data, Integer number, Document doc);
}
