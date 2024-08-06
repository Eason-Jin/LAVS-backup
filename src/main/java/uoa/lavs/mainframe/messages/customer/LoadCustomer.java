package uoa.lavs.mainframe.messages.customer;

import uoa.lavs.mainframe.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class LoadCustomer implements Message, MessageDescription {
    public static final int REQUEST_CODE = 1101;
    private Response response;
    private String customerId;

    public class Fields {
        public static final String[] INPUT = {"id"};
        public static final String[] OUTPUT = {"citizenship", "dob", "name", "occupation", "title", "visa"};

        public static final String CUSTOMER_ID = "id";
        public static final String TITLE = "title";
        public static final String NAME = "name";
        public static final String DATE_OF_BIRTH = "dob";
        public static final String OCCUPATION = "occupation";
        public static final String CITIZENSHIP = "citizenship";
        public static final String VISA = "visa";
    }

    @Override
    public Status send(Connection connection) {
        Request request = new Request(REQUEST_CODE);
        if (customerId != null) request.setValue(Fields.CUSTOMER_ID, customerId.toString());
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

    // gets title from server
    public String getTitleFromServer()
     {
        String key = Fields.TITLE;
        return response.getValue(key);
     }

    // gets name from server
    public String getNameFromServer()
     {
        String key = Fields.NAME;
        return response.getValue(key);
     }

    // gets date of birth [dob] from server
    public LocalDate getDateofBirthFromServer()
     {
        String key = Fields.DATE_OF_BIRTH;
        String value = response.getValue(key);
        if (value == null) return null;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        return LocalDate.parse(value, formatter);
     }

    // gets occupation from server
    public String getOccupationFromServer()
     {
        String key = Fields.OCCUPATION;
        return response.getValue(key);
     }

    // gets citizenship from server
    public String getCitizenshipFromServer()
     {
        String key = Fields.CITIZENSHIP;
        return response.getValue(key);
     }

    // gets visa from server
    public String getVisaFromServer()
     {
        String key = Fields.VISA;
        return response.getValue(key);
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
