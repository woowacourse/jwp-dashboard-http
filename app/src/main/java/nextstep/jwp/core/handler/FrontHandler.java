package nextstep.jwp.core.handler;

import nextstep.jwp.core.ApplicationContext;
import nextstep.jwp.core.DefaultApplicationContext;
import nextstep.jwp.core.handler.mapping.HandlerMapping;
import nextstep.jwp.core.handler.mapping.MethodHandlerMapping;
import nextstep.jwp.exception.PageNotFoundException;
import nextstep.jwp.request.HttpRequest;
import nextstep.jwp.response.ContentType;
import nextstep.jwp.response.HttpResponse;
import nextstep.jwp.response.StatusCode;

public class FrontHandler {

    private HandlerMapping handlerMapping;
    private ApplicationContext applicationContext;

    public FrontHandler(String basePackage) {
        this.applicationContext = new DefaultApplicationContext(basePackage);
        this.handlerMapping = new MethodHandlerMapping(applicationContext);
    }

    public HttpResponse getResponse(HttpRequest httpRequest, HttpResponse httpResponse) {
        try {
            final Handler handler = handlerMapping.findHandler(httpRequest);
            if (handler == null) {
                return resourceHandle(httpRequest, httpResponse);
            }

            final String content = handler.doRequest(httpRequest, httpResponse);
            httpResponse.addBody(content);
        } catch (PageNotFoundException e) {
            httpResponse.addStatus(StatusCode.NOT_FOUND);
            httpResponse.addPage("static/404.html");
        }
        return httpResponse;
    }

    private HttpResponse resourceHandle(HttpRequest httpRequest, HttpResponse httpResponse) {
        final String file = httpRequest.httpUrl();
        httpResponse.addPage("static" + file);
        httpResponse.addHeader("Content-Type", ContentType.contentType(file));
        httpResponse.addStatus(StatusCode.OK);
        return httpResponse;
    }
}
