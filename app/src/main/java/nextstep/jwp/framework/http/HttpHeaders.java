package nextstep.jwp.framework.http;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import nextstep.jwp.framework.util.MultiValueMap;

public class HttpHeaders {
    private final MultiValueMap<String, String> headers;

    public HttpHeaders() {
        this(new MultiValueMap<>());
    }

    public HttpHeaders(MultiValueMap<String, String> headers) {
        this.headers = headers;
    }

    public void addHeader(String name, String... values) {
        addHeader(name, Arrays.asList(values));
    }

    public void addHeader(String name, List<String> values) {
        final List<String> valuesWithoutWhitespace = values.stream()
                                                           .map(String::trim)
                                                           .collect(Collectors.toList());

        this.headers.addAll(HttpHeader.resolve(name), valuesWithoutWhitespace);
    }
}
