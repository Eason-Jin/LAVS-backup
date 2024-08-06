package uoa.lavs.mainframe.messages.loan;

import uoa.lavs.mainframe.*;

public class LoadLoanPayments implements Message, MessageDescription {
    public static final int REQUEST_CODE = 2102;
    private Response response;
    private String loanId;
    private Integer number;

    public class Fields {
        public static final String[] INPUT = {"id", "number"};
        public static final String[] OUTPUT = {"[01].interest", "[01].number", "[01].principal", "[01].remaining", "[02].interest", "[02].number", "[02].principal", "[02].remaining", "[03].interest", "[03].number", "[03].principal", "[03].remaining", "[04].interest", "[04].number", "[04].principal", "[04].remaining", "[05].interest", "[05].number", "[05].principal", "[05].remaining", "[06].interest", "[06].number", "[06].principal", "[06].remaining", "[07].interest", "[07].number", "[07].principal", "[07].remaining", "[08].interest", "[08].number", "[08].principal", "[08].remaining", "[09].interest", "[09].number", "[09].principal", "[09].remaining", "[10].interest", "[10].number", "[10].principal", "[10].remaining", "[11].interest", "[11].number", "[11].principal", "[11].remaining", "[12].interest", "[12].number", "[12].principal", "[12].remaining", "[13].interest", "[13].number", "[13].principal", "[13].remaining", "[14].interest", "[14].number", "[14].principal", "[14].remaining", "[15].interest", "[15].number", "[15].principal", "[15].remaining", "[16].interest", "[16].number", "[16].principal", "[16].remaining", "[17].interest", "[17].number", "[17].principal", "[17].remaining", "[18].interest", "[18].number", "[18].principal", "[18].remaining", "customerId", "customerName", "pages", "payments"};

        public static final String LOAN_ID = "id";
        public static final String CUSTOMER_ID = "customerId";
        public static final String CUSTOMER_NAME = "customerName";
        public static final String NUMBER = "number";
        public static final String PAGE_COUNT = "pages";
        public static final String PAYMENT_COUNT = "payments";
        public static final String PAYMENT_INTEREST = "[%02d].interest";
        public static final String PAYMENT_PRINCIPAL = "[%02d].principal";
        public static final String PAYMENT_REMAINING = "[%02d].remaining";
        public static final String PAYMENT_NUMBER = "[%02d].number";
    }

    @Override
    public Status send(Connection connection) {
        Request request = new Request(REQUEST_CODE);
        if (loanId != null) request.setValue(Fields.LOAN_ID, loanId.toString());
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

    // gets payment count [payments] from server
    public Integer getPaymentCountFromServer()
     {
        String key = Fields.PAYMENT_COUNT;
        String value = response.getValue(key);
        if (value == null) return null;
        return Integer.parseInt(value);
     }

    // gets payment interest [paymentInterest] from server
    public Double getPaymentInterestFromServer(Integer payment)
     {
        String key = String.format(Fields.PAYMENT_INTEREST, payment);
        String value = response.getValue(key);
        if (value == null) return 0.0;
        return Double.parseDouble(value);
     }

    // gets payment principal [paymentPrincipal] from server
    public Double getPaymentPrincipalFromServer(Integer payment)
     {
        String key = String.format(Fields.PAYMENT_PRINCIPAL, payment);
        String value = response.getValue(key);
        if (value == null) return 0.0;
        return Double.parseDouble(value);
     }

    // gets payment remaining [paymentRemaining] from server
    public Double getPaymentRemainingFromServer(Integer payment)
     {
        String key = String.format(Fields.PAYMENT_REMAINING, payment);
        String value = response.getValue(key);
        if (value == null) return 0.0;
        return Double.parseDouble(value);
     }

    // gets payment number [paymentNumber] from server
    public Integer getPaymentNumberFromServer(Integer payment)
     {
        String key = String.format(Fields.PAYMENT_NUMBER, payment);
        String value = response.getValue(key);
        if (value == null) return null;
        return Integer.parseInt(value);
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
