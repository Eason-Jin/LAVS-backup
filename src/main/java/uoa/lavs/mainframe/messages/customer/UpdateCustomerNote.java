package uoa.lavs.mainframe.messages.customer;

import uoa.lavs.mainframe.*;

public class UpdateCustomerNote implements Message, MessageDescription, UpdateCustomerChildMessage {
    public static final int REQUEST_CODE = 1206;
    private Response response;
    private String customerId;
    private Integer number;
    private String[] line= new String[20];

    public class Fields {
        public static final String[] INPUT = {"[01].line", "[02].line", "[03].line", "[04].line", "[05].line", "[06].line", "[07].line", "[08].line", "[09].line", "[10].line", "[11].line", "[12].line", "[13].line", "[14].line", "[15].line", "[16].line", "[17].line", "[18].line", "[19].line", "[20].line", "id", "number"};
        public static final String[] OUTPUT = {"[01].line", "[02].line", "[03].line", "[04].line", "[05].line", "[06].line", "[07].line", "[08].line", "[09].line", "[10].line", "[11].line", "[12].line", "[13].line", "[14].line", "[15].line", "[16].line", "[17].line", "[18].line", "[19].line", "[20].line", "lines", "number", "pages"};

        public static final String CUSTOMER_ID = "id";
        public static final String NUMBER = "number";
        public static final String PAGE_COUNT = "pages";
        public static final String LINE_COUNT = "lines";
        public static final String LINE = "[%02d].line";
    }

    @Override
    public Status send(Connection connection) {
        Request request = new Request(REQUEST_CODE);
        if (customerId != null) request.setValue(Fields.CUSTOMER_ID, customerId);
        if (number != null) request.setValue(Fields.NUMBER, number.toString());
        for (int loop = 0; loop < 20; loop++) {
            if (line[loop] != null) {
                request.setValue(
                    String.format(Fields.LINE, loop),
                    line[loop].toString());
            }
        }
        response = connection.send(request);
        return response.getStatus();
    }

    // sets customer id [customerId]
    public void setCustomerId(String value)
        throws IllegalArgumentException
     {
        if (value != null && value.length() > 10) {
            throw new IllegalArgumentException("customerId is too long - max length is 10");
        }
        customerId = value;
     }

    // sets number [number]
    public void setNumber(Integer value)
     {
        number = value;
     }

    // sets line [line]
    public void setLine(Integer row, String value)
     {
        line[row] = value;
     }

    // gets number from server
    public Integer getNumberFromServer()
     {
        String key = Fields.NUMBER;
        String value = response.getValue(key);
        if (value == null) return null;
        return Integer.parseInt(value);
     }

    // gets page count [pages] from server
    public Integer getPageCountFromServer()
     {
        String key = Fields.PAGE_COUNT;
        String value = response.getValue(key);
        if (value == null) return null;
        return Integer.parseInt(value);
     }

    // gets line count [lines] from server
    public Integer getLineCountFromServer()
     {
        String key = Fields.LINE_COUNT;
        String value = response.getValue(key);
        if (value == null) return null;
        return Integer.parseInt(value);
     }

    // gets line from server
    public String getLineFromServer(Integer row)
     {
        String key = String.format(Fields.LINE, row);
        return response.getValue(key);
     }

    @Override
    public String[] getInputFields()
    {
        return Fields.INPUT;
    }

    @Override
    public String[] getOutputFields()
    {
        return Fields.OUTPUT;
    }
}
