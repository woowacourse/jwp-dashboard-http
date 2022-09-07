package org.apache.coyote.http11;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class RequestLine {

    private static final String REQUEST_LINE_DELIMITER = " ";
    private static final String URL_DELIMITER = "?";
    private static final String REQUEST_PARAM_DELIMITER = "&";
    private static final String VALUE_DELIMITER = "=";

    private final HttpMethod httpMethod;
    private final String url;
    private final Map<String, String> queryParams;

    private RequestLine(final HttpMethod httpMethod, final String url,
                        final Map<String, String> queryParams) {
        this.httpMethod = httpMethod;
        this.url = url;
        this.queryParams = queryParams;
    }

    public static RequestLine of(final String value) {
        final String[] requestValues = value.split(REQUEST_LINE_DELIMITER);
        final HttpMethod httpMethod = HttpMethod.of(requestValues[0]);
        final Map<String, String> queryParams = new HashMap<>(Collections.emptyMap());

        if (!hasQueryString(value)) {
            final String url = requestValues[1];
            return new RequestLine(httpMethod, url, queryParams);
        }

        final int index = requestValues[1].indexOf(URL_DELIMITER);
        final String url = requestValues[1].substring(0, index);
        queryParams.putAll(parseQueryString(requestValues[1].substring(index + 1)));

        return new RequestLine(httpMethod, url, queryParams);
    }

    private static boolean hasQueryString(final String value) {
        return value.contains(URL_DELIMITER);
    }

    private static Map<String, String> parseQueryString(final String queryString) {
        final String[] queryPairs = queryString.split(REQUEST_PARAM_DELIMITER);

        final Map<String, String> queryParams = new HashMap<>();
        for (String queryPair : queryPairs) {
            final String[] values = queryPair.split(VALUE_DELIMITER);
            queryParams.put(values[0], values[1]);
        }
        return queryParams;
    }

    public boolean isGet() {
        return httpMethod == HttpMethod.GET;
    }

    public HttpMethod getHttpMethod() {
        return httpMethod;
    }

    public String getUrl() {
        return url;
    }

    public Map<String, String> getQueryParams() {
        return queryParams;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final RequestLine that = (RequestLine) o;
        return httpMethod == that.httpMethod && Objects.equals(url, that.url) && Objects.equals(
                queryParams, that.queryParams);
    }

    @Override
    public int hashCode() {
        return Objects.hash(httpMethod, url, queryParams);
    }
}
