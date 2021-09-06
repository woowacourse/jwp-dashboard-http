package nextstep.jwp.exception;

import nextstep.jwp.http.HttpStatus;

public abstract class AbstractHttpException extends RuntimeException implements HttpException {

    protected AbstractHttpException() {
    }

    protected AbstractHttpException(String message) {
        super(message);
    }

    public abstract HttpStatus httpStatus();
}
