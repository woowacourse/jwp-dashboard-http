package org.apache.coyote.http11.request;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import org.apache.coyote.http11.common.Headers;
import org.apache.coyote.http11.common.Method;

public class Request {

    private final Method method;
    private final String uri;
    private final Headers headers;
    private final String body;

    private Request(
            Method method,
            String uri,
            Headers headers,
            String body
    ) {
        this.method = method;
        this.uri = uri;
        this.headers = headers;
        this.body = body;
    }

    public static Request from(
            String methodName,
            String requestURI,
            Headers headers,
            String body
    ) {
        Method method = Method.find(methodName)
                .orElseThrow(() -> new IllegalArgumentException("invalid method"));

        return new Request(method, requestURI, headers, body);
    }

    public static Optional<Request> read(BufferedReader bufferedReader) throws IOException {
        String requestHead = bufferedReader.readLine();

        Headers headers = readHeaders(bufferedReader);

        String[] head = requestHead.split(" ");
        String method = head[0];
        String uri = head[1];
        String body = readBody(bufferedReader, headers);

        return Optional.of(
                Request.from(
                        method,
                        uri,
                        headers,
                        body
                )
        );
    }

    private static Headers readHeaders(BufferedReader bufferedReader) throws IOException {
        Map<String, String> headers = new HashMap<>();
        String line;
        while (!"".equals((line = bufferedReader.readLine()))) {
            String[] header = line.split(": ");
            String key = header[0];
            String value = header[1].trim();
            headers.put(key, value);
        }

        return new Headers(headers);
    }

    private static String readBody(BufferedReader bufferedReader, Headers headers) throws IOException {
        String body = "";
        if (headers.hasContentLength()) {
            int contentLength = Integer.parseInt(headers.find("Content-Length"));
            char[] buffer = new char[contentLength];
            bufferedReader.read(buffer, 0, contentLength);
            body = new String(buffer);
        }

        return body;
    }

    public String getPath() {
        return URI.create(uri).getPath();
    }

    public Method getMethod() {
        return method;
    }

    public String getUri() {
        return uri;
    }

    public String getBody() {
        return body;
    }

    public boolean hasCookieByName(String name) {
        return headers.getCookie()
                .isExistByName(name);
    }

    @Override
    public String toString() {
        return "Request{" +
                "method=" + method +
                ", URI='" + uri + '\'' +
                ", headers='" + headers + '\'' +
                ", body='" + body + '\'' +
                '}';
    }

    // TODO Builder 패턴 적용
}
