package uoa.lavs.mainframe.simulator.nitrite;

import org.dizitart.no2.Nitrite;
import org.dizitart.no2.collection.Document;
import org.dizitart.no2.collection.DocumentCursor;
import uoa.lavs.mainframe.MessageErrorStatus;
import uoa.lavs.mainframe.Request;
import uoa.lavs.mainframe.Response;

import java.util.ArrayList;

import static org.dizitart.no2.filters.FluentFilter.where;

public abstract class BaseCustomerProcessor extends BaseProcessor {
    private Document customerDocument;

    protected BaseCustomerProcessor(Nitrite database) {
        super(database);
    }

    protected Response loadCustomerDocument(Request request, long transactionId) {
        String id = request.getValue("id");
        Response response = validateId(
                id,
                10,
                MessageErrorStatus.INVALID_REQUEST_CUSTOMER_ID,
                transactionId);
        if (response != null) return response;
        
        DocumentCursor cursor = getCustomersCollection().find(where("id").eq(id));
        customerDocument = cursor.firstOrNull();
        if (customerDocument == null) {
            return MessageErrorStatus.CUSTOMER_NOT_FOUND.generateEmptyResponse(transactionId);
        }

        return null;
    }

    protected Document getCustomerDocument() {
        return customerDocument;
    }

    protected ArrayList<Document> getCustomerItems(String key) {
        return getItemsList(customerDocument, key);
    }
}
