package uoa.lavs.mainframe.simulator.nitrite;

import org.junit.jupiter.api.Test;
import uoa.lavs.mainframe.Connection;
import uoa.lavs.mainframe.Status;
import uoa.lavs.mainframe.messages.loan.LoadLoanCoborrowers;
import uoa.lavs.mainframe.simulator.NitriteConnection;

import static org.junit.jupiter.api.Assertions.*;
import static uoa.lavs.mainframe.MessageErrorStatus.LOAN_NOT_FOUND;

class LoadLoanCoborrowersTests {
    @Test
    public void handlesMissingLoan() {
        // Arrange
        Connection connection = new NitriteConnection();
        LoadLoanCoborrowers message = new LoadLoanCoborrowers();
        message.setLoanId("123-09");

        // Act
        Status status = message.send(connection);

        // Assert
        assertAll(
                () -> assertEquals(LOAN_NOT_FOUND.getCode(), status.getErrorCode()),
                () -> assertEquals(LOAN_NOT_FOUND.getMessage(), status.getErrorMessage())
        );
    }

    @Test
    public void handlesExistingLoan() {
        // Arrange
        Connection connection = new NitriteConnection(
                DatabaseHelper.generateDefaultDatabase());
        LoadLoanCoborrowers message = new LoadLoanCoborrowers();
        message.setLoanId("123-09");

        // Act
        Status status = message.send(connection);

        // Assert
        assertAll(
                () -> assertTrue(status.getWasSuccessful()),
                () -> assertEquals("123", message.getCustomerIdFromServer()),
                () -> assertEquals("John Doe", message.getCustomerNameFromServer()),
                () -> assertEquals(1, message.getPageCountFromServer()),
                () -> assertEquals(1, message.getCountFromServer()),
                () -> assertEquals("456", message.getCoborrowerIdFromServer(1)),
                () -> assertEquals(1, message.getCoborrowerNumberFromServer(1)),
                () -> assertEquals("John Doe", message.getCoborrowerNameFromServer(1))
        );
    }
}