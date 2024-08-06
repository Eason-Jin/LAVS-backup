package uoa.lavs.mainframe.messages.loan;

import uoa.lavs.mainframe.*;

public class UpdateLoanStatus implements Message, MessageDescription {
    public static final int REQUEST_CODE = 2207;
    private Response response;
    private String loanId;
    private LoanStatus status;

    public class Fields {
        public static final String[] INPUT = {"id", "status"};
        public static final String[] OUTPUT = {"customerId", "customerName", "status"};

        public static final String LOAN_ID = "id";
        public static final String CUSTOMER_ID = "customerId";
        public static final String CUSTOMER_NAME = "customerName";
        public static final String STATUS = "status";
    }

    @Override
    public Status send(Connection connection) {
        Request request = new Request(REQUEST_CODE);
        if (loanId != null) request.setValue(Fields.LOAN_ID, loanId.toString());
        switch (status)
        {
            case New:
                request.setValue(Fields.STATUS, "1");
                break;
            case Pending:
                request.setValue(Fields.STATUS, "2");
                break;
            case Active:
                request.setValue(Fields.STATUS, "5");
                break;
            case Cancelled:
                request.setValue(Fields.STATUS, "8");
                break;
        }
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

    // sets status [status]
    public void setStatus(LoanStatus value)
     {
        status = value;
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
    public LoanStatus getStatusFromServer()
     {
        String key = Fields.STATUS;
        String value = response.getValue(key);
        switch (value)
        {
            case "1":
                return LoanStatus.New;
            case "2":
                return LoanStatus.Pending;
            case "5":
                return LoanStatus.Active;
            case "8":
                return LoanStatus.Cancelled;
        }
        return LoanStatus.Unknown;
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
