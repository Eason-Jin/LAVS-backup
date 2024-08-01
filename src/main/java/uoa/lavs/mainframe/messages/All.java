package uoa.lavs.mainframe.messages;

import uoa.lavs.mainframe.MessageDescription;
import uoa.lavs.mainframe.messages.customer.*;
import uoa.lavs.mainframe.messages.loan.*;

public class All {
    public static final Integer FindCustomer = 1001;
    public static final Integer LoadCustomerUpdateStatus = 1119;
    public static final Integer LoadCustomer = 1101;
    public static final Integer UpdateCustomer = 1201;
    public static final Integer LoadCustomerAddress = 1102;
    public static final Integer FindCustomerAddress = 1002;
    public static final Integer UpdateCustomerAddress = 1202;
    public static final Integer LoadCustomerEmployer = 1105;
    public static final Integer UpdateCustomerEmployer = 1205;
    public static final Integer LoadCustomerNote = 1106;
    public static final Integer LoadCustomerEmail = 1104;
    public static final Integer FindCustomerEmail = 1004;
    public static final Integer UpdateCustomerEmail = 1204;
    public static final Integer LoadCustomerPhoneNumber = 1103;
    public static final Integer FindCustomerPhoneNumber = 1003;
    public static final Integer UpdateCustomerPhoneNumber = 1203;
    public static final Integer LoadLoan = 2101;
    public static final Integer UpdateLoan = 2201;
    public static final Integer LoadLoanPayments = 2102;
    public static final Integer LoadLoanSummary = 2103;
    public static final Integer UpdateLoanCoborrower = 2206;
    public static final Integer UpdateLoanStatus = 2207;

    public static MessageDescription getMessageDescription(Integer code) {
        switch (code) {
            case 1001:
                return new FindCustomer();
            case 1119:
                return new LoadCustomerUpdateStatus();
            case 1101:
                return new LoadCustomer();
            case 1201:
                return new UpdateCustomer();
            case 1102:
                return new LoadCustomerAddress();
            case 1002:
                return new FindCustomerAddress();
            case 1202:
                return new UpdateCustomerAddress();
            case 1105:
                return new LoadCustomerEmployer();
            case 1205:
                return new UpdateCustomerEmployer();
            case 1106:
                return new LoadCustomerNote();
            case 1104:
                return new LoadCustomerEmail();
            case 1004:
                return new FindCustomerEmail();
            case 1204:
                return new UpdateCustomerEmail();
            case 1103:
                return new LoadCustomerPhoneNumber();
            case 1003:
                return new FindCustomerPhoneNumber();
            case 1203:
                return new UpdateCustomerPhoneNumber();
            case 2101:
                return new LoadLoan();
            case 2201:
                return new UpdateLoan();
            case 2102:
                return new LoadLoanPayments();
            case 2103:
                return new LoadLoanSummary();
            case 2206:
                return new UpdateLoanCoborrower();
            case 2207:
                return new UpdateLoanStatus();
        }
        return null;
    }
}
