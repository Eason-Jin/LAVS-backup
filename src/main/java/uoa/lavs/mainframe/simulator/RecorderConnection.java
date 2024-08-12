package uoa.lavs.mainframe.simulator;

import uoa.lavs.mainframe.Connection;
import uoa.lavs.mainframe.Request;
import uoa.lavs.mainframe.Response;
import uoa.lavs.mainframe.Status;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;

// this is a connection that will record any messages so that they can be modified and replayed later
public class RecorderConnection implements Connection {

    private final BufferedWriter writer;
    private Long transactionId = 0L;

    public RecorderConnection(String dataPath) {
        try {
            File dataFile = new File(dataPath);
            String parent = dataFile.getParent();
            if (parent != null) Files.createDirectories(Paths.get(parent));
            if (dataFile.exists()) dataFile.delete();
            writer = new BufferedWriter(new FileWriter(dataPath));
        } catch (IOException e) {
            // This is bad - it means we can't create the output directory
            // or the output file! So, we will crash the program instead.
            throw new RuntimeException(e);
        }
    }

    public void close() throws IOException {
        writer.close();
    }

    @Override
    public Response send(Request request) {
        String data = DataParser.convertToData(request, true);
        try {
            writer.write(data);
            writer.newLine();
            writer.flush();
        } catch (IOException e) {
            return new Response(
                    new Status(2010, "cannot write", ++transactionId),
                    new HashMap<>());
        }

        return new Response(
                new Status(++transactionId),
                new HashMap<>());
    }
}
