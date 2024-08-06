package uoa.lavs.mainframe.simulator;

import uoa.lavs.mainframe.Request;
import uoa.lavs.mainframe.Response;

public interface MessageProcessor {
    Response process(Request request, long transactionId);
}
