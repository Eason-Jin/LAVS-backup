package uoa.lavs.mainframe.simulator;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import uoa.lavs.mainframe.ConnectionWithState;
import uoa.lavs.mainframe.simulator.http.Constants;

import static org.junit.jupiter.api.Assertions.assertTrue;

@EnabledIfEnvironmentVariable(named = "LAVS_ENVIRONMENT", matches = "http_test")
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