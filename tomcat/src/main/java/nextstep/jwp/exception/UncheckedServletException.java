package nextstep.jwp.exception;

public class UncheckedServletException extends RuntimeException {

    public UncheckedServletException(Exception e) {
        super(e);
    }

    public UncheckedServletException(String message) {
        super(message);
    }
}
