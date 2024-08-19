package uoa.lavs.mainframe.simulator.nitrite;

import org.dizitart.no2.Nitrite;
import org.dizitart.no2.collection.Document;
import org.dizitart.no2.collection.DocumentCursor;
import uoa.lavs.mainframe.MessageErrorStatus;
import uoa.lavs.mainframe.Request;
import uoa.lavs.mainframe.Response;
import uoa.lavs.mainframe.Status;
import uoa.lavs.mainframe.messages.customer.LoadCustomer;
import uoa.lavs.mainframe.messages.loan.LoadLoan;
import uoa.lavs.mainframe.messages.loan.LoadLoanCoborrowers;
import uoa.lavs.mainframe.simulator.NitriteConnection;

import java.util.ArrayList;
import java.util.HashMap;

import static org.dizitart.no2.filters.FluentFilter.where;

public class LoadLoanCoborrowersProcessor extends LoanBaseProcessor {

    public LoadLoanCoborrowersProcessor(Nitrite database) {

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

        HashMap<String, String> data = new HashMap<>();
        copyOutputData(doc, data, LoadLoan.Fields.OUTPUT);
        ArrayList<Document> items = getItemsList(doc, NitriteConnection.Internal.ITEM_COBORROWERS);
        Integer numberOfItems = 0;
        if (items != null) {
            numberOfItems = items.size();
            for (Integer key = 0; key < numberOfItems; key++) {
                Document item = items.get(key);
                data.put(
                        String.format(LoadLoanCoborrowers.Fields.COBORROWER_ID, key + 1),
                        item.get(LoadCustomer.Fields.CUSTOMER_ID, String.class));
                data.put(
                        String.format(LoadLoanCoborrowers.Fields.COBORROWER_NAME, key + 1),
                        item.get(LoadCustomer.Fields.NAME, String.class));
                data.put(
                        String.format(LoadLoanCoborrowers.Fields.COBORROWER_NUMBER, key + 1),
                        String.format("%d", key + 1));
            }
        }

        data.put(LoadLoanCoborrowers.Fields.PAGE_COUNT, "1");
        data.put(LoadLoanCoborrowers.Fields.COUNT, numberOfItems.toString());
        return new Response(
                new Status(transactionId),
                data);
    }
}
