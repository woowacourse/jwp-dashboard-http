package org.apache.coyote.http11.request;

import java.util.Objects;

public class RequestLine {
    private static final int DEFAULT_LENGTH = 3;
    private static final int REQUEST_METHOD_INDEX = 0;
    private static final int REQUEST_URI_INDEX = 1;
    private static final int REQUEST_VERSION_INDEX = 2;

    private final String method;
    private final RequestUri requestUri;
    private final String version;

    private RequestLine(final String method, final RequestUri requestUri, final String version) {
        this.method = method;
        this.requestUri = requestUri;
        this.version = version;
    }

    public static RequestLine from(final String line) {
        Objects.requireNonNull(line);
        final String[] requestLine = line.split(" ");
        validateLength(requestLine);

        final String method = requestLine[REQUEST_METHOD_INDEX];
        final RequestUri requestUri = RequestUri.from(requestLine[REQUEST_URI_INDEX]);
        final String version = requestLine[REQUEST_VERSION_INDEX];

        return new RequestLine(method, requestUri, version);
    }

    private static void validateLength(final String[] requestLine) {
        if (requestLine.length != DEFAULT_LENGTH) {
            throw new IllegalArgumentException("올바른 RequestLine 형식이 아닙니다.");
        }
    }

    public String findQueryValue(final String key) {
        return requestUri.findQueryValue(key);
    }

    public boolean containsQuery() {
        return requestUri.containsQuery();
    }

    public String getUri() {
        return requestUri.getPath();
    }

    public String getMethod() {
        return method;
    }

    public RequestUri getRequestUri() {
        return requestUri;
    }

    public String getVersion() {
        return version;
    }
}
