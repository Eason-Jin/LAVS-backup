package uoa.lavs.mainframe.simulator.nitrite;

import org.dizitart.no2.Nitrite;
import org.dizitart.no2.collection.Document;
import org.dizitart.no2.collection.DocumentCursor;
import org.dizitart.no2.collection.NitriteCollection;
import uoa.lavs.mainframe.Request;
import uoa.lavs.mainframe.Response;
import uoa.lavs.mainframe.Status;
import uoa.lavs.mainframe.messages.customer.LoadCustomer;
import uoa.lavs.mainframe.MessageErrorStatus;

import java.util.HashMap;

import static org.dizitart.no2.filters.FluentFilter.where;

public class LoadCustomerProcessor extends BaseCustomerProcessor {

    public LoadCustomerProcessor(Nitrite database) {
        super(database);
    }

    @Override
    public Response process(Request request, long transactionId) {
        Response response = loadCustomerDocument(request, transactionId);
        if (response != null) return response;

        HashMap<String, String> data = new HashMap<>();
        copyOutputData(getCustomerDocument(), data, LoadCustomer.Fields.OUTPUT);
        return new Response(
                new Status(transactionId),
                data);
    }
}
