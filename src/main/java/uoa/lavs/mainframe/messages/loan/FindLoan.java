package uoa.lavs.mainframe.messages.loan;

import uoa.lavs.mainframe.*;

public class FindLoan implements Message, MessageDescription {
    public static final int REQUEST_CODE = 2001;
    private Response response;
    private String id;

    @Override
    public Status send(Connection connection) {
        Request request = new Request(REQUEST_CODE);
        if (id != null) request.setValue(Fields.ID, id);
        response = connection.send(request);
        return response.getStatus();
    }

    // sets id [id]
    public void setId(String value)
            throws IllegalArgumentException {
        if (value != null && value.length() > 10) {
            throw new IllegalArgumentException("id is too long - max length is 10");
        }
        id = value;
    }

    // gets count from server
    public Integer getCountFromServer() {
        String key = Fields.COUNT;
        String value = response.getValue(key);
        if (value == null) return null;
        return Integer.parseInt(value);
    }

    // gets customer id [customerId] from server
    public String getCustomerIdFromServer() {
        String key = Fields.CUSTOMER_ID;
        return response.getValue(key);
    }

    // gets customer name [customerName] from server
    public String getCustomerNameFromServer() {
        String key = Fields.CUSTOMER_NAME;
        return response.getValue(key);
    }

    // gets loan id [loanId] from server
    public String getLoanIdFromServer(Integer row) {
        String key = String.format(Fields.LOAN_ID, row);
        return response.getValue(key);
    }

    // gets status from server
    public String getStatusFromServer(Integer row) {
        String key = String.format(Fields.STATUS, row);
        return response.getValue(key);
    }

    // gets principal from server
    public Double getPrincipalFromServer(Integer row) {
        String key = String.format(Fields.PRINCIPAL, row);
        String value = response.getValue(key);
        if (value == null) return 0.0;
        return Double.parseDouble(value);
    }

    @Override
    public String[] getInputFields() {
        return Fields.INPUT;
    }

    @Override
    public String[] getOutputFields() {
        return Fields.OUTPUT;
    }

    public class Fields {
        public static final String[] INPUT = {"id"};
        public static final String[] OUTPUT = {"[01].id", "[01].principal", "[01].status", "[02].id", "[02].principal", "[02].status", "[03].id", "[03].principal", "[03].status", "[04].id", "[04].principal", "[04].status", "[05].id", "[05].principal", "[05].status", "count", "customerId", "customerName"};

        public static final String ID = "id";
        public static final String COUNT = "count";
        public static final String CUSTOMER_ID = "customerId";
        public static final String CUSTOMER_NAME = "customerName";
        public static final String LOAN_ID = "[%02d].id";
        public static final String STATUS = "[%02d].status";
        public static final String PRINCIPAL = "[%02d].principal";
    }
}
