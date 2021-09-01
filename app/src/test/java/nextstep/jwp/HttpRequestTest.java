package nextstep.jwp;

import nextstep.jwp.ui.request.HttpRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;

public class HttpRequestTest {
    private HttpRequest httpRequest;

    @Test
    @DisplayName("GET /login 요청대로 HttpRequest가 생성된다.")
    void getHttpRequest() {
        //given
        String request = String.join("\r\n",
                "GET /login?account=gugu&password=password HTTP/1.1 ",
                "Host: localhost:8080",
                "Connection: keep-alive",
                "Accept: */*",
                "",
                "");
        httpRequest = new HttpRequest(new ByteArrayInputStream(request.getBytes()));

        //when
        //then
        assertThat(httpRequest.getMethod()).isEqualTo("GET");
        assertThat(httpRequest.getPath()).isEqualTo("/login");
        assertThat(httpRequest.getParameter("account")).isEqualTo("gugu");
        assertThat(httpRequest.getParameter("password")).isEqualTo("password");
        assertThat(httpRequest.getQueryString()).isEqualTo("account=gugu&password=password");
        assertThat(httpRequest.getHeaders())
                .contains(entry("Host", "localhost:8080"), entry("Connection", "keep-alive"),
                        entry("Accept", "*/*"));
    }



    @Test
    @DisplayName("POST /login 요청대로 HttpRequest가 생성된다.")
    void postHttpRequest() {
        //given
        String request = String.join("\r\n",
                "POST /login HTTP/1.1 ",
                "Host: localhost:8080",
                "Connection: keep-alive",
                "Content-Length: 30",
                "Content-Type: application/x-www-form-urlencoded",
                "Accept: */*",
                "",
                "account=gugu&password=password");
        httpRequest = new HttpRequest(new ByteArrayInputStream(request.getBytes()));

        //when
        //then
        assertThat(httpRequest.getMethod()).isEqualTo("POST");
        assertThat(httpRequest.getPath()).isEqualTo("/login");
        assertThat(httpRequest.getParameter("account")).isEqualTo("gugu");
        assertThat(httpRequest.getParameter("password")).isEqualTo("password");
        assertThat(httpRequest.getHeaders())
                .contains(entry("Host", "localhost:8080"), entry("Connection", "keep-alive"),
                        entry("Content-Length", "30"), entry("Content-Type", "application/x-www-form-urlencoded"),
                        entry("Accept", "*/*"));
    }

    @Test
    @DisplayName("헤더의 쿠키 값이 파싱되어서 저장된다.")
    void parseCookie() {
        // given
        String request = String.join("\r\n",
                "GET /index.html HTTP/1.1",
                "Host: localhost:8080",
                "Connection: keep-alive",
                "Accept: */*",
                "Cookie: yummy_cookie=choco; tasty_cookie=strawberry; JSESSIONID=656cef62-e3c4-40bc-a8df-94732920ed46",
                "",
                "");
        httpRequest = new HttpRequest(new ByteArrayInputStream(request.getBytes()));

        //when
        //then
        assertThat(httpRequest.getMethod()).isEqualTo("GET");
        assertThat(httpRequest.getPath()).isEqualTo("/index.html");
        assertThat(httpRequest.getHeaders())
                .contains(entry("Host", "localhost:8080"), entry("Connection", "keep-alive"),
                        entry("Accept", "*/*"),
                        entry("Cookie", "yummy_cookie=choco; tasty_cookie=strawberry; JSESSIONID=656cef62-e3c4-40bc-a8df-94732920ed46"));
        assertThat(httpRequest.getCookie())
                .contains(entry("yummy_cookie", "choco"), entry("tasty_cookie", "strawberry"),
                        entry("JSESSIONID", "656cef62-e3c4-40bc-a8df-94732920ed46"));
    }
}
