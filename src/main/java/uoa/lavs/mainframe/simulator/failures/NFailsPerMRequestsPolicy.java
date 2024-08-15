package uoa.lavs.mainframe.simulator.failures;

import uoa.lavs.mainframe.simulator.IntermittentFailurePolicy;

public class NFailsPerMRequestsPolicy implements IntermittentFailurePolicy {

    private final int failureThreshold;
    private final int successThreshold;
    private int requestCount = 0;

    public NFailsPerMRequestsPolicy(int numberOfFailures, int numberOfRequests) {
        failureThreshold = numberOfRequests + numberOfFailures - 1;
        successThreshold = numberOfRequests;
    }

    @Override
    public boolean canSend() {
        if (++requestCount < successThreshold) {
            return true;
        }
        if (requestCount > failureThreshold) {
            requestCount = 1;
        }

        return requestCount < failureThreshold;
    }
}
