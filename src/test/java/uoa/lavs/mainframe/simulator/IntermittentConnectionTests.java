package uoa.lavs.mainframe.simulator;

import org.junit.jupiter.api.Test;
import uoa.lavs.mainframe.Connection;
import uoa.lavs.mainframe.Response;
import uoa.lavs.mainframe.Status;
import uoa.lavs.mainframe.messages.customer.FindCustomer;

import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.assertTrue;

class IntermittentConnectionTests {
    @Test
    public void sendOccasionallyFails() {
        // arrange
        Connection conn = new IntermittentConnection(
                new InMemoryConnection(
                        new Response(
                                new Status(1),
                                new HashMap<>()
                        )
                )
        );
        FindCustomer message = new FindCustomer();

        // act - run multiple times as we have no guarantee of when it will
        // fail
        boolean failed = false;
        for (int loop = 0; loop < 100 && !failed; loop++) {
            Status status = message.send(conn);
            failed = !status.getWasSuccessful();
        }

        // assert
        assertTrue(failed);
    }
}