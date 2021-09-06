package nextstep.jwp.exception;

import nextstep.jwp.http.HttpStatus;

public class UnauthorizedException extends AbstractHttpException {

    private static final String MESSAGE = "/401.html";
    private static final HttpStatus httpStatus = HttpStatus.UNAUTHORIZED;

    @Override
    public String getMessage() {
        return MESSAGE;
    }

    @Override
    public HttpStatus httpStatus() {
        return httpStatus;
    }
}
