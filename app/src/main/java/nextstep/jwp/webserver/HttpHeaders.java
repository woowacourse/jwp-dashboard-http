package nextstep.jwp.webserver;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class HttpHeaders {

    public static final HttpHeaders EMPTY_HEADERS = new HttpHeaders(new HashMap<>());

    private final Map<String, String> headers;

    public HttpHeaders(Map<String, String> headers) {
        this.headers = headers;
    }

    public HttpHeaders(List<String> headerList) {
        this(parseHeaders(headerList));
    }

    public HttpHeaders() {
        this(new HashMap<>());
    }

    private static Map<String, String> parseHeaders(List<String> headerList) {
        Map<String, String> headers = new HashMap<>();
        for (String header : headerList) {
            String[] keyValue = header.split(":");
            headers.put(keyValue[0], keyValue[1]);
        }
        return headers;
    }

    public String get(String name) {
        return headers.get(name);
    }

    public void set(String name, String value) {
        headers.put(name, value);
    }

    public String getString() {
        return headers.keySet().stream()
                .map(this::headerToString)
                .collect(Collectors.joining("\r\n"));
    }

    private String headerToString(String name) {
        return name + ": " + headers.get(name);
    }
}
