package org.apache.coyote.common.request;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Map;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("Request 는 ")
class RequestTest {

    @DisplayName("요청 스트링을 통해 요청 메시지의 정보를 파싱하고 저장한다.")
    @Test
    void saveRequest() {
        final Request request = new Request("GET /example?key=value&key2=value2 HTTP/1.1");

        assertAll(
                () -> assertThat(request).extracting("queryString")
                        .isEqualTo(Map.of("key", "value",
                                "key2", "value2")),
                () -> assertThat(request).extracting("method")
                        .isEqualTo(RequestMethod.GET),
                () -> assertThat(request).extracting("path")
                        .isEqualTo("/example")
        );
    }
}
