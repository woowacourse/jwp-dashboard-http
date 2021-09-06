package nextstep.jwp.exception;

import nextstep.jwp.http.HttpStatus;

public class UsernameConflictException extends AbstractHttpException {

    private static final String MESSAGE = "{\"message\": \"이미 존재하는 아이디 입니다.\"}";
    private static final HttpStatus httpStatus = HttpStatus.CONFLICT;

    public UsernameConflictException() {
        super(MESSAGE);
    }

    @Override
    public HttpStatus httpStatus() {
        return httpStatus;
    }
}
