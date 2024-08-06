package uoa.lavs.mainframe.messages.customer;

import uoa.lavs.mainframe.*;

public class UpdateCustomerPhoneNumber implements Message, MessageDescription {
    public static final int REQUEST_CODE = 1203;
    private Response response;
    private String customerId;
    private Integer number;
    private String type;
    private String prefix;
    private String phoneNumber;
    private Integer flags = 0;

    public class Fields {
        public static final String[] INPUT = {"flags", "id", "number", "phone", "prefix", "type"};
        public static final String[] OUTPUT = {"flags", "phone", "prefix", "type"};

        public static final String CUSTOMER_ID = "id";
        public static final String NUMBER = "number";
        public static final String TYPE = "type";
        public static final String PREFIX = "prefix";
        public static final String PHONE_NUMBER = "phone";
        public static final String IS_PRIMARY = "flags";
        public static final String CAN_SEND_TXT = "flags";
    }

    @Override
    public Status send(Connection connection) {
        Request request = new Request(REQUEST_CODE);
        if (customerId != null) request.setValue(Fields.CUSTOMER_ID, customerId.toString());
        if (number != null) request.setValue(Fields.NUMBER, number.toString());
        if (type != null) request.setValue(Fields.TYPE, type.toString());
        if (prefix != null) request.setValue(Fields.PREFIX, prefix.toString());
        if (phoneNumber != null) request.setValue(Fields.PHONE_NUMBER, phoneNumber.toString());
        if (flags != null) request.setValue(Fields.IS_PRIMARY, flags.toString());
        if (flags != null) request.setValue(Fields.CAN_SEND_TXT, flags.toString());
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

    // sets type [type]
    public void setType(String value)
     {
        type = value;
     }

    // sets prefix [prefix]
    public void setPrefix(String value)
     {
        prefix = value;
     }

    // sets phone number [phoneNumber]
    public void setPhoneNumber(String value)
     {
        phoneNumber = value;
     }

    // sets is primary [IsPrimary]
    public void setIsPrimary(Boolean value)
     {
        flags &= 254;
        if (value) flags |= 1;
     }

    // sets can send txt [CanSendTxt]
    public void setCanSendTxt(Boolean value)
     {
        flags &= 253;
        if (value) flags |= 2;
     }

    // gets type from server
    public String getTypeFromServer()
     {
        String key = Fields.TYPE;
        return response.getValue(key);
     }

    // gets prefix from server
    public String getPrefixFromServer()
     {
        String key = Fields.PREFIX;
        return response.getValue(key);
     }

    // gets phone number [phone] from server
    public String getPhoneNumberFromServer()
     {
        String key = Fields.PHONE_NUMBER;
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

    // gets can send txt [flags] from server
    public Boolean getCanSendTxtFromServer()
     {
        String key = Fields.CAN_SEND_TXT;
        String value = response.getValue(key);
        int flags = Integer.parseInt(value);
        return (flags & 2) == 2;
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
