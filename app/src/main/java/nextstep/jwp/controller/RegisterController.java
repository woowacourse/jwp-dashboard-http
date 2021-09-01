package nextstep.jwp.controller;

import nextstep.jwp.http.*;
import nextstep.jwp.http.request.HttpRequest;
import nextstep.jwp.http.response.HttpResponse;
import nextstep.jwp.service.UserService;

public class RegisterController extends AbstractController {
    private final UserService userService;

    public RegisterController(final UserService userService) {
        this.userService = userService;
    }

    @Override
    public boolean canHandle(final HttpRequest httpRequest) {
        final HttpMethod httpMethod = httpRequest.getHttpMethod();
        final String path = httpRequest.getPath();
        return httpMethod.isPost() && "/register".equals(path);
    }

    @Override
    public HttpResponse doGet(HttpRequest httpRequest) {
        throw new UnsupportedOperationException();
    }

    @Override
    public HttpResponse doPost(final HttpRequest httpRequest) {
        userService.save(httpRequest.getPayload());

        final String redirectUrl = "/index.html";
        return new HttpResponse(
                httpRequest.getProtocol(),
                HttpStatus.SEE_OTHER,
                httpRequest.getCookie(),
                redirectUrl
        );
    }
}
