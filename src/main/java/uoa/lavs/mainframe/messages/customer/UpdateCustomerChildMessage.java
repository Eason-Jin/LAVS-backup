package uoa.lavs.mainframe.messages.customer;

import uoa.lavs.mainframe.Message;

public interface UpdateCustomerChildMessage extends Message {
    Integer getNumberFromServer();

    void setCustomerId(String value) throws IllegalArgumentException;

    void setNumber(Integer value);
}
