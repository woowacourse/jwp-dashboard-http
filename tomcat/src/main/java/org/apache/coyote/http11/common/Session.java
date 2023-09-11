package org.apache.coyote.http11.common;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Session {

    private final String id;
    private final Map<String, Object> items = new HashMap<>();

    public Session() {
        this(UUID.randomUUID().toString(), Collections.emptyMap());
    }

    public Session(final String id) {
        this(id, Collections.emptyMap());
    }

    public Session(final String id, Map<String, Object> attributes) {
        this.id = id;
        this.items.putAll(attributes);
    }

    public Object getAttribute(final String key) {
        return items.get(key);
    }

    public void setAttribute(final String key, final Object value) {
        items.put(key, value);
    }

    public void removeAttribute(final String key) {
        items.remove(key);
    }

    public void invalidate() {
        items.clear();
    }

    public String getId() {
        return id;
    }

    public Map<String, Object> getItems() {
        return items;
    }
}
