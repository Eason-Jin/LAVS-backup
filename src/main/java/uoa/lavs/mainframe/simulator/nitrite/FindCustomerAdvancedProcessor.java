package uoa.lavs.mainframe.simulator.nitrite;

import org.dizitart.no2.Nitrite;
import org.dizitart.no2.collection.Document;
import org.dizitart.no2.collection.DocumentCursor;
import uoa.lavs.mainframe.MessageErrorStatus;
import uoa.lavs.mainframe.Request;
import uoa.lavs.mainframe.Response;
import uoa.lavs.mainframe.Status;
import uoa.lavs.mainframe.messages.customer.FindCustomerAdvanced;
import uoa.lavs.mainframe.messages.customer.LoadCustomer;

import java.util.HashMap;

import static org.dizitart.no2.filters.FluentFilter.where;

public class FindCustomerAdvancedProcessor extends BaseProcessor {

    public FindCustomerAdvancedProcessor(Nitrite database) {
        super(database);
    }

    @Override
    public Response process(Request request, long transactionId) {
        String searchStr = request.getValue(FindCustomerAdvanced.Fields.SEARCH_NAME);
        if (searchStr == null || searchStr.length() <= 2) {
            return MessageErrorStatus.INVALID_REQUEST_SEARCH.generateEmptyResponse(transactionId);
        }

        HashMap<String, String> data = new HashMap<>();
        DocumentCursor cursor = getCustomersCollection().find(where(LoadCustomer.Fields.NAME).text(searchStr));
        Integer count = 0;
        for (Document doc : cursor) {
            count++;
            data.put(
                    String.format(FindCustomerAdvanced.Fields.ID, count),
                    doc.get("id").toString());
            data.put(
                    String.format(FindCustomerAdvanced.Fields.NAME, count),
                    doc.get("name").toString());
            data.put(
                    String.format(FindCustomerAdvanced.Fields.DATE_OF_BIRTH, count),
                    doc.get("dob").toString());
        }

        data.put(FindCustomerAdvanced.Fields.CUSTOMER_COUNT, count.toString());
        return new Response(
                new Status(transactionId),
                data);
    }
}
