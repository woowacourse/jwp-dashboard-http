package org.apache.coyote.http11.httpmessage.support;

import java.util.Arrays;
import org.apache.coyote.http11.httpmessage.HttpHeader;
import org.apache.coyote.http11.httpmessage.request.HttpMethod;

public class HttpRequestParser {

    private static final String CRLF = "\r\n";
    private static final String QUERY_DELIMITER = "\\?";
    private static final String SPACE = " ";
    private static final int METHOD_INDEX = 0;
    private static final int PATH_INDEX = 0;
    private static final int REQUEST_INFO_INDEX = 0;
    private static final int HEADER_START = 1;
    public static final int URL_INDEX = 1;
    private static final int PROTOCOL_INDEX = 2;

    private HttpRequestParser() {
    }

    public static HttpMethod getHttpMethod(final String request) {
        final String[] splitRequest = request.split(CRLF);
        return HttpMethod.getMethod(splitRequest[REQUEST_INFO_INDEX].split(SPACE)[METHOD_INDEX]);
    }

    public static String getPath(final String request) {
        final String[] splitRequest = request.split(CRLF);
        return splitRequest[REQUEST_INFO_INDEX].split(SPACE)[URL_INDEX].split(QUERY_DELIMITER)[PATH_INDEX];
    }

    public static String getProtocol(final String request) {
        final String[] splitRequest = request.split(CRLF);
        return splitRequest[REQUEST_INFO_INDEX].split(SPACE)[PROTOCOL_INDEX];
    }

    public static HttpHeader getHeader(final String request) {
        final String[] splitRequest = request.split(CRLF);
        final String[] headers = Arrays.copyOfRange(splitRequest, HEADER_START, splitRequest.length);
        return HttpHeader.fromRequest(headers);
    }
}
