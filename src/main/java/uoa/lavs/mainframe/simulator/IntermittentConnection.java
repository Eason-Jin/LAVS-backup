package uoa.lavs.mainframe.simulator;

import uoa.lavs.mainframe.Connection;
import uoa.lavs.mainframe.ConnectionWithState;
import uoa.lavs.mainframe.Request;
import uoa.lavs.mainframe.Response;
import uoa.lavs.mainframe.simulator.failures.RandomPolicy;

import java.io.IOException;

import static uoa.lavs.mainframe.MessageErrorStatus.NETWORK_FAILURE_UNAVAILABLE;

public class IntermittentConnection implements ConnectionWithState {

    private final Connection conn;
    private final IntermittentFailurePolicy policy;
    private long transactionId = 0;

    public IntermittentConnection(Connection conn) {
        this.conn = conn;
        policy = new RandomPolicy();
    }

    public IntermittentConnection(Connection conn, IntermittentFailurePolicy policy) {
        this.conn = conn;
        this.policy = policy;
    }

    @Override
    public Response send(Request request) {
        if (!policy.canSend(false)) {
            return NETWORK_FAILURE_UNAVAILABLE.generateEmptyResponse(++transactionId);
        }

        Response response = conn.send(request);
        response.getStatus().setTransactionId(transactionId);
        return response;
    }

    @Override
    public void close() throws IOException {
        conn.close();
    }

    @Override
    public boolean isConnected() {
        if (!policy.canSend(true)) return false;
        if (conn instanceof ConnectionWithState) {
            return ((ConnectionWithState) conn).isConnected();
        }

        return true;
    }
}
