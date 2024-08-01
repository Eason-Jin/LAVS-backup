package uoa.lavs.mainframe.messages.customer;

import uoa.lavs.mainframe.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class FindCustomer implements Message, MessageDescription {
    public static final int REQUEST_CODE = 1001;
    private Response response;
    private String customerId;

    public class Fields {
        public static final String[] INPUT = {"id"};
        public static final String[] OUTPUT = {"[01].dob", "[01].id", "[01].name", "[02].dob", "[02].id", "[02].name", "[03].dob", "[03].id", "[03].name", "[04].dob", "[04].id", "[04].name", "[05].dob", "[05].id", "[05].name", "count"};

        public static final String CUSTOMER_ID = "id";
        public static final String CUSTOMER_COUNT = "count";
        public static final String NAME = "[%02d].name";
        public static final String DATE_OF_BIRTH = "[%02d].dob";
        public static final String ID = "[%02d].id";
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
        if (value.length() > 10) {
            throw new IllegalArgumentException("customerId is too long - max length is 10");
        }
        customerId = value;
     }

    // gets customer count [count] from server
    public Integer getCustomerCountFromServer()
     {
        String key = Fields.CUSTOMER_COUNT;
        String value = response.getValue(key);
        if (value == null) return null;
        return Integer.parseInt(value);
     }

    // gets name from server
    public String getNameFromServer(Integer row)
     {
        String key = String.format(Fields.NAME, row);
        return response.getValue(key);
     }

    // gets date of birth [dob] from server
    public LocalDate getDateofBirthFromServer(Integer row)
     {
        String key = String.format(Fields.DATE_OF_BIRTH, row);
        String value = response.getValue(key);
        if (value == null) return null;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        return LocalDate.parse(value, formatter);
     }

    // gets id from server
    public String getIdFromServer(Integer row)
     {
        String key = String.format(Fields.ID, row);
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
