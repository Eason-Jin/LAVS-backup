package uoa.lavs.mainframe.messages.loan;

import uoa.lavs.mainframe.*;

public class UpdateLoanStatus implements Message, MessageDescription {
    public static final int REQUEST_CODE = 2207;
    public static final String[] INPUT = {"id", "status"};
    public static final String[] OUTPUT = {"customerId", "customerName", "status"};

    private Response response;
    private String loanId;
    private LoanStatus status;

    @Override
    public Status send(Connection connection) {
        Request request = new Request(REQUEST_CODE);
        if (loanId != null) request.setValue("id", loanId.toString());
        switch (status)
        {
            case New:
                request.setValue("status", "1");
                break;
            case Pending:
                request.setValue("status", "2");
                break;
            case Active:
                request.setValue("status", "5");
                break;
            case Cancelled:
                request.setValue("status", "8");
                break;
        }
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

    // sets status [status]
    public void setStatus(LoanStatus value)
     {
        status = value;
     }

    // gets customer id [customerId] from server
    public String getCustomerIdFromServer()
     {
        String key = "customerId";
        return response.getValue(key);
     }

    // gets customer name [customerName] from server
    public String getCustomerNameFromServer()
     {
        String key = "customerName";
        return response.getValue(key);
     }

    // gets status from server
    public LoanStatus getStatusFromServer()
     {
        String key = "status";
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
        return INPUT;
    }

    @Override
    public String[] getOutputFields()
    {
        return OUTPUT;
    }
}
