package nextstep.jwp.http;

import nextstep.jwp.exception.InvalidCookieException;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class HttpCookie {

    private static final String VALUE_DELIMITER = "; ";
    private static final String KEY_VALUE_DELIMITER = "=";
    private static final int KEY_INDEX = 0;
    private static final int VALUE_INDEX = 1;
    private static final int KEY_VALUE_SIZE = 2;

    private final Map<String, String> value;

    private HttpCookie(final Map<String, String> value) {
        this.value = value;
    }

    public static HttpCookie create() {
        return new HttpCookie(new LinkedHashMap<>());
    }

    public static HttpCookie create(final String raw) {
        Map<String, String> value = new LinkedHashMap<>();
        if (raw != null) {
            putKeyValue(raw, value);
        }
        return new HttpCookie(value);
    }

    private static void putKeyValue(final String raw, final Map<String, String> value) {
        for (String keyValue : raw.split(VALUE_DELIMITER)) {
            final String[] seperatedKeyValue = keyValue.split(KEY_VALUE_DELIMITER);
            validateKeyValueSize(seperatedKeyValue);
            value.put(seperatedKeyValue[KEY_INDEX], seperatedKeyValue[VALUE_INDEX]);
        }
    }

    private static void validateKeyValueSize(final String[] seperatedKeyValue) {
        if (seperatedKeyValue.length != KEY_VALUE_SIZE) {
            throw new InvalidCookieException();
        }
    }

    public void put(final String key, final String value) {
        this.value.put(key, value);
    }

    public String findByKey(final String key) {
        return value.get(key);
    }

    public String parse() {
        final List<String> collect = value.entrySet()
                .stream()
                .map(entry -> entry.getKey() + KEY_VALUE_DELIMITER + entry.getValue())
                .collect(Collectors.toList());
        return String.join(VALUE_DELIMITER, collect);
    }
}
