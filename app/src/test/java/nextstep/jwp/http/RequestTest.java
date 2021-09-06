package nextstep.jwp.http;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("RequestTest")
class RequestTest {

    private static final String HTTP_URI = "/index.html";
    private static final String HTTP_VERSION = "HTTP/1.1";

    @Test
    @DisplayName("uri 이 같다면 true 를 반환한다.")
    void isUriMatchWhenTrue() {
        Request request = getRequest(HTTP_URI);
        assertThat(request.isUriMatch(HTTP_URI)).isTrue();
    }

    @Test
    @DisplayName("uri 이 다르다면 false 를 반환한다.")
    void isUriMatchWhenFalse() {
        Request request = getRequest(HTTP_URI);
        assertThat(request.isUriMatch("/error.html")).isFalse();
    }

    @Test
    @DisplayName("uri 가 파일을 나타내면 true 를 반환한다.")
    void isUriFileWhenTrue() {
        Request request = getRequest(HTTP_URI);
        assertThat(request.isUriFile()).isTrue();
    }

    @Test
    @DisplayName("uri 가 파일을 나타내면 false 를 반환한다.")
    void isUriFileWhenFalse() {
        Request request = getRequest("/login");
        assertThat(request.isUriFile()).isFalse();
    }

    @Test
    @DisplayName("HTTP method 가 일치하면 true 를 반환한다.")
    void isEqualsHttpMethodWhenTrue() {
        Request request = getRequest(HTTP_URI);
        assertThat(request.isEqualsHttpMethod(HttpMethod.GET)).isTrue();
    }

    @Test
    @DisplayName("HTTP method 가 일치하면 False 를 반환한다.")
    void isEqualsHttpMethodWhenFalse() {
        Request request = getRequest(HTTP_URI);
        assertThat(request.isEqualsHttpMethod(HttpMethod.POST)).isFalse();
    }

    @Test
    @DisplayName("HTTP 버전을 가져온다.")
    void getHttpVersion() {
        Request request = getRequest(HTTP_URI);
        assertThat(request.getHttpVersion()).isEqualTo(HTTP_VERSION);
    }

    @Test
    @DisplayName("쿠키 데이터를 가져온다.")
    void getCookie() {
        Request request = getRequest(HTTP_URI);
        assertThat(request.getCookie("yumi")).isEqualTo("choco");
    }

    @Test
    @DisplayName("Header 에서 값을 가져온다.")
    void getParameter() {
        Request request = getRequest(HTTP_URI);
        assertThat(request.getParameter("content-Type")).isEqualTo("text/html");
    }

    private Request getRequest(String uri) {
        UUID uuid = UUID.randomUUID();
        Map<String, String> cookie = new HashMap<>();
        cookie.put("yumi", "choco");
        HttpCookie httpCookie = new HttpCookie(cookie);
        Map<String, String> header = new HashMap<>();
        header.put("content-Type", "text/html");
        RequestHeader requestHeader = new RequestHeader(header);

        return new Request.Builder()
            .method(HttpMethod.GET)
            .uri(uri)
            .httpVersion(HTTP_VERSION)
            .header(requestHeader)
            .body(new HashMap<>())
            .cookie(httpCookie)
            .httpSession(new HttpSession(uuid.toString()))
            .build();
    }
}