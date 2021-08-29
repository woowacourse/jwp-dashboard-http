package nextstep.jwp.http.request;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import nextstep.jwp.exception.EmptyQueryParametersException;
import nextstep.jwp.exception.InvalidRequestUriException;
import nextstep.jwp.exception.QueryParameterNotFoundException;

public class RequestUri {

    private static final Map<String, String> EMPTY_PARAMETERS = new HashMap<>();
    private static final int KEY_INDEX = 0;
    private static final int VALUE_INDEX = 1;
    private static final int URI_INDEX = 0;
    private static final int QUERY_INDEX = 1;

    private final String value;
    private final Map<String, String> queryParameters;

    // TODO: '*'이 들어올 경우 모든 경로를 처리한다.
    private RequestUri(String value, Map<String, String> queryParameters) {
        this.value = value;
        this.queryParameters = queryParameters;
        validateNull(this.value);
    }

    public static RequestUri parse(String uri) {
        if (uri.contains("?")) {
            String[] splitedUri = uri.split("\\?");

            String parsedUri = splitedUri[URI_INDEX];
            Map<String, String> queryParameters = getParsedQueryParameters(splitedUri[QUERY_INDEX]);

            return new RequestUri(parsedUri, queryParameters);
        }

        return new RequestUri(uri, EMPTY_PARAMETERS);
    }

    private static Map<String, String> getParsedQueryParameters(String uri) {
        Map<String, String> querys = new HashMap<>();

        String[] splitedQuerys = uri.split("&");
        for (String query : splitedQuerys) {
            String[] splitedQuery = query.split("=");
            String key = splitedQuery[KEY_INDEX];
            String value = splitedQuery[VALUE_INDEX];

            querys.put(key, value);
        }

        return querys;
    }

    private void validateNull(String value) {
        if (Objects.isNull(value)) {
            throw new InvalidRequestUriException();
        }
    }

    public boolean hasQueryParam() {
        return !queryParameters.isEmpty();
    }

    public String getQueryParameter(String parameter) {
        validateEmpty();
        validateExistQuery(parameter);

        return queryParameters.get(parameter);
    }

    private void validateEmpty() {
        if (queryParameters.isEmpty()) {
            throw new EmptyQueryParametersException();
        }
    }

    private void validateExistQuery(String parameter) {
        if (!queryParameters.containsKey(parameter)) {
            throw new QueryParameterNotFoundException();
        }
    }

    public String getValue() {
        return value;
    }
}
