package nextstep.jwp.controller;

import org.apache.coyote.request.HttpRequest;
import org.apache.coyote.response.HttpResponse;

public interface Controller {

    void service(HttpRequest request, HttpResponse httpResponse);
}
