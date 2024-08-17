package uoa.lavs.mainframe.simulator;

public interface IntermittentFailurePolicy {
    boolean canSend(boolean checkOnly);
}
