package nextstep.jwp.controller;

import static org.apache.coyote.HttpStatus.OK;
import static org.apache.coyote.header.HttpHeaders.CONTENT_LENGTH;
import static org.apache.coyote.header.HttpHeaders.CONTENT_TYPE;
import static org.apache.coyote.header.HttpMethod.GET;

import nextstep.jwp.util.ResourceLoaderUtil;
import org.apache.coyote.HttpRequest;
import org.apache.coyote.HttpResponse;
import org.apache.coyote.header.ContentType;
import org.apache.coyote.http11.handler.Controller;

public class RegisterPageController implements Controller {

    @Override
    public boolean support(HttpRequest request) {
        return request.httpMethod().equals(GET) && request.requestUrl().equals("/register");
    }

    @Override
    public void handle(HttpRequest request, HttpResponse response) {
        String content = ResourceLoaderUtil.loadContent(request.requestUrl());
        response.setVersion(request.protocolVersion());
        response.setStatus(OK);
        response.addHeader(CONTENT_TYPE, ContentType.negotiate(request.requestUrl()));
        response.addHeader(CONTENT_LENGTH, String.valueOf(content.getBytes().length));
        response.setBody(content);
    }
}
