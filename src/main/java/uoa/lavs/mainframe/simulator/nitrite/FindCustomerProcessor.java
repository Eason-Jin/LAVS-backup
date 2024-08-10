package uoa.lavs.mainframe.simulator.nitrite;

import org.dizitart.no2.Nitrite;
import org.dizitart.no2.collection.Document;
import org.dizitart.no2.collection.DocumentCursor;
import uoa.lavs.mainframe.MessageErrorStatus;
import uoa.lavs.mainframe.Request;
import uoa.lavs.mainframe.Response;
import uoa.lavs.mainframe.Status;
import uoa.lavs.mainframe.messages.customer.FindCustomer;

import java.util.HashMap;

import static org.dizitart.no2.filters.FluentFilter.where;

public class FindCustomerProcessor extends BaseProcessor {

    public FindCustomerProcessor(Nitrite database) {
        super(database);
    }

    @Override
    public Response process(Request request, long transactionId) {
        String id = request.getValue("id");
        Response response = validateId(
                id,
                10,
                MessageErrorStatus.INVALID_REQUEST_CUSTOMER_ID,
                transactionId);
        if (response != null) return response;

        DocumentCursor cursor = getCustomersCollection().find(where("id").eq(id));
        Document doc = cursor.firstOrNull();

        HashMap<String, String> data = new HashMap<>();
        Integer count = 0;
        if (doc != null) {
            count = 1;
            data.put(
                    String.format(FindCustomer.Fields.ID, 1),
                    retrieveDocumentField(doc, "id"));
            data.put(
                    String.format(FindCustomer.Fields.NAME, 1),
                    retrieveDocumentField(doc, "name"));
            data.put(
                    String.format(FindCustomer.Fields.DATE_OF_BIRTH, 1),
                    retrieveDocumentField(doc, "dob"));
        }

        data.put("count", count.toString());
        return new Response(
                new Status(transactionId),
                data);
    }
}
