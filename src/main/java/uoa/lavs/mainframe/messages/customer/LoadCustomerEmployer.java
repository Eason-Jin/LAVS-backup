package uoa.lavs.mainframe.messages.customer;

import uoa.lavs.mainframe.*;

public class LoadCustomerEmployer implements Message, MessageDescription {
    public static final int REQUEST_CODE = 1105;
    private Response response;
    private String customerId;
    private Integer number;

    public class Fields {
        public static final String[] INPUT = {"id", "number"};
        public static final String[] OUTPUT = {"city", "country", "email", "flags", "line1", "line2", "name", "phone", "postCode", "suburb", "web"};

        public static final String CUSTOMER_ID = "id";
        public static final String NUMBER = "number";
        public static final String NAME = "name";
        public static final String LINE_1 = "line1";
        public static final String LINE_2 = "line2";
        public static final String SUBURB = "suburb";
        public static final String CITY = "city";
        public static final String POST_CODE = "postCode";
        public static final String COUNTRY = "country";
        public static final String PHONE_NUMBER = "phone";
        public static final String EMAIL_ADDRESS = "email";
        public static final String WEBSITE = "web";
        public static final String IS_OWNER = "flags";
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

    // gets name from server
    public String getNameFromServer()
     {
        String key = Fields.NAME;
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

    // gets phone number [phone] from server
    public String getPhoneNumberFromServer()
     {
        String key = Fields.PHONE_NUMBER;
        return response.getValue(key);
     }

    // gets email address [email] from server
    public String getEmailAddressFromServer()
     {
        String key = Fields.EMAIL_ADDRESS;
        return response.getValue(key);
     }

    // gets website [web] from server
    public String getWebsiteFromServer()
     {
        String key = Fields.WEBSITE;
        return response.getValue(key);
     }

    // gets is owner [flags] from server
    public Boolean getIsOwnerFromServer()
     {
        String key = Fields.IS_OWNER;
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
