package org.apache.coyote.http11;

public enum HttpStatus {

    OK(200),
    ;

    private final int code;

    HttpStatus(final int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }

}
