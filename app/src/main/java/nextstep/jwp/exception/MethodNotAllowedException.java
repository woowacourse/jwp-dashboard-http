package nextstep.jwp.exception;

import nextstep.jwp.http.HttpStatus;

public class MethodNotAllowedException extends AbstractHttpException {

    private static final String MESSAGE = "{\"message\": \" 요청하신 method를 찾을 수 없습니다.\"}";
    private static final HttpStatus httpStatus = HttpStatus.METHOD_NOT_ALLOWED;

    public MethodNotAllowedException() {
        super(MESSAGE);
    }

    @Override
    public HttpStatus httpStatus() {
        return httpStatus;
    }
}
