package nextstep.jwp.http;

import java.util.HashMap;
import java.util.Map;

public class HttpSession {

    private final String id;
    private final Map<String, Object> values = new HashMap<>();

    public HttpSession(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setAttribute(String name, Object value) {
        values.put(name, value);
    }

    public Object getAttribute(String name) {
        return values.get(name);
    }
}
