package org.apache.coyote;

import org.apache.coyote.http.request.HttpRequest;
import org.apache.coyote.http.response.HttpResponse;
import org.apache.coyote.http.session.Session;

public interface Controller {

    void service(HttpRequest httpRequest, HttpResponse httpResponse, Session session);
}
