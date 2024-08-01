package uoa.lavs.mainframe.messages.loan;

import uoa.lavs.mainframe.*;

public class UpdateLoanCoborrower implements Message, MessageDescription {
    public static final int REQUEST_CODE = 2206;
    private Response response;
    private String loanId;
    private String coborrowerId;

    public class Fields {
        public static final String[] INPUT = {"coborrowerId", "id"};
        public static final String[] OUTPUT = {"coborrowerId", "coborrowerName", "customerId", "customerName"};

        public static final String LOAN_ID = "id";
        public static final String CUSTOMER_ID = "customerId";
        public static final String CUSTOMER_NAME = "customerName";
        public static final String COBORROWER_ID = "coborrowerId";
        public static final String COBORROWER_NAME = "coborrowerName";
    }

    @Override
    public Status send(Connection connection) {
        Request request = new Request(REQUEST_CODE);
        if (loanId != null) request.setValue(Fields.LOAN_ID, loanId.toString());
        if (coborrowerId != null) request.setValue(Fields.COBORROWER_ID, coborrowerId.toString());
        response = connection.send(request);
        return response.getStatus();
    }

    // sets loan id [loanId]
    public void setLoanId(String value)
        throws IllegalArgumentException
     {
        if (value.length() > 14) {
            throw new IllegalArgumentException("loanId is too long - max length is 14");
        }
        loanId = value;
     }

    // sets coborrower id [coborrowerId]
    public void setCoborrowerId(String value)
        throws IllegalArgumentException
     {
        if (value.length() > 10) {
            throw new IllegalArgumentException("coborrowerId is too long - max length is 10");
        }
        coborrowerId = value;
     }

    // gets customer id [customerId] from server
    public String getCustomerIdFromServer()
     {
        String key = Fields.CUSTOMER_ID;
        return response.getValue(key);
     }

    // gets customer name [customerName] from server
    public String getCustomerNameFromServer()
     {
        String key = Fields.CUSTOMER_NAME;
        return response.getValue(key);
     }

    // gets coborrower id [coborrowerId] from server
    public String getCoborrowerIdFromServer()
     {
        String key = Fields.COBORROWER_ID;
        return response.getValue(key);
     }

    // gets coborrower name [coborrowerName] from server
    public String getCoborrowerNameFromServer()
     {
        String key = Fields.COBORROWER_NAME;
        return response.getValue(key);
     }

    @Override
    public String[] getInputFields()
    {
        return Fields.INPUT;
    }

    @Override
    public String[] getOutputFields()
    {
        return Fields.OUTPUT;
    }
}
