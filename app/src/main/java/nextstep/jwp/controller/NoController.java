package nextstep.jwp.controller;

import nextstep.jwp.http.CustomException;
import nextstep.jwp.http.HttpRequest;
import nextstep.jwp.http.HttpResponse;
import nextstep.jwp.http.HttpStatus;
import nextstep.jwp.http.StaticFileReader;

public class NoController implements Controller {

    @Override
    public HttpResponse get(HttpRequest request) {
        return renderNotFoundPage();
    }

    @Override
    public HttpResponse post(HttpRequest request) {
        return renderNotFoundPage();
    }

    private HttpResponse renderNotFoundPage() {
        StaticFileReader staticFileReader = new StaticFileReader();
        return new HttpResponse(HttpStatus.NOT_FOUND, staticFileReader.read("404.html"));
    }

    @Override
    public boolean isSatisfiedBy(String httpUriPath) {
        throw new CustomException("존재하지 않는 컨트롤러입니다.");
    }
}
