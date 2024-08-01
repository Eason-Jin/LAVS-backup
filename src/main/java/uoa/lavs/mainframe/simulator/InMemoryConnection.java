package uoa.lavs.mainframe.simulator;

import uoa.lavs.mainframe.Connection;
import uoa.lavs.mainframe.Request;
import uoa.lavs.mainframe.Response;

import java.io.IOException;

// this is a simple connection stores and returns a single response
public class InMemoryConnection implements Connection {

    private final Response response;

    public InMemoryConnection(Response response) {
        this.response = response;
    }

    public void close() throws IOException {
    }

    @Override
    public Response send(Request request) {
        return response;
    }
}
