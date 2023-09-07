package org.apache.coyote.http11.cookie;

import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

public class HttpCookie {
    private static final String JSESSIONID = "JSESSIONID";
    private static final String LIST_DELIMITER = "; ";
    private static final String PAIR_DELIMITER = "=";

    private final Map<String, String> cookie;

    private HttpCookie(final Map<String, String> cookie) {
        this.cookie = cookie;
    }

    public static HttpCookie from(final String cookie) {
        return new HttpCookie(Arrays.stream(cookie.split(LIST_DELIMITER))
                .map(data -> data.split(PAIR_DELIMITER))
                .collect(Collectors.toMap(
                        data -> data[0],
                        data -> data[1])
                )
        );
    }

    public static HttpCookie empty() {
        return new HttpCookie(Collections.emptyMap());
    }

    public static HttpCookie create() {
        return new HttpCookie(Map.of(JSESSIONID, String.valueOf(UUID.randomUUID())));
    }

    public boolean hasJSESSIONID() {
        return this.cookie.containsKey(JSESSIONID);
    }

    public Map<String, String> getCookie() {
        return cookie;
    }

    public String getJSESSIONID() {
        return this.cookie.get(JSESSIONID);
    }
}