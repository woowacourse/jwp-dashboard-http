package org.apache.catalina;

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
        return SESSIONS.getOrDefault(id, null);
    }

    @Override
    public void remove(final Session session) {
        SESSIONS.remove(session.getId());
    }
}
