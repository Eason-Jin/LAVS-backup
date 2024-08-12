package uoa.lavs.mainframe.messages.customer;

import uoa.lavs.mainframe.*;

public class UpdateCustomerEmployer implements Message, MessageDescription, UpdateCustomerChildMessage {
    public static final int REQUEST_CODE = 1205;
    private Response response;
    private String customerId;
    private Integer number;
    private String name;
    private String line1;
    private String line2;
    private String suburb;
    private String city;
    private String postCode;
    private String country;
    private String phoneNumber;
    private String emailAddress;
    private String website;
    private Integer flags = 0;

    public class Fields {
        public static final String[] INPUT = {"city", "country", "email", "flags", "id", "line1", "line2", "name", "number", "phone", "postCode", "suburb", "web"};
        public static final String[] OUTPUT = {"city", "country", "email", "flags", "line1", "line2", "name", "number", "phone", "postCode", "suburb", "web"};

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
        if (customerId != null) request.setValue(Fields.CUSTOMER_ID, customerId);
        if (number != null) request.setValue(Fields.NUMBER, number.toString());
        if (name != null) request.setValue(Fields.NAME, name);
        if (line1 != null) request.setValue(Fields.LINE_1, line1);
        if (line2 != null) request.setValue(Fields.LINE_2, line2);
        if (suburb != null) request.setValue(Fields.SUBURB, suburb);
        if (city != null) request.setValue(Fields.CITY, city);
        if (postCode != null) request.setValue(Fields.POST_CODE, postCode);
        if (country != null) request.setValue(Fields.COUNTRY, country);
        if (phoneNumber != null) request.setValue(Fields.PHONE_NUMBER, phoneNumber);
        if (emailAddress != null) request.setValue(Fields.EMAIL_ADDRESS, emailAddress);
        if (website != null) request.setValue(Fields.WEBSITE, website);
        if (flags != null) request.setValue(Fields.IS_OWNER, flags.toString());
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

    // sets name [name]
    public void setName(String value)
        throws IllegalArgumentException
     {
        if (value != null && value.length() > 60) {
            throw new IllegalArgumentException("name is too long - max length is 60");
        }
        name = value;
     }

    // sets line 1 [line1]
    public void setLine1(String value)
        throws IllegalArgumentException
     {
        if (value != null && value.length() > 60) {
            throw new IllegalArgumentException("line1 is too long - max length is 60");
        }
        line1 = value;
     }

    // sets line 2 [line2]
    public void setLine2(String value)
        throws IllegalArgumentException
     {
        if (value != null && value.length() > 60) {
            throw new IllegalArgumentException("line2 is too long - max length is 60");
        }
        line2 = value;
     }

    // sets suburb [suburb]
    public void setSuburb(String value)
        throws IllegalArgumentException
     {
        if (value != null && value.length() > 30) {
            throw new IllegalArgumentException("suburb is too long - max length is 30");
        }
        suburb = value;
     }

    // sets city [city]
    public void setCity(String value)
        throws IllegalArgumentException
     {
        if (value != null && value.length() > 30) {
            throw new IllegalArgumentException("city is too long - max length is 30");
        }
        city = value;
     }

    // sets post code [postCode]
    public void setPostCode(String value)
        throws IllegalArgumentException
     {
        if (value != null && value.length() > 10) {
            throw new IllegalArgumentException("postCode is too long - max length is 10");
        }
        postCode = value;
     }

    // sets country [country]
    public void setCountry(String value)
        throws IllegalArgumentException
     {
        if (value != null && value.length() > 30) {
            throw new IllegalArgumentException("country is too long - max length is 30");
        }
        country = value;
     }

    // sets phone number [phoneNumber]
    public void setPhoneNumber(String value)
        throws IllegalArgumentException
     {
        if (value != null && value.length() > 30) {
            throw new IllegalArgumentException("phoneNumber is too long - max length is 30");
        }
        phoneNumber = value;
     }

    // sets email address [emailAddress]
    public void setEmailAddress(String value)
        throws IllegalArgumentException
     {
        if (value != null && value.length() > 60) {
            throw new IllegalArgumentException("emailAddress is too long - max length is 60");
        }
        emailAddress = value;
     }

    // sets website [website]
    public void setWebsite(String value)
        throws IllegalArgumentException
     {
        if (value != null && value.length() > 60) {
            throw new IllegalArgumentException("website is too long - max length is 60");
        }
        website = value;
     }

    // sets is owner [IsOwner]
    public void setIsOwner(Boolean value)
     {
        flags &= 254;
        if (value) flags |= 1;
     }

    // gets number from server
    public Integer getNumberFromServer()
     {
        String key = Fields.NUMBER;
        String value = response.getValue(key);
        if (value == null) return null;
        return Integer.parseInt(value);
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
