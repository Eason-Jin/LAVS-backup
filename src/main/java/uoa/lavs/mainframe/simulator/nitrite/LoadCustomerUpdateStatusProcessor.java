package uoa.lavs.mainframe.simulator.nitrite;

import org.dizitart.no2.Nitrite;
import org.dizitart.no2.collection.Document;
import org.dizitart.no2.collection.DocumentCursor;
import org.dizitart.no2.collection.NitriteCollection;
import uoa.lavs.mainframe.Request;
import uoa.lavs.mainframe.Response;
import uoa.lavs.mainframe.Status;
import uoa.lavs.mainframe.messages.customer.LoadCustomerUpdateStatus;
import uoa.lavs.mainframe.MessageErrorStatus;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;

import static org.dizitart.no2.filters.FluentFilter.where;

public class LoadCustomerUpdateStatusProcessor extends BaseCustomerProcessor {

    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");

    public LoadCustomerUpdateStatusProcessor(Nitrite database) {
        super(database);
    }

    @Override
    public Response process(Request request, long transactionId) {
        Response response = loadCustomerDocument(request, transactionId);
        if (response != null) return response;

        HashMap<String, String> data = new HashMap<>();
        Document doc = getCustomerDocument();
        setDataValue(doc, data, LoadCustomerUpdateStatus.Fields.LAST_DETAILS_CHANGE);
        setDataValue(doc, data, LoadCustomerUpdateStatus.Fields.LAST_ADDRESS_CHANGE);
        setDataValue(doc, data, LoadCustomerUpdateStatus.Fields.LAST_EMAIL_CHANGE);
        setDataValue(doc, data, LoadCustomerUpdateStatus.Fields.LAST_PHONE_NUMBER_CHANGE);
        response = new Response(
                new Status(transactionId),
                data);
        return response;
    }

    private static void setDataValue(Document doc, HashMap<String, String> data, String key) {
        String value = null;
        if (doc.containsKey(key)) {
            LocalDate date = doc.get(key, LocalDate.class);
            value = formatter.format(date);
        }
        data.put(key, value);
    }
}
