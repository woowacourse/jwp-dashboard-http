package org.apache.coyote.http11;

import static org.apache.coyote.http11.StaticPages.INDEX_PAGE;

public class HttpRequestURI {

    private static final String QUERY_STRING_DELIMITER = "?";
    private static final String INDEX_PATH = "/";

    private final String path;
    private final QueryStrings queryStrings;

    public HttpRequestURI(final String path, final QueryStrings queryStrings) {
        this.path = path;
        this.queryStrings = queryStrings;
    }

    public static HttpRequestURI from(final String rawURI) {
        final int queryStringDelimiterIndex = rawURI.indexOf(QUERY_STRING_DELIMITER);

        if (queryStringDelimiterIndex == -1) {
            return new HttpRequestURI(rawURI, QueryStrings.from(null));
        }

        final String path = rawURI.substring(0, queryStringDelimiterIndex);
        final String queryString = rawURI.substring(queryStringDelimiterIndex + 1);

        return new HttpRequestURI(path, QueryStrings.from(queryString));
    }

    public boolean hasQueryString() {
        return queryStrings.exists();
    }

    public boolean hasSamePath(final String path) {
        return path.equals(getPath());
    }

    public String getPath() {
        if (INDEX_PATH.equals(path)) {
            return INDEX_PAGE.getFileName();
        }
        return path;
    }

    public QueryStrings getQueryStrings() {
        return queryStrings;
    }
}