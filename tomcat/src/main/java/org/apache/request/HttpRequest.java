package org.apache.request;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class HttpRequest {

    public static final String WHITE_SPACE = " ";
    public static final String HEADER_KEY_VALUE_DELIMITER = ":";
    public static final String EMPTY_BODY = "";

    private RequestLine requestLine;
    private Map<String, String> header;
    private String body;

    public HttpRequest(final RequestLine requestLine, final Map<String, String> header, final String body) {
        this.requestLine = requestLine;
        this.header = header;
        this.body = body;
    }

    public HttpRequest(final String method, final String path, final String httpVersion,
                       final Map<String, String> header,
                       final String body) {
        this.requestLine = new RequestLine(method, path, httpVersion);
        this.header = header;
        this.body = body;
    }

    public HttpRequest(final BufferedReader bufferedReader) throws IOException {
        this.requestLine = parseRequestLine(bufferedReader.readLine());
        this.header = parseHeader(bufferedReader);
        this.body = parseRequestBody(bufferedReader);
    }

    private RequestLine parseRequestLine(final String firstLine) {
        String[] requestLine = firstLine.split(WHITE_SPACE);
        return new RequestLine(requestLine[0], requestLine[1], requestLine[2]);
    }

    private Map<String, String> parseHeader(final BufferedReader bufferedReader) throws IOException {
        List<String> headerLines = new ArrayList<>();
        String line = bufferedReader.readLine();
        while (!line.isEmpty()) {
            headerLines.add(line);
            line = bufferedReader.readLine();
        }
        Map<String, String> header = new HashMap<>();
        for (int i = 0; i < headerLines.size(); i++) {
            String[] headerLine = headerLines.get(i).split(HEADER_KEY_VALUE_DELIMITER);
            header.put(headerLine[0].trim(), headerLine[1].trim());
        }
        return header;
    }

    private String parseRequestBody(final BufferedReader bufferedReader) throws IOException {
        if (!isHeaderKey("Content-Length")) {
            return EMPTY_BODY;
        }
        int contentLength = Integer.parseInt(getHeaderValue("Content-Length").orElse("0"));
        char[] body = new char[contentLength];
        bufferedReader.read(body, 0, contentLength);
        return new String(body);
    }

    private boolean isHeaderKey(final String key) {
        return this.header.containsKey(key);
    }

    public Optional<String> getHeaderValue(final String key) {
        if (!header.containsKey(key)) {
            return Optional.empty();
        }
        return Optional.of(header.get(key));
    }

    public RequestLine getRequestLine() {
        return requestLine;
    }

    public String getBody() {
        return body;
    }
}
