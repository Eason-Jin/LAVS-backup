package uoa.lavs.mainframe.simulator.nitrite;

import org.dizitart.no2.Nitrite;
import org.dizitart.no2.collection.Document;
import org.dizitart.no2.collection.DocumentCursor;
import uoa.lavs.mainframe.MessageErrorStatus;
import uoa.lavs.mainframe.Request;
import uoa.lavs.mainframe.Response;
import uoa.lavs.mainframe.Status;
import uoa.lavs.mainframe.messages.loan.LoadLoan;
import uoa.lavs.mainframe.messages.loan.UpdateLoanStatus;

import java.util.HashMap;

import static org.dizitart.no2.filters.FluentFilter.where;

public class UpdateLoanStatusProcessor extends BaseProcessor {

    public UpdateLoanStatusProcessor(Nitrite database) {
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

        doc.put(LoadLoan.Fields.STATUS, request.getValue(UpdateLoanStatus.Fields.STATUS));
        getLoansCollection().update(doc);

        HashMap<String, String> data = new HashMap<>();
        copyOutputData(doc, data, LoadLoan.Fields.OUTPUT);
        return new Response(
                new Status(transactionId),
                data);
    }
}
