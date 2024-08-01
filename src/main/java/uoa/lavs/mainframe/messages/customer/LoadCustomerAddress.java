package uoa.lavs.mainframe.messages.customer;

import uoa.lavs.mainframe.*;

public class LoadCustomerAddress implements Message, MessageDescription {
    public static final int REQUEST_CODE = 1102;
    private Response response;
    private String customerId;
    private Integer number;

    public class Fields {
        public static final String[] INPUT = {"id", "number"};
        public static final String[] OUTPUT = {"city", "country", "flags", "line1", "line2", "postCode", "suburb", "type"};

        public static final String CUSTOMER_ID = "id";
        public static final String NUMBER = "number";
        public static final String TYPE = "type";
        public static final String LINE_1 = "line1";
        public static final String LINE_2 = "line2";
        public static final String SUBURB = "suburb";
        public static final String CITY = "city";
        public static final String POST_CODE = "postCode";
        public static final String COUNTRY = "country";
        public static final String IS_PRIMARY = "flags";
        public static final String IS_MAILING = "flags";
    }

    @Override
    public Status send(Connection connection) {
        Request request = new Request(REQUEST_CODE);
        if (customerId != null) request.setValue(Fields.CUSTOMER_ID, customerId.toString());
        if (number != null) request.setValue(Fields.NUMBER, number.toString());
        response = connection.send(request);
        return response.getStatus();
    }

    // sets customer id [customerId]
    public void setCustomerId(String value)
        throws IllegalArgumentException
     {
        if (value.length() > 10) {
            throw new IllegalArgumentException("customerId is too long - max length is 10");
        }
        customerId = value;
     }

    // sets number [number]
    public void setNumber(Integer value)
     {
        number = value;
     }

    // gets type from server
    public String getTypeFromServer()
     {
        String key = Fields.TYPE;
        return response.getValue(key);
     }

    // gets line 1 [line1] from server
    public String getLine1FromServer()
     {
        String key = Fields.LINE_1;
        return response.getValue(key);
     }

    // gets line 2 [line2] from server
    public String getLine2FromServer()
     {
        String key = Fields.LINE_2;
        return response.getValue(key);
     }

    // gets suburb from server
    public String getSuburbFromServer()
     {
        String key = Fields.SUBURB;
        return response.getValue(key);
     }

    // gets city from server
    public String getCityFromServer()
     {
        String key = Fields.CITY;
        return response.getValue(key);
     }

    // gets post code [postCode] from server
    public String getPostCodeFromServer()
     {
        String key = Fields.POST_CODE;
        return response.getValue(key);
     }

    // gets country from server
    public String getCountryFromServer()
     {
        String key = Fields.COUNTRY;
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

    // gets is mailing [flags] from server
    public Boolean getIsMailingFromServer()
     {
        String key = Fields.IS_MAILING;
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
