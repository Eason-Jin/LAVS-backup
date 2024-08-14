package uoa.lavs.mainframe.simulator.nitrite;

import org.dizitart.no2.Nitrite;
import org.dizitart.no2.collection.Document;
import org.dizitart.no2.collection.DocumentCursor;
import org.dizitart.no2.collection.NitriteCollection;
import uoa.lavs.mainframe.MessageErrorStatus;
import uoa.lavs.mainframe.Request;
import uoa.lavs.mainframe.Response;
import uoa.lavs.mainframe.Status;
import uoa.lavs.mainframe.messages.customer.UpdateCustomer;
import uoa.lavs.mainframe.messages.loan.UpdateLoan;
import uoa.lavs.mainframe.simulator.NitriteConnection;

import java.util.HashMap;

import static org.dizitart.no2.filters.FluentFilter.where;

public class UpdateLoanProcessor extends BaseProcessor {

    public UpdateLoanProcessor(Nitrite database) {
        super(database);
    }

    @Override
    public Response process(Request request, long transactionId) {
        NitriteCollection loans = getLoansCollection();
        String customerId = request.getValue(UpdateLoan.Fields.CUSTOMER_ID);
        DocumentCursor cursor = getCustomersCollection().find(where(UpdateCustomer.Fields.CUSTOMER_ID).eq(customerId));
        Document customerDoc = cursor.firstOrNull();
        if (customerDoc == null) {
            return MessageErrorStatus.CUSTOMER_NOT_FOUND.generateEmptyResponse(transactionId);
        }


        String id = request.getValue(UpdateLoan.Fields.LOAN_ID);
        Document doc;
        if (id == null) {
            // generate the new loan id
            Integer nextLoanId = customerDoc.get(NitriteConnection.Internal.NEXT_LOAN_ID, Integer.class);
            if (nextLoanId == null) nextLoanId = 1;
            id = String.format(
                    "%s-%02d",
                    customerDoc.get(UpdateCustomer.Fields.CUSTOMER_ID),
                    nextLoanId);
            customerDoc.put(NitriteConnection.Internal.NEXT_LOAN_ID, nextLoanId + 1);
            getCustomersCollection().update(customerDoc);

            doc = Document.createDocument();
            copyInputData(doc, request, UpdateLoan.Fields.INPUT);
            doc.put(UpdateLoan.Fields.STATUS, "New");
            doc.put(UpdateLoan.Fields.LOAN_ID, id);
            doc.put(UpdateLoan.Fields.CUSTOMER_NAME, customerDoc.get(UpdateCustomer.Fields.NAME, String.class));
            loans.insert(doc);
        } else {
            cursor = loans.find(where(UpdateLoan.Fields.LOAN_ID).eq(id));
            doc = cursor.firstOrNull();
            if (doc == null) {
                return MessageErrorStatus.LOAN_NOT_FOUND.generateEmptyResponse(transactionId);
            }
            copyInputData(doc, request, UpdateLoan.Fields.INPUT);
            loans.update(doc);
        }

        HashMap<String, String> data = new HashMap<>();
        copyOutputData(doc, data, UpdateLoan.Fields.OUTPUT);
        return new Response(
                new Status(transactionId),
                data);
    }
}
