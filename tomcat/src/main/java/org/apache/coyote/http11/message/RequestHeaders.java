package org.apache.coyote.http11.message;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class RequestHeaders {

    private static final String FIELD_VALUE_DELIMITER = ": ";
    private static final String VALUES_DELIMITER = ",";
    private static final int FIELD_INDEX = 0;
    private static final int VALUE_INDEX = 1;

    private final Map<String, String> headersWithValue;

    private RequestHeaders(final Map<String, String> headersWithValue) {
        this.headersWithValue = headersWithValue;
    }

    public static RequestHeaders from(final List<String> headerLines) {
        final Map<String, String> headersWithValue = new HashMap<>();
        String[] parsedHeaderLine;

        for (final String line : headerLines) {
            parsedHeaderLine = line.split(FIELD_VALUE_DELIMITER);
            headersWithValue.put(parsedHeaderLine[FIELD_INDEX].trim(), parsedHeaderLine[VALUE_INDEX].trim());
        }

        return new RequestHeaders(headersWithValue);
    }

    public Optional<String> findFirstValueOfField(final String field) {
        return Optional.ofNullable(headersWithValue.get(field))
            .map(values -> Arrays.stream(values.split(VALUES_DELIMITER)).findFirst())
            .orElse(Optional.empty());
    }

    public Map<String, String> getHeadersWithValue() {
        return new HashMap<>(headersWithValue);
    }
}