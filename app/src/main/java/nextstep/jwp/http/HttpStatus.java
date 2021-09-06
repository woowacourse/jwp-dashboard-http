package nextstep.jwp.http;

public enum HttpStatus {

    OK(200, "OK"),
    FOUND(302, "Found"),
    BAD_REQUEST(400, "Bad Request"),
    UNAUTHORIZED(401, "Unauthorized"),
    NOT_FOUND(404, "Not Found"),
    METHOD_NOT_ALLOWED(405, "Method Not Allowed"),
    CONFLICT(409, "Conflict");

    private final int status;
    private final String statusMessage;

    HttpStatus(int httpStatus, String statusMessage) {
        this.status = httpStatus;
        this.statusMessage = statusMessage;
    }

    public int getStatus() {
        return status;
    }

    public String getStatusMessage() {
        return statusMessage;
    }

    public boolean equalsStatus(HttpStatus httpStatus) {
        return this == httpStatus;
    }
}
