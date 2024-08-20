package uoa.lavs.mainframe.simulator;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import uoa.lavs.mainframe.messages.customer.FindCustomer;
import uoa.lavs.mainframe.messages.customer.LoadCustomer;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;

// WARNING: Some of these tests are fragile as they test against hard-coded strings. If the underlying string
// generation changes, then some of these tests will fail.
@Disabled
class RecorderConnectionTests {
    @Test
    public void sendStoresTransaction() throws IOException {
        // Arrange
        String dataFile = "recorder-data.1.txt";
        RecorderConnection connection = new RecorderConnection(dataFile);
        FindCustomer message = new FindCustomer();
        message.setCustomerId("123456-789");

// Act
        message.send(connection);
        connection.close();

        // Assert
        String actual = Files.readString(Path.of(dataFile)).trim();
        String expected = "code=\"1001\",error=\"0\",msg=,in-id=\"123456-789\"," +
                "out-[01].dob=\"\",out-[01].id=\"\",out-[01].name=\"\"," +
                "out-[02].dob=\"\",out-[02].id=\"\",out-[02].name=\"\"," +
                "out-[03].dob=\"\",out-[03].id=\"\",out-[03].name=\"\"," +
                "out-[04].dob=\"\",out-[04].id=\"\",out-[04].name=\"\"," +
                "out-[05].dob=\"\",out-[05].id=\"\",out-[05].name=\"\",out-count=\"\"";
        assertEquals(expected, actual);
    }

    @Test
    public void handlesDirectory() throws IOException {
        // Arrange
        String dataFile = "testing/recorder-data.3.txt";
        RecorderConnection connection = new RecorderConnection(dataFile);

        // Act
        connection.close();

        // Assert
        String actual = Files.readString(Path.of(dataFile)).trim();
        String expected = "";
        assertEquals(expected, actual);
    }

    @Test
    public void handlesExistingFile() throws IOException {
        // Arrange
        String dataFile = "recorder-data.4.txt";
        Path path = Path.of(dataFile);
        Files.writeString(path, "some-data");
        RecorderConnection connection = new RecorderConnection(dataFile);

        // Act
        connection.close();

        // Assert
        String actual = Files.readString(path).trim();
        String expected = "";
        assertEquals(expected, actual);
    }

    @Test
    public void handlesMissingFile() throws IOException {
        // Arrange
        String dataFile = "recorder-data.5.txt";
        Path path = Path.of(dataFile);
        if (Files.exists(path)) Files.delete(path);
        RecorderConnection connection = new RecorderConnection(dataFile);

        // Act
        connection.close();

        // Assert
        String actual = Files.readString(path).trim();
        String expected = "";
        assertEquals(expected, actual);
    }

    @Test
    public void sendStoresMultipleTransactions() throws IOException {
        // Arrange
        String dataFile = "recorder-data.2.txt";
        RecorderConnection connection = new RecorderConnection(dataFile);
        FindCustomer message1 = new FindCustomer();
        message1.setCustomerId("123456-789");
        LoadCustomer message2 = new LoadCustomer();
        message1.setCustomerId("123456-789");

        // Act
        message1.send(connection);
        message2.send(connection);
        connection.close();

        // Assert
        String actual = Files.readString(Path.of(dataFile)).trim();
        String expected = "code=\"1001\",error=\"0\",msg=,in-id=\"123456-789\","
                + "out-[01].dob=\"\",out-[01].id=\"\",out-[01].name=\"\","
                + "out-[02].dob=\"\",out-[02].id=\"\",out-[02].name=\"\","
                + "out-[03].dob=\"\",out-[03].id=\"\",out-[03].name=\"\","
                + "out-[04].dob=\"\",out-[04].id=\"\",out-[04].name=\"\","
                + "out-[05].dob=\"\",out-[05].id=\"\",out-[05].name=\"\",out-count=\"\""
                + System.lineSeparator()
                + "code=\"1101\",error=\"0\",msg=,in-id=,out-citizenship=\"\",out-dob=\"\",out-name=\"\",out-occupation=\"\",out-status=\"\",out-title=\"\",out-visa=\"\"";
        assertEquals(expected, actual);
    }
}