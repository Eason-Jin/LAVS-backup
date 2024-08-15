package uoa.lavs.mainframe.simulator.failures;

import org.junit.jupiter.api.Test;
import uoa.lavs.mainframe.Connection;
import uoa.lavs.mainframe.Response;
import uoa.lavs.mainframe.Status;
import uoa.lavs.mainframe.messages.customer.FindCustomer;
import uoa.lavs.mainframe.simulator.InMemoryConnection;
import uoa.lavs.mainframe.simulator.IntermittentConnection;

import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.assertEquals;

class NFailsPerMRequestsPolicyTests {
    private static void runTest(int numberOfTests, int numberOfRequests, int numberOfFailures, int expectedFailures) {
        // arrange
        Connection conn = new IntermittentConnection(
                new InMemoryConnection(
                        new Response(
                                new Status(1),
                                new HashMap<>()
                        )
                ),
                new NFailsPerMRequestsPolicy(numberOfFailures, numberOfRequests)
        );
        FindCustomer message = new FindCustomer();
        int failureCount = 0;

        // act
        for (int i = 0; i < numberOfTests; i++) {
            Status status = message.send(conn);
            if (!status.getWasSuccessful()) failureCount++;
        }

        // assert
        assertEquals(expectedFailures, failureCount);
    }

    @Test
    public void failsOneEveryThree() {
        runTest(9, 3, 1, 3);
    }

    @Test
    public void failsEveryRequest() {
        // arrange
        runTest(9, 1, 1, 9);
    }
}