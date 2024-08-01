package uoa.lavs.mainframe.messages.loan;

import uoa.lavs.mainframe.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class LoadLoan implements Message, MessageDescription {
    public static final int REQUEST_CODE = 2101;
    private Response response;
    private String loanId;

    public class Fields {
        public static final String[] INPUT = {"id"};
        public static final String[] OUTPUT = {"compounding", "customerId", "customerName", "date", "payment.amount", "payment.freq", "period", "principal", "rate.type", "rate.value", "status", "term"};

        public static final String LOAN_ID = "id";
        public static final String CUSTOMER_ID = "customerId";
        public static final String CUSTOMER_NAME = "customerName";
        public static final String STATUS = "status";
        public static final String PRINCIPAL = "principal";
        public static final String RATE_VALUE = "rate.value";
        public static final String RATE_TYPE = "rate.type";
        public static final String START_DATE = "date";
        public static final String PERIOD = "period";
        public static final String TERM = "term";
        public static final String PAYMENT_AMOUNT = "payment.amount";
        public static final String PAYMENT_FREQUENCY = "payment.freq";
        public static final String COMPOUNDING = "compounding";
    }

    @Override
    public Status send(Connection connection) {
        Request request = new Request(REQUEST_CODE);
        if (loanId != null) request.setValue(Fields.LOAN_ID, loanId.toString());
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

    // gets status from server
    public String getStatusFromServer()
     {
        String key = Fields.STATUS;
        return response.getValue(key);
     }

    // gets principal from server
    public Double getPrincipalFromServer()
     {
        String key = Fields.PRINCIPAL;
        String value = response.getValue(key);
        if (value == null) return 0.0;
        return Double.parseDouble(value);
     }

    // gets rate value [rate.value] from server
    public Double getRateValueFromServer()
     {
        String key = Fields.RATE_VALUE;
        String value = response.getValue(key);
        if (value == null) return 0.0;
        return Double.parseDouble(value);
     }

    // gets rate type [rate.type] from server
    public RateType getRateTypeFromServer()
     {
        String key = Fields.RATE_TYPE;
        String value = response.getValue(key);
        switch (value)
        {
            case "1":
                return RateType.Floating;
            case "2":
                return RateType.Fixed;
            case "3":
                return RateType.InterestOnly;
        }
        return RateType.Unknown;
     }

    // gets start date [date] from server
    public LocalDate getStartDateFromServer()
     {
        String key = Fields.START_DATE;
        String value = response.getValue(key);
        if (value == null) return null;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        return LocalDate.parse(value, formatter);
     }

    // gets period from server
    public Integer getPeriodFromServer()
     {
        String key = Fields.PERIOD;
        String value = response.getValue(key);
        if (value == null) return null;
        return Integer.parseInt(value);
     }

    // gets term from server
    public Integer getTermFromServer()
     {
        String key = Fields.TERM;
        String value = response.getValue(key);
        if (value == null) return null;
        return Integer.parseInt(value);
     }

    // gets payment amount [payment.amount] from server
    public Double getPaymentAmountFromServer()
     {
        String key = Fields.PAYMENT_AMOUNT;
        String value = response.getValue(key);
        if (value == null) return 0.0;
        return Double.parseDouble(value);
     }

    // gets payment frequency [payment.freq] from server
    public Frequency getPaymentFrequencyFromServer()
     {
        String key = Fields.PAYMENT_FREQUENCY;
        String value = response.getValue(key);
        switch (value)
        {
            case "2":
                return Frequency.Weekly;
            case "3":
                return Frequency.Fortnightly;
            case "4":
                return Frequency.Monthly;
        }
        return Frequency.Unknown;
     }

    // gets compounding from server
    public Frequency getCompoundingFromServer()
     {
        String key = Fields.COMPOUNDING;
        String value = response.getValue(key);
        switch (value)
        {
            case "2":
                return Frequency.Weekly;
            case "4":
                return Frequency.Monthly;
            case "7":
                return Frequency.Yearly;
        }
        return Frequency.Unknown;
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
