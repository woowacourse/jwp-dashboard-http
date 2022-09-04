package org.apache.mvc.handlerchain;

import java.util.List;
import java.util.Map;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.ResponseEntity;
import org.apache.coyote.http11.response.headers.ContentType;
import org.apache.mvc.Controller;
import org.apache.mvc.ControllerParser;

public class ControllerRequestHandlerChain implements RequestHandlerChain {

    private static final String REDIRECT = "redirect:";

    private final RequestHandlerChain next;
    private final Map<RequestKey, RequestHandlerMethod> map;

    public ControllerRequestHandlerChain(RequestHandlerChain next, List<Controller> controllers) {
        this.next = next;
        this.map = ControllerParser.parse(controllers);
    }

    @Override
    public HttpResponse handle(HttpRequest request, HttpResponse response) {
        RequestHandlerMethod requestHandlerMethod = findMappedHandlerMethod(request);
        if (requestHandlerMethod == null) {
            return next.handle(request, response);
        }

        ResponseEntity entity = requestHandlerMethod.handle(request);
        response = response.update(entity.getStatus(), entity.getBody());
        if (isRedirect(entity.getBody())) {
            return next.handle(redirectPath(request, entity.getBody()), response);
        }
        return response.addHeader(ContentType.TEXT_HTML);
    }

    private RequestHandlerMethod findMappedHandlerMethod(HttpRequest httpRequest) {
        RequestKey requestKey = new RequestKey(httpRequest.getMethod(), httpRequest.getPath());
        return map.get(requestKey);
    }

    private boolean isRedirect(String entityBody) {
        return entityBody.startsWith(REDIRECT);
    }

    private HttpRequest redirectPath(HttpRequest request, String body) {
        return request.redirectPath(body.replace(REDIRECT, ""));
    }
}
