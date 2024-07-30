package uoa.lavs.utility;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class AmortizingLoanCalculatorTests {
    @Test
    public void calculateRepayments() {
        // Arrange
        AmortizingLoanCalculator calculator = new AmortizingLoanCalculator();

        // Act
        ArrayList<LoanRepayment> repayments = calculator.generateRepaymentSchedule(
                 450000.00,
                8.15,
                3349.00,
                PaymentFrequency.Monthly,
                LocalDate.of(2024, 8, 22));

        // Assert
        LoanRepayment firstPayment = repayments.get(0);
        LoanRepayment lastPayment = repayments.get(repayments.size() - 1);
        assertAll(
                () -> assertEquals(361, repayments.size()),
                () -> assertEquals(292.75, firstPayment.getPrincipalAmount()),
                () -> assertEquals(3056.25, firstPayment.getInterestAmount()),
                () -> assertEquals(449707.25, firstPayment.getRemainingAmount()),
                () -> assertEquals(0.00, lastPayment.getRemainingAmount()));
    }

    @Test
    public void calculateHandlesWeekly() {
        // Arrange
        AmortizingLoanCalculator calculator = new AmortizingLoanCalculator();

        // Act
        ArrayList<LoanRepayment> repayments = calculator.generateRepaymentSchedule(
                1000.0,
                10.0,
                99.00,
                PaymentFrequency.Weekly,
                LocalDate.of(2024, 8, 22));

        // Assert
        LoanRepayment firstPayment = repayments.get(0);
        LoanRepayment lastPayment = repayments.get(repayments.size() - 1);
        assertAll(
                () -> assertEquals(11, repayments.size()),
                () -> assertEquals(97.08, firstPayment.getPrincipalAmount()),
                () -> assertEquals(1.92, firstPayment.getInterestAmount()),
                () -> assertEquals(902.92, firstPayment.getRemainingAmount()),
                () -> assertEquals(0.00, lastPayment.getRemainingAmount()));
    }

    @Test
    public void calculateHandlesFortnightly() {
        // Arrange
        AmortizingLoanCalculator calculator = new AmortizingLoanCalculator();

        // Act
        ArrayList<LoanRepayment> repayments = calculator.generateRepaymentSchedule(
                1000.0,
                10.0,
                99.00,
                PaymentFrequency.Fortnightly,
                LocalDate.of(2024, 8, 22));

        // Assert
        LoanRepayment firstPayment = repayments.get(0);
        LoanRepayment lastPayment = repayments.get(repayments.size() - 1);
        assertAll(
                () -> assertEquals(11, repayments.size()),
                () -> assertEquals(95.16, firstPayment.getPrincipalAmount()),
                () -> assertEquals(3.84, firstPayment.getInterestAmount()),
                () -> assertEquals(904.84, firstPayment.getRemainingAmount()),
                () -> assertEquals(0.00, lastPayment.getRemainingAmount()));
    }

    @Test
    public void calculateSetsDateAndNumberForMonthly() {
        // Arrange
        AmortizingLoanCalculator calculator = new AmortizingLoanCalculator();

        // Act
        ArrayList<LoanRepayment> repayments = calculator.generateRepaymentSchedule(
                1000.0,
                10.0,
                99.00,
                PaymentFrequency.Monthly,
                LocalDate.of(2024, 8, 22));

        // Assert
        LoanRepayment firstPayment = repayments.get(0);
        LoanRepayment lastPayment = repayments.get(repayments.size() - 1);
        assertAll(
                () -> assertEquals(LocalDate.of(2024, 9, 22), firstPayment.getRepaymentDate()),
                () -> assertEquals(1, firstPayment.getNumber()),
                () -> assertEquals(LocalDate.of(2025, 7, 22), lastPayment.getRepaymentDate()),
                () -> assertEquals(11, lastPayment.getNumber())
        );
    }

    @Test
    public void calculateSetsDateAndNumberForWeekly() {
        // Arrange
        AmortizingLoanCalculator calculator = new AmortizingLoanCalculator();

        // Act
        ArrayList<LoanRepayment> repayments = calculator.generateRepaymentSchedule(
                1000.0,
                10.0,
                99.00,
                PaymentFrequency.Weekly,
                LocalDate.of(2024, 8, 22));

        // Assert
        LoanRepayment firstPayment = repayments.get(0);
        LoanRepayment lastPayment = repayments.get(repayments.size() - 1);
        assertAll(
                () -> assertEquals(LocalDate.of(2024, 8, 29), firstPayment.getRepaymentDate()),
                () -> assertEquals(1, firstPayment.getNumber()),
                () -> assertEquals(LocalDate.of(2024, 11, 7), lastPayment.getRepaymentDate()),
                () -> assertEquals(11, lastPayment.getNumber())
        );
    }

    @Test
    public void calculateSetsDateAndNumberForFortnightly() {
        // Arrange
        AmortizingLoanCalculator calculator = new AmortizingLoanCalculator();

        // Act
        ArrayList<LoanRepayment> repayments = calculator.generateRepaymentSchedule(
                1000.0,
                10.0,
                99.00,
                PaymentFrequency.Fortnightly,
                LocalDate.of(2024, 8, 22));

        // Assert
        LoanRepayment firstPayment = repayments.get(0);
        LoanRepayment lastPayment = repayments.get(repayments.size() - 1);
        assertAll(
                () -> assertEquals(LocalDate.of(2024, 9, 5), firstPayment.getRepaymentDate()),
                () -> assertEquals(1, firstPayment.getNumber()),
                () -> assertEquals(LocalDate.of(2025, 1, 23), lastPayment.getRepaymentDate()),
                () -> assertEquals(11, lastPayment.getNumber())
        );
    }
}