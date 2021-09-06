package nextstep.jwp.http;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class HttpSessions {

    private static final Map<String, HttpSession> SESSIONS = new ConcurrentHashMap<>();

    private HttpSessions() {
    }

    public static HttpSession getSession(String id) {
        return SESSIONS.get(id);
    }

    public static void add(String id, HttpSession httpSession) {
        SESSIONS.put(id, httpSession);
    }
}
