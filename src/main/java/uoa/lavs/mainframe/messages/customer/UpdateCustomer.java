package uoa.lavs.mainframe.messages.customer;

import uoa.lavs.mainframe.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class UpdateCustomer implements Message, MessageDescription {
    public static final int REQUEST_CODE = 1201;
    public static final String[] INPUT = {"id", "title", "name", "dob", "occupation", "citizenship", "visa"};
    public static final String[] OUTPUT = {"id", "title", "name", "dob", "occupation", "citizenship", "visa"};

    private Response response;
    private String customerId;
    private String title;
    private String name;
    private LocalDate dateOfBirth;
    private String occupation;
    private String citizenship;
    private String visa;

    @Override
    public Status send(Connection connection) {
        Request request = new Request(REQUEST_CODE);
        if (customerId != null) request.setValue("id", customerId.toString());
        if (title != null) request.setValue("title", title.toString());
        if (name != null) request.setValue("name", name.toString());
        if (dateOfBirth != null) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
            request.setValue("dob", formatter.format(dateOfBirth));
        }
        if (occupation != null) request.setValue("occupation", occupation.toString());
        if (citizenship != null) request.setValue("citizenship", citizenship.toString());
        if (visa != null) request.setValue("visa", visa.toString());
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

    // sets title [title]
    public void setTitle(String value)
        throws IllegalArgumentException
     {
        if (value.length() > 10) {
            throw new IllegalArgumentException("title is too long - max length is 10");
        }
        title = value;
     }

    // sets name [name]
    public void setName(String value)
        throws IllegalArgumentException
     {
        if (value.length() > 60) {
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
        if (value.length() > 40) {
            throw new IllegalArgumentException("occupation is too long - max length is 40");
        }
        occupation = value;
     }

    // sets citizenship [citizenship]
    public void setCitizenship(String value)
        throws IllegalArgumentException
     {
        if (value.length() > 40) {
            throw new IllegalArgumentException("citizenship is too long - max length is 40");
        }
        citizenship = value;
     }

    // sets visa [visa]
    public void setVisa(String value)
        throws IllegalArgumentException
     {
        if (value.length() > 40) {
            throw new IllegalArgumentException("visa is too long - max length is 40");
        }
        visa = value;
     }

    // gets customer id [id] from server
    public String getCustomerIdFromServer()
     {
        String key = "id";
        return response.getValue(key);
     }

    // gets title from server
    public String getTitleFromServer()
     {
        String key = "title";
        return response.getValue(key);
     }

    // gets name from server
    public String getNameFromServer()
     {
        String key = "name";
        return response.getValue(key);
     }

    // gets date of birth [dob] from server
    public LocalDate getDateofBirthFromServer()
     {
        String key = "dob";
        String value = response.getValue(key);
        if (value == null) return null;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        return LocalDate.parse(value, formatter);
     }

    // gets occupation from server
    public String getOccupationFromServer()
     {
        String key = "occupation";
        return response.getValue(key);
     }

    // gets citizenship from server
    public String getCitizenshipFromServer()
     {
        String key = "citizenship";
        return response.getValue(key);
     }

    // gets visa from server
    public String getVisaFromServer()
     {
        String key = "visa";
        return response.getValue(key);
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
