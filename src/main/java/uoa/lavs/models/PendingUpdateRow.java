package uoa.lavs.models;

public class PendingUpdateRow {

    private final String customerId;
    private final String customerName;
    private final Boolean generalDetails;
    private final Boolean address;
    private final Boolean email;
    private final Boolean phoneNumber;
    private final Boolean employer;
    private final Boolean loan;
    private final Boolean coborrower;

    public PendingUpdateRow(String customerId, String customerName, Boolean generalDetails, Boolean address, Boolean email, Boolean phoneNumber, Boolean employer, Boolean loan, Boolean coborrower) {
        this.customerId = customerId;
        this.customerName = customerName;
        this.generalDetails = generalDetails;
        this.address = address;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.employer = employer;
        this.loan = loan;
        this.coborrower = coborrower;
    }

    public String getCustomerId() {
        return customerId;
    }

    public String getCustomerName() {
        return customerName;
    }

    public Boolean getGeneralDetails() {
        return generalDetails;
    }

    public Boolean getAddress() {
        return address;
    }

    public Boolean getEmail() {
        return email;
    }

    public Boolean getPhoneNumber() {
        return phoneNumber;
    }

    public Boolean getEmployer() {
        return employer;
    }

    public Boolean getLoan() {
        return loan;
    }

    public Boolean getCoborrower() {
        return coborrower;
    }
}
