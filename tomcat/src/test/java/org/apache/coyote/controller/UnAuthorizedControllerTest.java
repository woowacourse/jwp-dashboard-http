package org.apache.coyote.controller;

import org.apache.coyote.httprequest.HttpRequest;
import org.apache.coyote.httpresponse.HttpResponse;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@SuppressWarnings("NonAsciiCharacters")
class UnAuthorizedControllerTest extends ControllerTestSupport {

    @Test
    void 에러코드_401_페이지를_보여준다() {
        // given
        final String input = String.join("\r\n",
                "GET /401.html HTTP/1.1",
                "Host: localhost:8080",
                "Connection: keep-alive",
                "Accept: */*");
        final HttpRequest httpRequest = super.makeHttpRequest(input);
        final UnAuthorizedController unAuthorizedController = new UnAuthorizedController();

        // when
        final HttpResponse httpResponse = unAuthorizedController.service(httpRequest);
        final String actual = super.bytesToText(httpResponse.getBytes());
        final Set<String> expectedHeaders = Set.of(
                "HTTP/1.1 401 Unauthorized",
                "Content-Type: text/html;charset=utf-8"
        );

        // then
        assertThat(actual).contains(expectedHeaders);
    }
}
