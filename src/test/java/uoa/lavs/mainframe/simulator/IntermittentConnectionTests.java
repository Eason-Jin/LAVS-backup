package uoa.lavs.mainframe.simulator;

import org.junit.jupiter.api.Test;
import uoa.lavs.mainframe.*;
import uoa.lavs.mainframe.messages.customer.FindCustomer;

import java.io.IOException;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;

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

    @Test
    public void isConnectedHandlesConnectionWithState() {
        // arrange
        ConnectionWithStateMock mock = new ConnectionWithStateMock();
        ConnectionWithState conn = new IntermittentConnection(mock);

        // act
        boolean isConnected = conn.isConnected();

        // assert
        assertAll(
                () -> assertFalse(isConnected),
                () -> assertTrue(mock.getWasIsConnectedCalled())
        );
    }

    @Test
    public void isConnectedHandlesPlainConnection() {
        // arrange
        ConnectionWithState conn = new IntermittentConnection(
                new MockConnection()
        );

        // act
        boolean isConnected = conn.isConnected();

        // assert
        assertTrue(isConnected);
    }

    private class ConnectionWithStateMock implements ConnectionWithState {

        private boolean wasIsConnectedCalled;

        @Override
        public boolean isConnected() {
            wasIsConnectedCalled = true;
            return false;
        }

        public boolean getWasIsConnectedCalled() {
            return wasIsConnectedCalled;
        }

        @Override
        public Response send(Request request) {
            return null;
        }

        @Override
        public void close() throws IOException {

        }
    }
}