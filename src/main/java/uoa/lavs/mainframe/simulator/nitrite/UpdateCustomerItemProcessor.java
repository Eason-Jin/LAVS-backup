package uoa.lavs.mainframe.simulator.nitrite;

import org.dizitart.no2.Nitrite;
import org.dizitart.no2.collection.Document;
import uoa.lavs.mainframe.Request;
import uoa.lavs.mainframe.Response;
import uoa.lavs.mainframe.Status;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;

public class UpdateCustomerItemProcessor extends BaseCustomerProcessor {

    private final String documentItemName;
    private final String[] inputData;
    private final String[] outputData;
    private final String lastUpdateKey;

    public UpdateCustomerItemProcessor(Nitrite database,
                                       String documentItemName,
                                       String[] inputData,
                                       String[] outputData,
                                       String lastUpdateKey) {
        super(database);
        this.documentItemName = documentItemName;
        this.inputData = inputData;
        this.outputData = outputData;
        this.lastUpdateKey = lastUpdateKey;
    }

    @Override
    public Response process(Request request, long transactionId) {
        Response response = loadCustomerDocument(request, transactionId);
        if (response != null) return response;

        Document doc = getCustomerDocument();
        String number = request.getValue("number");
        ArrayList<Document> items = getCustomerItems(documentItemName);
        if (items == null) {
            items = new ArrayList<>();
            doc.put(documentItemName, items);
        }

        Integer index = Integer.parseInt(number) - 1;
        Document item;
        if (index >= items.size()) {
            item = Document.createDocument("number", number);
            items.add(item);
        } else {
            item = items.get(index);
        }

        copyInputData(item, request, inputData);
        doc.put(lastUpdateKey, LocalDate.now());
        getCustomersCollection().update(doc);

        HashMap<String, String> data = new HashMap<>();
        copyOutputData(item, data, outputData);
        return new Response(
                new Status(transactionId),
                data);
    }
}
