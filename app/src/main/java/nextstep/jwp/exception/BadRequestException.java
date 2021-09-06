package nextstep.jwp.exception;

import nextstep.jwp.http.HttpStatus;

public class BadRequestException extends AbstractHttpException {

    private static final String MESSAGE = "{\"message\": \"잘못된 request 입니다.\"}";
    private static final HttpStatus httpStatus = HttpStatus.BAD_REQUEST;

    public BadRequestException() {
        super(MESSAGE);
    }

    @Override
    public HttpStatus httpStatus() {
        return httpStatus;
    }
}
