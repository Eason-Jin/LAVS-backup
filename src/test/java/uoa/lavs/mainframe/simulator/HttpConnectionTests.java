package uoa.lavs.mainframe.simulator;

import org.junit.jupiter.api.Test;
import uoa.lavs.mainframe.ConnectionWithState;
import uoa.lavs.mainframe.simulator.http.Constants;

import static org.junit.jupiter.api.Assertions.assertTrue;

class HttpConnectionTests {
    @Test
    public void handlesIsConnected() {
        // arrange
        ConnectionWithState connection = new HttpConnection(Constants.BASE_URL);

        // act
        boolean isConnected = connection.isConnected();

        // assert
        assertTrue(isConnected);
    }
}