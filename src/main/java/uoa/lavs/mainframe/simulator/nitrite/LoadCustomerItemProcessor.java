package uoa.lavs.mainframe.simulator.nitrite;

import org.dizitart.no2.Nitrite;
import org.dizitart.no2.collection.Document;
import uoa.lavs.mainframe.MessageErrorStatus;
import uoa.lavs.mainframe.Request;
import uoa.lavs.mainframe.Response;
import uoa.lavs.mainframe.Status;

import java.util.ArrayList;
import java.util.HashMap;

public class LoadCustomerItemProcessor extends BaseCustomerProcessor {

    private final String documentItemName;
    private final MessageErrorStatus errorStatus;
    private final String[] outputData;

    public LoadCustomerItemProcessor(Nitrite database,
                                     String documentItemName,
                                     MessageErrorStatus errorStatus,
                                     String[] outputData) {
        super(database);
        this.documentItemName = documentItemName;
        this.errorStatus = errorStatus;
        this.outputData = outputData;
    }

    @Override
    public Response process(Request request, long transactionId) {
        Response response = loadCustomerDocument(request, transactionId);
        if (response != null) return response;

        String number = request.getValue("number");
        Integer index = Integer.parseInt(number) - 1;
        ArrayList<Document> items = getCustomerItems(documentItemName);
        if (index < 0 || index >= items.size()) {
            return errorStatus.generateEmptyResponse(transactionId);
        }

        Document item = items.get(index);
        HashMap<String, String> data = new HashMap<>();
        copyOutputData(item, data, outputData);
        return new Response(
                new Status(transactionId),
                data);
    }
}
