package nextstep.jwp.presentation.controller;

import org.apache.coyote.http11.Http11Request;
import org.apache.coyote.http11.Http11Response;
import org.apache.coyote.http11.HttpMethod;
import org.apache.coyote.http11.RequestLine;

public class LoginPageRequestHandler implements RequestHandler {
    @Override
    public String handle(final Http11Request request, final Http11Response response) {
        return "login";
    }

    @Override
    public boolean support(final Http11Request request) {
        final RequestLine requestLine = request.getRequestLine();
        return requestLine.getRequestURI().contains("login") && (requestLine.getHttpMethod() == HttpMethod.GET);
    }
}
