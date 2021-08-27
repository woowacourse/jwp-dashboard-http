package nextstep.jwp.httpserver.mapping;

import nextstep.jwp.httpserver.BeanFactory;
import nextstep.jwp.httpserver.controller.Handler;
import nextstep.jwp.httpserver.domain.request.HttpRequest;

public class StaticViewHandlerMapping implements HandlerMapping {

    @Override
    public boolean isHandle(HttpRequest httpRequest) {
        final String requestUri = httpRequest.getRequestUri();
        return requestUri.endsWith(".html") || requestUri.equals("/");
    }

    @Override
    public Handler find(HttpRequest httpRequest) {
        return (Handler) BeanFactory.getBean("staticViewController");
    }
}
