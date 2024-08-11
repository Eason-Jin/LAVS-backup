package uoa.lavs.mainframe.simulator;

import uoa.lavs.mainframe.MessageDescription;
import uoa.lavs.mainframe.Request;
import uoa.lavs.mainframe.Response;
import uoa.lavs.mainframe.Status;
import uoa.lavs.mainframe.messages.All;

import java.util.HashMap;

public final class DataParser {

    public static String convertToData(Request request, boolean appendOutput) {
        Integer code = request.getRequestType();
        // retrieve the message description
        MessageDescription description = All.getMessageDescription(code);
        StringBuilder builder = new StringBuilder();

        // add the message and error data - this is required for all messages
        // adding the error data here means they can be manually set
        builder.append("code=\"");
        builder.append(code);
        if (appendOutput) {
            builder.append("\",error=\"0\",msg=");
        } else {
            builder.append("\"");
        }

        // add all the input fields - use the data from the request
        for (String input : description.getInputFields()) {
            if (appendOutput) {
                builder.append(",in-");
            } else {
                builder.append(",");
            }
            builder.append(input);
            String value = request.getValue(input);
            builder.append("=");

            // encode the value in case there is any characters that break the format
            builder.append(encodeValue(value));
        }

        if (appendOutput) {
            // add all the output fields - these will be empty, but it makes it easier to fill them
            // in manually later
            for (String output : description.getOutputFields()) {
                builder.append(",out-");
                builder.append(output);
                builder.append("=\"\"");
            }
        }

        // return the generate data string
        return builder.toString();
    }

    public static Response convertResponseFromData(String value, Long transactionId) {
        if (value == "") return null;
        HashMap<String, String> values = new HashMap<>();
        StringBuilder valueBuilder = new StringBuilder();
        StringBuilder keyBuilder = new StringBuilder();

        // first step is to parse the input string and split it into its separate component
        boolean inQuote = false;
        boolean hasValue = false;
        for (Integer i = 0; i < value.length(); i++) {
            char c = value.charAt(i);
            // first process any value data
            if (!inQuote) {
                switch (c) {
                    case '=':
                        // starting the value building section - we are expecting a quote or a new value,
                        // anything else is an exception
                        if (++i < value.length()) {
                            c = value.charAt(i);
                        } else {
                            // this means we have hit the end of our string, so, we will treat it as an end-of-value
                            // character
                            c = ',';
                        }
                        switch (c) {
                            case '"':
                                inQuote = true;
                                break;

                            case ',':
                                // we will backtrack and handle this elsewhere, we are mainly checking
                                // for a valid character
                                --i;
                                break;

                            default:
                                throw new IllegalArgumentException("Invalid character '" + c + "' at position " + i);
                        }
                        break;

                    case ',':
                        // end of value, store and start on the next key value pair
                        String thisKey = keyBuilder.toString();
                        String thisValue = hasValue ? valueBuilder.toString() : null;
                        values.put(thisKey, thisValue);

                        // reset the variables for the next value
                        keyBuilder = new StringBuilder();
                        valueBuilder = new StringBuilder();
                        hasValue = false;
                        break;

                    default:
                        // otherwise, must be part of the key name
                        keyBuilder.append(c);
                        break;
                }
                continue;
            }

            // handle the end of a quote
            if (c == '"') {
                inQuote = false;
                continue;
            }

            // if we have gotten this far, then we must have a value (or an error)
            hasValue = true;

            // then process any special characters
            if (c == '\\') {
                c = value.charAt(++i);
                switch (c) {
                    case 'n':
                        valueBuilder.append('\n');
                        break;

                    case 'r':
                        valueBuilder.append('\r');
                        break;

                    case '"':
                        valueBuilder.append('"');
                        break;

                    case '\\':
                        valueBuilder.append('\\');
                        break;

                    default:
                        throw new IllegalArgumentException("Invalid character '" + c + "' at position " + i);
                }
                continue;
            }

            valueBuilder.append(c);
        }

        if (!keyBuilder.isEmpty()) {
            String thisKey = keyBuilder.toString();
            String thisValue = hasValue ? valueBuilder.toString() : null;
            values.put(thisKey, thisValue);
        }

        // let's get the message description - we will need it for building the response
        Integer msgCode = Integer.parseInt(values.get("code"));
        MessageDescription description = All.getMessageDescription(msgCode);

        // now we can finally build the response
        // retrieve the always present values
        Integer errorCode = Integer.parseInt(values.get("error"));
        String errorMsg = values.get("msg");

        // retrieve the message specific values
        HashMap<String, String> responseValues = new HashMap<>();
        for (String output : description.getOutputFields()) {
            String thisValue = values.get("out-" + output);
            responseValues.put(output, thisValue);
        }

        // build and return the actual response
        Response response = new Response(
                new Status(errorCode, errorMsg, transactionId),
                responseValues);
        return response;
    }

    public static String encodeValue(String value) {
        // if we have null, then there is no data
        if (value == null) return "";

        // otherwise, all data is wraped in quotes
        StringBuilder builder = new StringBuilder();
        builder.append('"');
        for (char c : value.toCharArray()) {
            // handle any special characters
            switch (c) {
                case '"':
                    builder.append("\\\"");
                    break;

                case '\\':
                    builder.append("\\\\");
                    break;

                case '\r':
                    builder.append("\\r");
                    break;

                case '\n':
                    builder.append("\\n");
                    break;

                default:
                    // not a special character, just append it
                    builder.append(c);
                    break;
            }
        }

        builder.append('"');
        return builder.toString();
    }
}
