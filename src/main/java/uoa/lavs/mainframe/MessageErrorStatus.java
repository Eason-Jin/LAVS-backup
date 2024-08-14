package uoa.lavs.mainframe;

import java.util.HashMap;

public enum MessageErrorStatus {
    UNKNOWN_MESSAGE(100, "Unknown Message"),
    INTERNAL_FAILURE(110, "Unknown Message"),
    CUSTOMER_NOT_FOUND(200, "Customer not found"),
    CUSTOMER_ADDRESS_NOT_FOUND(210, "Customer address not found"),
    CUSTOMER_EMAIL_NOT_FOUND(220, "Customer email not found"),
    CUSTOMER_EMPLOYER_NOT_FOUND(230, "Customer employer not found"),
    CUSTOMER_PHONE_NUMBER_NOT_FOUND(240, "Customer phone number not found"),
    CUSTOMER_NOTE_NOT_FOUND(250, "Customer note not found"),

    LOAN_NOT_FOUND(300, "Loan not found"),
    INVALID_COBORROWER_ID(310, "Invalid coborrower"),

    INVALID_REQUEST_NUMBER(400, "Request is invalid - missing number"),
    INVALID_REQUEST_SEARCH(420, "Request is invalid - invalid or missing search criteria"),
    INVALID_REQUEST_CUSTOMER_ID(450, "Request is invalid - invalid or missing customer ID"),
    INVALID_REQUEST_LOAN_ID(460, "Request is invalid - invalid or missing loan ID"),
    INVALID_REQUEST_LOAN_STATUS(470, "Loan status is missing or invalid"),

    NETWORK_FAILURE_UNKNOWN(1000, "Unknown network error"),
    NETWORK_FAILURE_TIMEOUT(1010, "The request has timed out"),
    NETWORK_FAILURE_UNAVAILABLE(1020, "The remote host has responded that it is currently unavailable");

    private final String message;
    private final Integer code;

    MessageErrorStatus(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    public Integer getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public Response generateEmptyResponse(long transactionId) {
        return new Response(
                new Status(code, message, transactionId),
                new HashMap<>());
    }
}
