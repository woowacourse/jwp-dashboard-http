package org.apache.coyote.http11.request;

import java.util.Map;
import utils.ParseUtils;

public class RequestHeaders {

    private static final String CONTENT_LENGTH = "Content-Length";
    private static final String COOKIE = "Cookie";

    private final Map<String, String> headers;

    public RequestHeaders(final Map<String, String> headers) {
        this.headers = headers;
    }

    public static RequestHeaders from(final String headers) {
        return new RequestHeaders(ParseUtils.parse(headers, "\r\n", ": "));
    }

    public boolean hasRequestBody() {
        return headers.containsKey(CONTENT_LENGTH);
    }

    public String getContentLength() {
        return headers.get(CONTENT_LENGTH);
    }

    public boolean hasCookies() {
        return headers.containsKey(COOKIE);
    }

    public String getCookies() {
        return headers.get(COOKIE);
    }
}
