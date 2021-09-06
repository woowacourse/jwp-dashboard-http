package nextstep.jwp.exception;

import nextstep.jwp.http.HttpStatus;

public class NotFoundException extends AbstractHttpException {

    private static final String MESSAGE = "/404.html";
    private static final HttpStatus httpStatus = HttpStatus.NOT_FOUND;

    @Override
    public String getMessage() {
        return MESSAGE;
    }

    @Override
    public HttpStatus httpStatus() {
        return httpStatus;
    }
}
