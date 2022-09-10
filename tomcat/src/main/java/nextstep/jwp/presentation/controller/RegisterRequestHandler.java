package nextstep.jwp.presentation.controller;

import static nextstep.jwp.presentation.ResourceLocation.ROOT;

import customservlet.RequestHandler;
import nextstep.jwp.application.MemberService;
import nextstep.jwp.dto.request.RegisterRequest;
import nextstep.jwp.presentation.resolver.FormDataResolver;
import org.apache.coyote.http11.http.HttpRequest;
import org.apache.coyote.http11.http.HttpResponse;
import org.apache.coyote.http11.http.RequestLine;
import org.apache.coyote.http11.util.HttpMethod;
import org.apache.coyote.http11.util.HttpStatus;

public class RegisterRequestHandler implements RequestHandler {

    private final MemberService memberService;

    public RegisterRequestHandler() {
        this.memberService = new MemberService();
    }

    @Override
    public String handle(final HttpRequest request, final HttpResponse response) {
        memberService.register(RegisterRequest.from(FormDataResolver.resolve(request.getRequestBody())));
        response.setStatusCode(HttpStatus.FOUND);
        response.setLocation(ROOT.getLocation());
        return null;
    }

    @Override
    public boolean support(final HttpRequest request) {
        final RequestLine requestLine = request.getRequestLine();
        return requestLine.getRequestUri().containUrl("register") && (requestLine.getHttpMethod() == HttpMethod.POST);
    }
}
