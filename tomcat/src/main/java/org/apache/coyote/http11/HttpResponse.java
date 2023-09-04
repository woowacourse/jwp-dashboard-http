package org.apache.coyote.http11;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HttpResponse {

    private static final String EMPTY_BODY = "";

    private final HttpStatus status;
    private final String body;
    private final Map<String, HttpHeader> headers = new HashMap<>();

    public HttpResponse(final String body, final String contentType) {
        this(HttpStatus.OK, body, List.of(new HttpHeader("Content-Type", contentType)));
    }

    public HttpResponse(final HttpStatus status, final List<HttpHeader> headers) {
        this(status, EMPTY_BODY, headers);
    }

    public HttpResponse(final HttpStatus status, final String body, final List<HttpHeader> headers) {
        this.status = status;
        this.body = body;
        for (HttpHeader header : headers) {
            putHeader(header);
        }
        putHeader(new HttpHeader("Content-Length", String.valueOf(body.getBytes().length)));
    }

    public static HttpResponse redirectTo(final String location) {
        return new HttpResponse(
                HttpStatus.FOUND,
                List.of(new HttpHeader("Location", location))
        );
    }

    public HttpStatus getStatus() {
        return status;
    }

    public String getBody() {
        return body;
    }

    public HttpHeader getHeader(String name) {
        return headers.get(name);
    }

    private void putHeader(HttpHeader header) {
        this.headers.put(header.getName(), header);
    }

    public List<HttpHeader> getHeaders() {
        return new ArrayList<>(headers.values());
    }
}