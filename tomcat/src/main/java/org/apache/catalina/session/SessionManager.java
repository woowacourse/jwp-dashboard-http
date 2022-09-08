package org.apache.catalina.session;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class SessionManager implements Manager {

    private static final Map<String, Session> SESSIONS = new ConcurrentHashMap<>();

    @Override
    public void add(final Session session) {
        SESSIONS.put(session.getId(), session);
    }

    @Override
    public Session findSession(final String id) {
        if (!SESSIONS.containsKey(id)) {
            throw new IllegalArgumentException("No Such Session exists");
        }
        return SESSIONS.get(id);
    }

    @Override
    public void remove(final Session session) {
        if (!SESSIONS.containsKey(session.getId())) {
            throw new IllegalArgumentException("No Such Session exists");
        }
        SESSIONS.remove(session.getId());
    }

    public boolean hasSession(final String id) {
        return SESSIONS.containsKey(id);
    }
}
