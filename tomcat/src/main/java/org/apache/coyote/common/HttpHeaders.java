package org.apache.coyote.common;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class HttpHeaders {

    private final Map<String, List<String>> headers;

    public HttpHeaders() {
        this.headers = new LinkedHashMap<>();
    }

    public void addHeader(String key, String value) {
        List<String> header = headers.computeIfAbsent(key, ignore -> new ArrayList<>());
        header.add(value);
    }

    public void addHeader(String key, List<String> values) {
        List<String> header = headers.computeIfAbsent(key, ignore -> new ArrayList<>());
        header.addAll(values);
    }

    public void setHeader(String key, String value) {
        ArrayList<String> values = new ArrayList<>();
        values.add(value);
        headers.put(key, values);
    }

    public void setContentType(HttpContentType contentType) {
        setHeader("Content-Type", contentType.getValue());
    }

    public void setHeader(String key, List<String> values) {
        headers.put(key, values);
    }

    public String getHeader(String key) {
        List<String> values = headers.getOrDefault(key, Collections.emptyList());
        return String.join(",", values);
    }

    public Map<String, List<String>> getHeaders() {
        return Collections.unmodifiableMap(headers);
    }
}
