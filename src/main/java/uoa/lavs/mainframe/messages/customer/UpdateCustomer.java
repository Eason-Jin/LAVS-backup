package uoa.lavs.mainframe.messages.customer;

import uoa.lavs.mainframe.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class UpdateCustomer implements Message, MessageDescription {
    public static final int REQUEST_CODE = 1201;
    private Response response;
    private String customerId;
    private String title;
    private String name;
    private LocalDate dateOfBirth;
    private String occupation;
    private String citizenship;
    private String visa;

    public class Fields {
        public static final String[] INPUT = {"citizenship", "dob", "id", "name", "occupation", "title", "visa"};
        public static final String[] OUTPUT = {"citizenship", "dob", "id", "name", "occupation", "title", "visa"};

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
        if (title != null) request.setValue(Fields.TITLE, title.toString());
        if (name != null) request.setValue(Fields.NAME, name.toString());
        if (dateOfBirth != null) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
            request.setValue(Fields.DATE_OF_BIRTH, formatter.format(dateOfBirth));
        }
        if (occupation != null) request.setValue(Fields.OCCUPATION, occupation.toString());
        if (citizenship != null) request.setValue(Fields.CITIZENSHIP, citizenship.toString());
        if (visa != null) request.setValue(Fields.VISA, visa.toString());
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

    // sets title [title]
    public void setTitle(String value)
        throws IllegalArgumentException
     {
        if (value != null && value.length() > 10) {
            throw new IllegalArgumentException("title is too long - max length is 10");
        }
        title = value;
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

    // sets date of birth [dateOfBirth]
    public void setDateofBirth(LocalDate value)
     {
        dateOfBirth = value;
     }

    // sets occupation [occupation]
    public void setOccupation(String value)
        throws IllegalArgumentException
     {
        if (value != null && value.length() > 40) {
            throw new IllegalArgumentException("occupation is too long - max length is 40");
        }
        occupation = value;
     }

    // sets citizenship [citizenship]
    public void setCitizenship(String value)
        throws IllegalArgumentException
     {
        if (value != null && value.length() > 40) {
            throw new IllegalArgumentException("citizenship is too long - max length is 40");
        }
        citizenship = value;
     }

    // sets visa [visa]
    public void setVisa(String value)
        throws IllegalArgumentException
     {
        if (value != null && value.length() > 40) {
            throw new IllegalArgumentException("visa is too long - max length is 40");
        }
        visa = value;
     }

    // gets customer id [id] from server
    public String getCustomerIdFromServer()
     {
        String key = Fields.CUSTOMER_ID;
        return response.getValue(key);
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
