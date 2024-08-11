package uoa.lavs.mainframe.simulator.nitrite;

import org.dizitart.no2.Nitrite;
import org.dizitart.no2.collection.Document;
import org.dizitart.no2.collection.DocumentCursor;
import org.dizitart.no2.collection.NitriteCollection;
import uoa.lavs.mainframe.MessageErrorStatus;
import uoa.lavs.mainframe.Request;
import uoa.lavs.mainframe.Response;
import uoa.lavs.mainframe.simulator.MessageProcessor;
import uoa.lavs.mainframe.simulator.NitriteConnection;

import java.util.ArrayList;
import java.util.HashMap;

import static org.dizitart.no2.filters.FluentFilter.where;

public abstract class BaseProcessor implements MessageProcessor {

    private final Nitrite database;

    protected BaseProcessor(Nitrite database) {
        this.database = database;
    }

    protected String retrieveDocumentField(Document doc, String key) {
        Object value = doc.get(key);
        return value == null ? "" : value.toString();
    }

    protected Response validateId(String id, Integer maxLength, MessageErrorStatus error, long transactionId) {
        if (id == null || id.length() == 0) return error.generateEmptyResponse(transactionId);
        if (id.length() > maxLength) return error.generateEmptyResponse(transactionId);
        for (Character c : id.toCharArray()) {
            if (!Character.isDigit(c) && (c != '-')) return error.generateEmptyResponse(transactionId);
        }
        return null;
    }

    protected NitriteCollection getCustomersCollection() {
        return database.getCollection(NitriteConnection.Internal.CUSTOMERS_COLLECTION);
    }

    protected ArrayList<Document> getItemsList(Document document, String key) {
        ArrayList list = document.get(key, ArrayList.class);
        if (list == null) {
            list = new ArrayList<Document>();
            document.put(key, list);
        }

        return list;
    }

    protected NitriteCollection getLoansCollection() {
        return database.getCollection(NitriteConnection.Internal.LOANS_COLLECTION);
    }

    protected void copyOutputData(Document doc, HashMap<String, String> data, String... names) {
        for (String name : names) {
            String value = doc.get(name, String.class);
            data.put(name, value);
        }
    }

    protected void copyInputData(Document doc, Request request, String... names) {
        for (String name : names) {
            doc.put(name, request.getValue(name));
        }
    }

    protected String generateNewId(String type) {
        NitriteCollection ids = database.getCollection(NitriteConnection.Internal.IDS_COLLECTION);
        DocumentCursor cursor = ids.find(where("type").eq(type));
        Document idDoc = cursor.firstOrNull();
        if (idDoc == null) {
            idDoc = Document.createDocument()
                    .put("type", type)
                    .put("id", 1);
            ids.insert(idDoc);
            return "1";
        }

        Integer id = idDoc.get("id", Integer.class) + 1;
        idDoc.put("id", id);
        ids.update(idDoc);
        return String.format("%d", id);
    }
}
