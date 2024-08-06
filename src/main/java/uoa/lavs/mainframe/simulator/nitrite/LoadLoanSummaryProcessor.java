package uoa.lavs.mainframe.simulator.nitrite;

import org.dizitart.no2.Nitrite;
import org.dizitart.no2.collection.Document;
import org.dizitart.no2.collection.DocumentCursor;
import org.dizitart.no2.collection.NitriteCollection;
import uoa.lavs.mainframe.Request;
import uoa.lavs.mainframe.Response;
import uoa.lavs.mainframe.Status;
import uoa.lavs.mainframe.messages.loan.LoadLoan;
import uoa.lavs.mainframe.messages.loan.LoadLoanSummary;
import uoa.lavs.mainframe.MessageErrorStatus;
import uoa.lavs.utility.LoanSummary;

import java.util.HashMap;

import static org.dizitart.no2.filters.FluentFilter.where;

public class LoadLoanSummaryProcessor extends LoanBaseProcessor {

    public LoadLoanSummaryProcessor(Nitrite database) {
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

        LoanSummary summary = new LoanSummary(generateRepaymentSchedule(doc));
        data.put(LoadLoanSummary.Fields.TOTAL_LOAN_COST, String.format("%.2f", summary.getTotalCost()));
        data.put(LoadLoanSummary.Fields.TOTAL_INTEREST, String.format("%.2f", summary.getTotalInterest()));
        return new Response(
                new Status(transactionId),
                data);
    }
}
