package uoa.lavs.mainframe.messages.customer;

import uoa.lavs.mainframe.*;

public class UpdateCustomerAddress implements Message, MessageDescription {
    public static final int REQUEST_CODE = 1202;
    public static final String[] INPUT = {"id", "number", "type", "line1", "line2", "suburb", "city", "postCode", "country", "flags", "flags"};
    public static final String[] OUTPUT = {"type", "line1", "line2", "suburb", "city", "postCode", "country", "flags", "flags"};

    private Response response;
    private String customerId;
    private Integer number;
    private String type;
    private String line1;
    private String line2;
    private String suburb;
    private String city;
    private String postCode;
    private String country;
    private Integer flags = 0;

    @Override
    public Status send(Connection connection) {
        Request request = new Request(REQUEST_CODE);
        if (customerId != null) request.setValue("id", customerId.toString());
        if (number != null) request.setValue("number", number.toString());
        if (type != null) request.setValue("type", type.toString());
        if (line1 != null) request.setValue("line1", line1.toString());
        if (line2 != null) request.setValue("line2", line2.toString());
        if (suburb != null) request.setValue("suburb", suburb.toString());
        if (city != null) request.setValue("city", city.toString());
        if (postCode != null) request.setValue("postCode", postCode.toString());
        if (country != null) request.setValue("country", country.toString());
        if (flags != null) request.setValue("flags", flags.toString());
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

    // sets type [type]
    public void setType(String value)
        throws IllegalArgumentException
     {
        if (value.length() > 20) {
            throw new IllegalArgumentException("type is too long - max length is 20");
        }
        type = value;
     }

    // sets line 1 [line1]
    public void setLine1(String value)
        throws IllegalArgumentException
     {
        if (value.length() > 60) {
            throw new IllegalArgumentException("line1 is too long - max length is 60");
        }
        line1 = value;
     }

    // sets line 2 [line2]
    public void setLine2(String value)
        throws IllegalArgumentException
     {
        if (value.length() > 60) {
            throw new IllegalArgumentException("line2 is too long - max length is 60");
        }
        line2 = value;
     }

    // sets suburb [suburb]
    public void setSuburb(String value)
        throws IllegalArgumentException
     {
        if (value.length() > 30) {
            throw new IllegalArgumentException("suburb is too long - max length is 30");
        }
        suburb = value;
     }

    // sets city [city]
    public void setCity(String value)
        throws IllegalArgumentException
     {
        if (value.length() > 30) {
            throw new IllegalArgumentException("city is too long - max length is 30");
        }
        city = value;
     }

    // sets post code [postCode]
    public void setPostCode(String value)
        throws IllegalArgumentException
     {
        if (value.length() > 10) {
            throw new IllegalArgumentException("postCode is too long - max length is 10");
        }
        postCode = value;
     }

    // sets country [country]
    public void setCountry(String value)
        throws IllegalArgumentException
     {
        if (value.length() > 30) {
            throw new IllegalArgumentException("country is too long - max length is 30");
        }
        country = value;
     }

    // sets is primary [IsPrimary]
    public void setIsPrimary(Boolean value)
     {
        flags &= 254;
        if (value) flags |= 1;
     }

    // sets is mailing [IsMailing]
    public void setIsMailing(Boolean value)
     {
        flags &= 253;
        if (value) flags |= 2;
     }

    // gets type from server
    public String getTypeFromServer()
     {
        String key = "type";
        return response.getValue(key);
     }

    // gets line 1 [line1] from server
    public String getLine1FromServer()
     {
        String key = "line1";
        return response.getValue(key);
     }

    // gets line 2 [line2] from server
    public String getLine2FromServer()
     {
        String key = "line2";
        return response.getValue(key);
     }

    // gets suburb from server
    public String getSuburbFromServer()
     {
        String key = "suburb";
        return response.getValue(key);
     }

    // gets city from server
    public String getCityFromServer()
     {
        String key = "city";
        return response.getValue(key);
     }

    // gets post code [postCode] from server
    public String getPostCodeFromServer()
     {
        String key = "postCode";
        return response.getValue(key);
     }

    // gets country from server
    public String getCountryFromServer()
     {
        String key = "country";
        return response.getValue(key);
     }

    // gets is primary [flags] from server
    public Boolean getIsPrimaryFromServer()
     {
        String key = "flags";
        String value = response.getValue(key);
        int flags = Integer.parseInt(value);
        return (flags & 1) == 1;
     }

    // gets is mailing [flags] from server
    public Boolean getIsMailingFromServer()
     {
        String key = "flags";
        String value = response.getValue(key);
        int flags = Integer.parseInt(value);
        return (flags & 2) == 2;
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
