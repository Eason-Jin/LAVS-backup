package uoa.lavs.mainframe.simulator.nitrite;

import org.dizitart.no2.Nitrite;
import org.dizitart.no2.collection.Document;
import org.dizitart.no2.collection.DocumentCursor;
import uoa.lavs.mainframe.MessageErrorStatus;
import uoa.lavs.mainframe.Request;
import uoa.lavs.mainframe.Response;
import uoa.lavs.mainframe.Status;
import uoa.lavs.mainframe.messages.customer.LoadCustomer;
import uoa.lavs.mainframe.messages.loan.UpdateLoanCoborrower;
import uoa.lavs.mainframe.simulator.NitriteConnection;

import java.util.ArrayList;
import java.util.HashMap;

import static org.dizitart.no2.filters.FluentFilter.where;

public class UpdateLoanCoborrowerProcessor extends BaseProcessor {

    public UpdateLoanCoborrowerProcessor(Nitrite database) {
        super(database);
    }

    @Override
    public Response process(Request request, long transactionId) {
        String id = request.getValue("id");
        DocumentCursor cursor = getLoansCollection().find(where("id").eq(id));
        Document doc = cursor.firstOrNull();
        if (doc == null) {
            return MessageErrorStatus.LOAN_NOT_FOUND.generateEmptyResponse(transactionId);
        }

        String number = request.getValue("number");
        if (number == null) {
            return MessageErrorStatus.INVALID_REQUEST_NUMBER.generateEmptyResponse(transactionId);
        }

        Integer index = Integer.parseInt(number) - 1;
        ArrayList<Document> items = doc.get(NitriteConnection.Internal.ITEM_COBORROWERS, ArrayList.class);
        if (items == null) {
            items = new ArrayList<>();
            doc.put(NitriteConnection.Internal.ITEM_COBORROWERS, items);
        }

        Document item;
        if (index >= items.size()) {
            item = Document.createDocument("number", number);
            items.add(item);
            number = String.valueOf(items.size());
        } else {
            item = items.get(index);
        }

        String coborrowerId = request.getValue(UpdateLoanCoborrower.Fields.COBORROWER_ID);
        cursor = getCustomersCollection().find(where("id").eq(coborrowerId));
        Document customerDocument = cursor.firstOrNull();
        if (customerDocument == null) {
            return MessageErrorStatus.INVALID_COBORROWER_ID.generateEmptyResponse(transactionId);
        }

        String name = customerDocument.get(LoadCustomer.Fields.NAME, String.class);
        item.put(LoadCustomer.Fields.CUSTOMER_ID, coborrowerId);
        item.put(LoadCustomer.Fields.NAME, name);
        getLoansCollection().update(doc);

        HashMap<String, String> data = new HashMap<>();
        copyOutputData(doc, data, UpdateLoanCoborrower.Fields.OUTPUT);
        data.put(UpdateLoanCoborrower.Fields.COBORROWER_ID, coborrowerId);
        data.put(UpdateLoanCoborrower.Fields.COBORROWER_NAME, name);
        data.put(UpdateLoanCoborrower.Fields.NUMBER, number);
        return new Response(
                new Status(transactionId),
                data);
    }
}
