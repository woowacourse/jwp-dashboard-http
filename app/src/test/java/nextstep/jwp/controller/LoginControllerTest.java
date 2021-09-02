package nextstep.jwp.controller;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import nextstep.jwp.framework.http.common.HttpBody;
import nextstep.jwp.framework.http.common.HttpHeaders;
import nextstep.jwp.framework.http.common.HttpMethod;
import nextstep.jwp.framework.http.common.HttpPath;
import nextstep.jwp.framework.http.common.ProtocolVersion;
import nextstep.jwp.framework.http.request.HttpRequest;
import nextstep.jwp.framework.http.request.HttpRequestLine;
import nextstep.jwp.framework.http.response.HttpResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class LoginControllerTest {

    @DisplayName("로그인 페이지를 방문한다.")
    @Test
    void getPage() throws IOException {
        // given
        LoginController loginController = new LoginController();
        HttpResponse response = new HttpResponse();

        // when
        loginController.doGet(new HttpRequest(
            new HttpRequestLine(HttpMethod.GET, new HttpPath("login.html"), new ProtocolVersion("HTTP/1.1")),
            new HttpHeaders("Content-Type: text/html;charset=utf-8 \r\nContent-Length: 12 \r\nCookie: io=H6Gs8jT7h07lTg94AAAA; JSESSIONID=acbd813f-eb5a-4f8d-87fe-b1737e0871a1"),
            new HttpBody()
        ), response);

        // then
        assertThat(response.body()).hasSize(2426);
    }

    @DisplayName("로그인을 요청한다.")
    @Test
    void postPage() throws IOException {
        // given
        LoginController loginController = new LoginController();
        HttpResponse response = new HttpResponse();

        // when
        loginController.doPost(new HttpRequest(
            new HttpRequestLine(HttpMethod.POST, new HttpPath("index.html"), new ProtocolVersion("HTTP/1.1")),
            new HttpHeaders("Content-Type: text/html;charset=utf-8 \r\nContent-Length: 12 \r\nCookie: io=H6Gs8jT7h07lTg94AAAA; JSESSIONID=acbd813f-eb5a-4f8d-87fe-b1737e0871a1"),
            new HttpBody("account=gugu&password=password&email=hkkang%40woowahan.com")
        ), response);

        // then
        assertThat(response.getBytes()).hasSize(2571);
    }
}
