package uoa.lavs.mainframe.simulator.nitrite;

import org.dizitart.no2.Nitrite;
import org.dizitart.no2.collection.Document;
import org.dizitart.no2.collection.DocumentCursor;
import uoa.lavs.mainframe.MessageErrorStatus;
import uoa.lavs.mainframe.Request;
import uoa.lavs.mainframe.Response;
import uoa.lavs.mainframe.Status;
import uoa.lavs.mainframe.messages.loan.LoadLoan;
import uoa.lavs.mainframe.messages.loan.LoadLoanPayments;
import uoa.lavs.utility.LoanRepayment;

import java.util.ArrayList;
import java.util.HashMap;

import static org.dizitart.no2.filters.FluentFilter.where;

public class LoadLoanPaymentsProcessor extends LoanBaseProcessor {

    public LoadLoanPaymentsProcessor(Nitrite database) {

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

        ArrayList<LoanRepayment> repayments = generateRepaymentSchedule(doc);
        Integer pages = repayments.size();
        pages = pages % 18 == 0
                ? pages / 18
                : pages / 18 + 1;
        data.put(LoadLoanPayments.Fields.PAGE_COUNT, pages.toString());
        String pageValue = request.getValue(LoadLoanPayments.Fields.NUMBER);
        Integer page = pageValue == null
                ? 1
                : Integer.parseInt(pageValue);
        Integer startIndex = (page - 1) * 18;
        Integer endIndex = page * 18 - 1;
        Integer number = 1;
        Integer paymentsAdded = 0;
        for (Integer loop = startIndex; loop < endIndex && loop < repayments.size(); loop++) {
            LoanRepayment repayment = repayments.get(loop);
            data.put(
                    String.format(LoadLoanPayments.Fields.PAYMENT_PRINCIPAL, number),
                    String.format("%.2f", repayment.getPrincipalAmount())
            );
            data.put(
                    String.format(LoadLoanPayments.Fields.PAYMENT_INTEREST, number),
                    String.format("%.2f", repayment.getInterestAmount())
            );
            data.put(
                    String.format(LoadLoanPayments.Fields.PAYMENT_REMAINING, number),
                    String.format("%.2f", repayment.getRemainingAmount())
            );
            Integer paymentNumber = loop + 1;
            data.put(
                    String.format(LoadLoanPayments.Fields.PAYMENT_NUMBER, number),
                    paymentNumber.toString()
            );
            number++;
            paymentsAdded++;
        }
        data.put(LoadLoanPayments.Fields.PAYMENT_COUNT, paymentsAdded.toString());
        return new Response(
                new Status(transactionId),
                data);
    }
}
