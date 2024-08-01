package uoa.lavs.mainframe.simulator;

import org.junit.jupiter.api.Test;
import uoa.lavs.mainframe.Request;
import uoa.lavs.mainframe.Response;
import uoa.lavs.mainframe.messages.All;
import uoa.lavs.mainframe.messages.customer.FindCustomer;

import static org.junit.jupiter.api.Assertions.*;

// WARNING: Some of these tests are fragile as they test against hard-coded strings. If the underlying string
// generation changes, then some of these tests will fail.
class DataParserTests {
    @Test
    public void convertResponseFromData() {
        // Arrange
        String data = "code=\"1001\",error=\"0\",msg=,in-id=\"123456-789\",out-count=\"1\",out-[01].name=\"2\"," +
                "out-[01].dob=\"3\",out-[01].id=\"4\"";

        // Act
        Response response = DataParser.convertResponseFromData(data, 123L);

        // Assert
        assertAll(
                () -> assertEquals(123L, response.getStatus().getTransactionId()),
                () -> assertEquals(0, response.getStatus().getErrorCode()),
                () -> assertNull(response.getStatus().getErrorMessage()),
                () -> assertEquals("1", response.getValue("count")),
                () -> assertEquals("2", response.getValue("[01].name")),
                () -> assertEquals("3", response.getValue("[01].dob")),
                () -> assertEquals("4", response.getValue("[01].id"))
        );
    }

    @Test
    public void convertResponseHandlesNull() {
        // Arrange
        String data = "code=\"1001\",error=\"0\",msg=,in-id=\"123456-789\",out-count=,out-name=,out-dob=,out-id=";

        // Act
        Response response = DataParser.convertResponseFromData(data, 123L);

        // Assert
        assertAll(
                () -> assertEquals(123L, response.getStatus().getTransactionId()),
                () -> assertEquals(0, response.getStatus().getErrorCode()),
                () -> assertNull(response.getStatus().getErrorMessage()),
                () -> assertNull(response.getValue("count")),
                () -> assertNull(response.getValue("name")),
                () -> assertNull(response.getValue("dob")),
                () -> assertNull(response.getValue("id"))
        );
    }

    @Test
    public void convertResponseHandlesEncodedData() {
        // Arrange
        String data = "code=\"1001\",error=\"0\",msg=,in-id=\"123456-789\",out-count=\"\\\\\",out-[01].name=\"\\\"\"," +
                "out-[01].dob=\"\\n\",out-[01].id=\"\\r\"";

        // Act
        Response response = DataParser.convertResponseFromData(data, 123L);

        // Assert
        assertAll(
                () -> assertEquals(123L, response.getStatus().getTransactionId()),
                () -> assertEquals(0, response.getStatus().getErrorCode()),
                () -> assertNull(response.getStatus().getErrorMessage()),
                () -> assertEquals("\\", response.getValue("count")),
                () -> assertEquals("\"", response.getValue("[01].name")),
                () -> assertEquals("\n", response.getValue("[01].dob")),
                () -> assertEquals("\r", response.getValue("[01].id"))
        );
    }

    @Test
    public void convertToDataGeneratesValidString() {
        // Arrange
        Request request = new Request(All.FindCustomer);
        request.setValue("id", "123456-789");

        // Act
        String actual = DataParser.convertToData(request);

        // Assert
        String expected = "code=\"1001\",error=\"0\",msg=,in-id=\"123456-789\"," +
                "out-[01].dob=\"\",out-[01].id=\"\",out-[01].name=\"\"," +
                "out-[02].dob=\"\",out-[02].id=\"\",out-[02].name=\"\"," +
                "out-[03].dob=\"\",out-[03].id=\"\",out-[03].name=\"\"," +
                "out-[04].dob=\"\",out-[04].id=\"\",out-[04].name=\"\"," +
                "out-[05].dob=\"\",out-[05].id=\"\",out-[05].name=\"\",out-count=\"\"";
        assertEquals(expected, actual);
    }

    @Test
    public void convertToDataHandlesNull() {
        // Arrange
        Request request = new Request(All.FindCustomer);

        // Act
        String actual = DataParser.convertToData(request);

        // Assert
        String expected = "code=\"1001\",error=\"0\",msg=,in-id=," +
                "out-[01].dob=\"\",out-[01].id=\"\",out-[01].name=\"\"," +
                "out-[02].dob=\"\",out-[02].id=\"\",out-[02].name=\"\"," +
                "out-[03].dob=\"\",out-[03].id=\"\",out-[03].name=\"\"," +
                "out-[04].dob=\"\",out-[04].id=\"\",out-[04].name=\"\"," +
                "out-[05].dob=\"\",out-[05].id=\"\",out-[05].name=\"\",out-count=\"\"";
        assertEquals(expected, actual);
    }

    @Test
    public void convertToDataEncodesSpecialCharacters() {
        // Arrange
        Request request = new Request(All.FindCustomer);
        request.setValue("id", "quote=\",slash=\\,newline=\r\n");

        // Act
        String actual = DataParser.convertToData(request);

        // Assert
        String expected = "code=\"1001\",error=\"0\",msg=,in-id=\"quote=\\\",slash=\\\\,newline=\\r\\n\"," +
                "out-[01].dob=\"\",out-[01].id=\"\",out-[01].name=\"\"," +
                "out-[02].dob=\"\",out-[02].id=\"\",out-[02].name=\"\"," +
                "out-[03].dob=\"\",out-[03].id=\"\",out-[03].name=\"\"," +
                "out-[04].dob=\"\",out-[04].id=\"\",out-[04].name=\"\"," +
                "out-[05].dob=\"\",out-[05].id=\"\",out-[05].name=\"\",out-count=\"\"";
        assertEquals(expected, actual);
    }

}