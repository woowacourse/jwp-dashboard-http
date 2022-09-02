package org.apache.coyote.http11;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HttpRequest {

    private static final int START_LINE_INDEX = 0;
    private static final String REQUEST_URI = "RequestURI";
    private static final String HTTP_METHOD = "HttpMethod";

    private static final String START_LINE_DELIMITER = " ";
    private static final String REQUEST_PARAM_DELIMITER = "?";
    private static final String PARAMS_DELIMITER = "&";
    private static final String KEY_VALUE_DELIMITER = "=";
    private static final String REQUEST_HEADER_DELIMITER = ": ";

    private final Map<String, String> requestMap;
    private final Map<String, String> paramMap;

    private HttpRequest(final Map<String, String> requestMap, final Map<String, String> paramMap) {
        this.requestMap = new HashMap<>(requestMap);
        this.paramMap = new HashMap<>(paramMap);
    }

    public static HttpRequest from(final List<String> rawHttpRequest) {
        final Map<String, String> requestMap = new HashMap<>();
        final Map<String, String> paramMap = new HashMap<>();

        // startLine 처리
        extractStartLine(rawHttpRequest, requestMap, paramMap);

        // header 처리
        extractHeader(rawHttpRequest, requestMap);

        return new HttpRequest(requestMap, paramMap);
    }

    private static void extractStartLine(final List<String> httpRequestStrs, final Map<String, String> requestMap,
                                         final Map<String, String> paramMap) {
        final String startLine = httpRequestStrs.remove(START_LINE_INDEX);
        final String[] startLineContents = startLine.split(START_LINE_DELIMITER);
        requestMap.put(HTTP_METHOD, startLineContents[0]);

        extractRequestUri(requestMap, paramMap, startLineContents);
    }

    private static void extractRequestUri(final Map<String, String> requestMap, final Map<String, String> paramMap,
                                          final String[] startLineContents) {
        final String uri = startLineContents[1];
        if (uri.contains(REQUEST_PARAM_DELIMITER)) {
            final int index = uri.indexOf(REQUEST_PARAM_DELIMITER);
            final String path = uri.substring(0, index);
            final String queryString = uri.substring(index + 1);
            requestMap.put(REQUEST_URI, path);
            extractRequestParam(paramMap, queryString);
        } else {
            requestMap.put(REQUEST_URI, uri);
        }
    }

    private static void extractRequestParam(final Map<String, String> paramMap, final String queryString) {
        final String[] queryParams = queryString.split(PARAMS_DELIMITER);
        Arrays.stream(queryParams)
                .map(it -> it.split(KEY_VALUE_DELIMITER))
                .forEach(paramKeyValue -> paramMap.put(paramKeyValue[0], paramKeyValue[1]));
    }

    private static void extractHeader(final List<String> httpRequestStrs, final Map<String, String> requestMap) {
        for (final String requestHeader : httpRequestStrs) {
            if (requestHeader.equals("")) {
                break;
            }
            final String[] requestKeyValue = requestHeader.split(REQUEST_HEADER_DELIMITER);
            requestMap.put(requestKeyValue[START_LINE_INDEX], requestKeyValue[1]);
        }
    }

    public boolean haveParam(final String param) {
        return paramMap.containsKey(param);
    }

    public String getPath() {
        return requestMap.get(REQUEST_URI);
    }

    public String getParam(final String param) {
        return paramMap.get(param);
    }
}
