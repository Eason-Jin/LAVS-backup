package uoa.lavs.mainframe.simulator.nitrite;

import org.dizitart.no2.Nitrite;
import org.dizitart.no2.collection.Document;
import uoa.lavs.mainframe.Request;
import uoa.lavs.mainframe.Response;
import uoa.lavs.mainframe.Status;
import uoa.lavs.mainframe.messages.customer.UpdateCustomerNote;
import uoa.lavs.mainframe.simulator.NitriteConnection;

import java.util.ArrayList;
import java.util.HashMap;

public class UpdateCustomerNoteProcessor extends BaseCustomerProcessor {
    public UpdateCustomerNoteProcessor(Nitrite database) {
        super(database);
    }

    @Override
    public Response process(Request request, long transactionId) {
        Response response = loadCustomerDocument(request, transactionId);
        if (response != null) return response;

        Document doc = getCustomerDocument();
        String number = request.getValue("number");

        Integer index = Integer.parseInt(number) - 1;
        ArrayList<Document> items = getCustomerItems(NitriteConnection.Internal.ITEM_NOTES);
        if (items == null) {
            items = new ArrayList<>();
            doc.put("notes", items);
        }

        Document item;
        if (index >= items.size()) {
            item = Document.createDocument("number", number);
            items.add(item);
        } else {
            item = items.get(index);
        }

        Integer count = 0;
        for (String name : UpdateCustomerNote.Fields.INPUT) {
            if (!name.endsWith(".line")) continue;
            String value = request.getValue(name);
            item.put(name, value);
            if (value != null) count++;
        }
        item.put(UpdateCustomerNote.Fields.LINE_COUNT, String.valueOf(count));
        getCustomersCollection().update(doc);

        HashMap<String, String> data = new HashMap<>();
        copyOutputData(item, data, UpdateCustomerNote.Fields.OUTPUT);
        data.put(UpdateCustomerNote.Fields.PAGE_COUNT, Integer.toString(items.size()));
        return new Response(
                new Status(transactionId),
                data);
    }
}
