package nextstep.jwp.http.handler.session;

import nextstep.jwp.http.request.HttpRequest;
import nextstep.jwp.http.response.HttpResponse;

public interface SessionHandler {

    void handle(HttpRequest request, HttpResponse response);
}
