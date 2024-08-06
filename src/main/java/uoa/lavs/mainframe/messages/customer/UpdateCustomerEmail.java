package uoa.lavs.mainframe.messages.customer;

import uoa.lavs.mainframe.*;

public class UpdateCustomerEmail implements Message, MessageDescription {
    public static final int REQUEST_CODE = 1204;
    private Response response;
    private String customerId;
    private Integer number;
    private String address;
    private Integer flags = 0;

    public class Fields {
        public static final String[] INPUT = {"address", "flags", "id", "number"};
        public static final String[] OUTPUT = {"address", "flags"};

        public static final String CUSTOMER_ID = "id";
        public static final String NUMBER = "number";
        public static final String ADDRESS = "address";
        public static final String IS_PRIMARY = "flags";
    }

    @Override
    public Status send(Connection connection) {
        Request request = new Request(REQUEST_CODE);
        if (customerId != null) request.setValue(Fields.CUSTOMER_ID, customerId.toString());
        if (number != null) request.setValue(Fields.NUMBER, number.toString());
        if (address != null) request.setValue(Fields.ADDRESS, address.toString());
        if (flags != null) request.setValue(Fields.IS_PRIMARY, flags.toString());
        response = connection.send(request);
        return response.getStatus();
    }

    // sets customer id [customerId]
    public void setCustomerId(String value)
        throws IllegalArgumentException
     {
        if (value != null && value.length() > 10) {
            throw new IllegalArgumentException("customerId is too long - max length is 10");
        }
        customerId = value;
     }

    // sets number [number]
    public void setNumber(Integer value)
     {
        number = value;
     }

    // sets address [address]
    public void setAddress(String value)
     {
        address = value;
     }

    // sets is primary [IsPrimary]
    public void setIsPrimary(Boolean value)
     {
        flags &= 254;
        if (value) flags |= 1;
     }

    // gets address from server
    public String getAddressFromServer()
     {
        String key = Fields.ADDRESS;
        return response.getValue(key);
     }

    // gets is primary [flags] from server
    public Boolean getIsPrimaryFromServer()
     {
        String key = Fields.IS_PRIMARY;
        String value = response.getValue(key);
        int flags = Integer.parseInt(value);
        return (flags & 1) == 1;
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
