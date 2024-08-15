package uoa.lavs.mainframe.simulator.failures;

import org.junit.jupiter.api.Test;
import uoa.lavs.mainframe.Connection;
import uoa.lavs.mainframe.Response;
import uoa.lavs.mainframe.Status;
import uoa.lavs.mainframe.messages.customer.FindCustomer;
import uoa.lavs.mainframe.simulator.InMemoryConnection;
import uoa.lavs.mainframe.simulator.IntermittentConnection;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;

class FileExistsPolicyTests {
    @Test
    public void handlesFileExists() throws IOException {
        // arrange
        String pathname = "failure-exists.txt";
        File file = new File(pathname);
        file.createNewFile();
        assertTrue(file.exists());
        Connection conn = new IntermittentConnection(
                new InMemoryConnection(
                        new Response(
                                new Status(1),
                                new HashMap<>()
                        )
                ),
                new FileExistsPolicy(pathname)
        );
        FindCustomer message = new FindCustomer();

        // act
        Status status = message.send(conn);

        // assert
        assertFalse(status.getWasSuccessful());
    }

    @Test
    public void conditionChanges() throws IOException {
        // arrange 1
        String pathname = "failure-changes.txt";
        File file = new File(pathname);
        file.delete();
        assertFalse(file.exists());
        Connection conn = new IntermittentConnection(
                new InMemoryConnection(
                        new Response(
                                new Status(1),
                                new HashMap<>()
                        )
                ),
                new FileExistsPolicy(pathname)
        );
        FindCustomer message = new FindCustomer();

        // act 1
        Status missingStatus = message.send(conn);

        // arrange 2
        file.createNewFile();
        assertTrue(file.exists());

        // act
        Status existsStatus = message.send(conn);

        // assert
        assertAll(
                () -> assertTrue(missingStatus.getWasSuccessful()),
                () -> assertFalse(existsStatus.getWasSuccessful())
        );
    }

    @Test
    public void handlesFileDoesNotExist() throws IOException {
        // arrange
        String pathname = "failure-doesnt-exist.txt";
        File file = new File(pathname);
        file.delete();
        assertFalse(file.exists());
        Connection conn = new IntermittentConnection(
                new InMemoryConnection(
                        new Response(
                                new Status(1),
                                new HashMap<>()
                        )
                ),
                new FileExistsPolicy(pathname)
        );
        FindCustomer message = new FindCustomer();

        // act
        Status status = message.send(conn);

        // assert
        assertTrue(status.getWasSuccessful());
    }
}