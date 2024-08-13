package uoa.lavs.mainframe.simulator.nitrite;

import org.dizitart.no2.Nitrite;
import org.dizitart.no2.collection.Document;
import uoa.lavs.mainframe.MessageErrorStatus;
import uoa.lavs.mainframe.Request;
import uoa.lavs.mainframe.Response;
import uoa.lavs.mainframe.Status;
import uoa.lavs.mainframe.messages.customer.LoadCustomerNote;
import uoa.lavs.mainframe.simulator.NitriteConnection;

import java.util.ArrayList;
import java.util.HashMap;

public final class LoadCustomerNoteProcessor extends BaseCustomerProcessor {

    public LoadCustomerNoteProcessor(Nitrite database) {
        super(database);
    }

    @Override
    public Response process(Request request, long transactionId) {
        Response response = loadCustomerDocument(request, transactionId);
        if (response != null) return response;

        String number = request.getValue("number");
        Integer index;
        try {
            index = Integer.parseInt(number) - 1;
        } catch (NumberFormatException e) {
            return MessageErrorStatus.INVALID_REQUEST_NUMBER.generateEmptyResponse(transactionId);
        }
        ArrayList<Document> items = getCustomerItems(NitriteConnection.Internal.ITEM_NOTES);
        if (index < 0 || index >= items.size()) {
            return MessageErrorStatus.CUSTOMER_NOTE_NOT_FOUND.generateEmptyResponse(transactionId);
        }

        Document doc = items.get(index);
        HashMap<String, String> data = new HashMap<>();
        copyOutputData(doc, data, LoadCustomerNote.Fields.OUTPUT);
        data.put(LoadCustomerNote.Fields.PAGE_COUNT, Integer.toString(items.size()));
        return new Response(
                new Status(transactionId),
                data);
    }
}
