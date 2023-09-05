package org.apache.coyote.http11.response.header;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class Headers {

    private final LinkedHashMap<Header, String> headers;

    private Headers(final LinkedHashMap<Header, String> headers) {
        this.headers = headers;
    }

    public static Headers of(final ContentType contentType, final String body) {
        LinkedHashMap<Header, String> headers = new LinkedHashMap<>();
        headers.put(Header.CONTENT_TYPE, contentType.toString());
        headers.put(Header.CONTENT_LENGTH, Integer.toString(body.getBytes().length));
        return new Headers(headers);
    }

    public void put(final Header header, final String value) {
        this.headers.put(header, value);
    }

    public void putAll(final Map<Header, String> headers) {
        this.headers.putAll(headers);
    }

    @Override
    public String toString() {
        return headers.keySet()
                .stream()
                .map(key -> String.join(": ", key.getName(), headers.get(key) + " "))
                .collect(Collectors.joining("\r\n"));
    }
}