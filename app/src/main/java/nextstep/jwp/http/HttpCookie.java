package nextstep.jwp.http;

import java.util.Map;

public class HttpCookie {

    private final Map<String, String> cookie;

    public HttpCookie(Map<String, String> cookie) {
        this.cookie = cookie;
    }

    public String getCookie(String key) {
        return cookie.get(key);
    }
}
