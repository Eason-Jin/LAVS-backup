package uoa.lavs.mainframe.simulator.nitrite;

import org.dizitart.no2.Nitrite;
import org.dizitart.no2.collection.Document;
import org.dizitart.no2.collection.DocumentCursor;
import uoa.lavs.mainframe.Request;
import uoa.lavs.mainframe.Response;
import uoa.lavs.mainframe.Status;
import uoa.lavs.mainframe.messages.customer.LoadCustomer;
import uoa.lavs.mainframe.messages.loan.FindLoan;
import uoa.lavs.mainframe.messages.loan.LoadLoan;

import java.util.HashMap;

import static org.dizitart.no2.filters.FluentFilter.where;

public class FindLoanProcessor extends BaseCustomerProcessor {


    public FindLoanProcessor(Nitrite database) {
        super(database);
    }

    @Override
    public Response process(Request request, long transactionId) {
        Response response = loadCustomerDocument(request, transactionId);
        if (response != null) return response;
        Document customer = getCustomerDocument();

        String id = request.getValue("id");
        DocumentCursor cursor = getLoansCollection().find(where(LoadLoan.Fields.CUSTOMER_ID).eq(id));

        HashMap<String, String> data = new HashMap<>();
        Integer count = 0;
        for (Document doc : cursor) {
            count++;
            data.put(
                    String.format(FindLoan.Fields.LOAN_ID, count),
                    doc.get(LoadLoan.Fields.LOAN_ID).toString());
            data.put(
                    String.format(FindLoan.Fields.PRINCIPAL, count),
                    doc.get(LoadLoan.Fields.PRINCIPAL).toString());
            data.put(
                    String.format(FindLoan.Fields.STATUS, count),
                    doc.get(LoadLoan.Fields.STATUS).toString());
        }

        data.put(FindLoan.Fields.CUSTOMER_ID, customer.get(LoadCustomer.Fields.CUSTOMER_ID).toString());
        data.put(FindLoan.Fields.CUSTOMER_NAME, customer.get(LoadCustomer.Fields.NAME).toString());
        data.put(FindLoan.Fields.COUNT, count.toString());
        return new Response(
                new Status(transactionId),
                data);
    }
}
