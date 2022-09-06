package org.apache.coyote.http11;

import java.util.Arrays;

public enum HttpMethod {

    GET;

    public static HttpMethod of(final String value) {
        return Arrays.stream(values())
                .filter(httpMethod -> httpMethod.name().equals(value))
                .findAny()
                .orElseThrow(() -> new RuntimeException("지원하지 않는 http 메서드입니다."));
    }
}
