package uoa.lavs.mainframe.simulator;

import uoa.lavs.mainframe.Connection;
import uoa.lavs.mainframe.Request;
import uoa.lavs.mainframe.Response;
import uoa.lavs.mainframe.Status;

import java.io.*;
import java.util.HashMap;

// this is a simple replay connection that will replay transactions in the order that they were received
public class SimpleReplayConnection implements Connection {

    private BufferedReader inputReader = null;
    private Long transactionId = 0L;

    public SimpleReplayConnection(String dataPath) {
        try {
            inputReader = new BufferedReader(new FileReader(dataPath));
        } catch (FileNotFoundException e) {
            // don't care - probably means that the file does not exist, although it does mean that all replay
            // attempts will fail
        }
    }

    public void close() throws IOException {
        if (inputReader != null) inputReader.close();
    }

    // helper method for generating an error response
    private Response generateErrorResponse(int errorCode) {
        return new Response(
                new Status(errorCode, "Transaction failed", ++transactionId),
                new HashMap<>());
    }

    @Override
    public Response send(Request request) {
        // cannot read if we don't have an input stream
        if (inputReader == null) {
            return generateErrorResponse(1000);
        }

        // attempt to read the next line, if there is no more data then we have an error
        String line;
        try {
            line = inputReader.readLine();
        } catch (IOException e) {
            return generateErrorResponse(1010);
        }
        if (line == null){
            return generateErrorResponse(1020);
        }

        // parse the data and return it as a Response - don't have to do much here as DataParser does all the
        // heavy lifting for us
        return DataParser.convertResponseFromData(line, ++transactionId);
    }
}
