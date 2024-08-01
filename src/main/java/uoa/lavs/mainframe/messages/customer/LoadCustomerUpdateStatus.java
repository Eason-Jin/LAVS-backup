package uoa.lavs.mainframe.messages.customer;

import uoa.lavs.mainframe.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class LoadCustomerUpdateStatus implements Message, MessageDescription {
    public static final int REQUEST_CODE = 1119;
    private Response response;
    private String customerId;

    public class Fields {
        public static final String[] INPUT = {"id"};
        public static final String[] OUTPUT = {"address", "details", "email", "phone"};

        public static final String CUSTOMER_ID = "id";
        public static final String LAST_DETAILS_CHANGE = "details";
        public static final String LAST_ADDRESS_CHANGE = "address";
        public static final String LAST_EMAIL_CHANGE = "email";
        public static final String LAST_PHONE_NUMBER_CHANGE = "phone";
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

    // gets last details change [details] from server
    public LocalDate getLastDetailsChangeFromServer()
     {
        String key = Fields.LAST_DETAILS_CHANGE;
        String value = response.getValue(key);
        if (value == null) return null;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        return LocalDate.parse(value, formatter);
     }

    // gets last address change [address] from server
    public LocalDate getLastAddressChangeFromServer()
     {
        String key = Fields.LAST_ADDRESS_CHANGE;
        String value = response.getValue(key);
        if (value == null) return null;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        return LocalDate.parse(value, formatter);
     }

    // gets last email change [email] from server
    public LocalDate getLastEmailChangeFromServer()
     {
        String key = Fields.LAST_EMAIL_CHANGE;
        String value = response.getValue(key);
        if (value == null) return null;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        return LocalDate.parse(value, formatter);
     }

    // gets last phone number change [phone] from server
    public LocalDate getLastPhoneNumberChangeFromServer()
     {
        String key = Fields.LAST_PHONE_NUMBER_CHANGE;
        String value = response.getValue(key);
        if (value == null) return null;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        return LocalDate.parse(value, formatter);
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
