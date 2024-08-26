package uoa.lavs.models;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import uoa.lavs.mainframe.LoanStatus;

public class LoanTests {

    @Test
    public void getPrincipalString() {
        Loan loan = new Loan();
        loan.setPrincipal(1234.56);
        String expected = "$1234.56";
        String actual = loan.getPrincipalString();
        assertEquals(expected, actual);
    }

    @Test
    public void getPaymentAmountString() {
        Loan loan = new Loan();
        loan.setPaymentAmount(1234.56);
        String expected = "$1234.56";
        String actual = loan.getPaymentAmountString();
        assertEquals(expected, actual);
    }

    @Test
    public void getStatus() {
        Loan loan = new Loan();

        loan.setStatus("1");
        assertEquals(LoanStatus.New.toString(), loan.getStatus());

        loan.setStatus("2");
        assertEquals(LoanStatus.Pending.toString(), loan.getStatus());

        loan.setStatus("5");
        assertEquals(LoanStatus.Active.toString(), loan.getStatus());

        loan.setStatus("8");
        assertEquals(LoanStatus.Cancelled.toString(), loan.getStatus());

        loan.setStatus("10");
        assertEquals("Unknown", loan.getStatus());
    }
}