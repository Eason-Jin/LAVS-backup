package uoa.lavs.mainframe.messages.loan;

import uoa.lavs.mainframe.*;

public class LoadLoanCoborrowers implements Message, MessageDescription {
    public static final int REQUEST_CODE = 2106;
    private Response response;
    private String loanId;
    private Integer number;

    public class Fields {
        public static final String[] INPUT = {"id", "number"};
        public static final String[] OUTPUT = {"[01].id", "[01].name", "[01].number", "[02].id", "[02].name", "[02].number", "[03].id", "[03].name", "[03].number", "[04].id", "[04].name", "[04].number", "[05].id", "[05].name", "[05].number", "[06].id", "[06].name", "[06].number", "[07].id", "[07].name", "[07].number", "[08].id", "[08].name", "[08].number", "[09].id", "[09].name", "[09].number", "[10].id", "[10].name", "[10].number", "[11].id", "[11].name", "[11].number", "[12].id", "[12].name", "[12].number", "[13].id", "[13].name", "[13].number", "[14].id", "[14].name", "[14].number", "[15].id", "[15].name", "[15].number", "[16].id", "[16].name", "[16].number", "[17].id", "[17].name", "[17].number", "[18].id", "[18].name", "[18].number", "count", "customerId", "customerName", "pages"};

        public static final String LOAN_ID = "id";
        public static final String CUSTOMER_ID = "customerId";
        public static final String CUSTOMER_NAME = "customerName";
        public static final String NUMBER = "number";
        public static final String PAGE_COUNT = "pages";
        public static final String COUNT = "count";
        public static final String COBORROWER_NUMBER = "[%02d].number";
        public static final String COBORROWER_ID = "[%02d].id";
        public static final String COBORROWER_NAME = "[%02d].name";
    }

    @Override
    public Status send(Connection connection) {
        Request request = new Request(REQUEST_CODE);
        if (loanId != null) request.setValue(Fields.LOAN_ID, loanId);
        if (number != null) request.setValue(Fields.NUMBER, number.toString());
        response = connection.send(request);
        return response.getStatus();
    }

    // sets loan id [loanId]
    public void setLoanId(String value)
        throws IllegalArgumentException
     {
        if (value != null && value.length() > 14) {
            throw new IllegalArgumentException("loanId is too long - max length is 14");
        }
        loanId = value;
     }

    // sets number [number]
    public void setNumber(Integer value)
     {
        number = value;
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

    // gets page count [pages] from server
    public Integer getPageCountFromServer()
     {
        String key = Fields.PAGE_COUNT;
        String value = response.getValue(key);
        if (value == null) return null;
        return Integer.parseInt(value);
     }

    // gets count from server
    public Integer getCountFromServer()
     {
        String key = Fields.COUNT;
        String value = response.getValue(key);
        if (value == null) return null;
        return Integer.parseInt(value);
     }

    // gets coborrower number [coborrowerNumber] from server
    public Integer getCoborrowerNumberFromServer(Integer coborrower)
     {
        String key = String.format(Fields.COBORROWER_NUMBER, coborrower);
        String value = response.getValue(key);
        if (value == null) return null;
        return Integer.parseInt(value);
     }

    // gets coborrower id [coborrowerId] from server
    public String getCoborrowerIdFromServer(Integer coborrower)
     {
        String key = String.format(Fields.COBORROWER_ID, coborrower);
        return response.getValue(key);
     }

    // gets coborrower name [coborrowerName] from server
    public String getCoborrowerNameFromServer(Integer coborrower)
     {
        String key = String.format(Fields.COBORROWER_NAME, coborrower);
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
