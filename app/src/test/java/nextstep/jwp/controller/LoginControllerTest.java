package nextstep.jwp.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import nextstep.jwp.MessageFactory;
import nextstep.jwp.exception.UnauthorizedException;
import nextstep.jwp.http.HttpMethod;
import nextstep.jwp.http.HttpSession;
import nextstep.jwp.http.HttpSessions;
import nextstep.jwp.http.Request;
import nextstep.jwp.http.Request.Builder;
import nextstep.jwp.http.Response;
import nextstep.jwp.service.LoginService;
import nextstep.jwp.utils.FileConverter;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("LoginControllerTest")
class LoginControllerTest {

    private static final LoginController LOGIN_CONTROLLER = new LoginController(new LoginService());
    private static final String NEW_LINE = "\r\n";

    static {
        HttpSessions.add(MessageFactory.LOGIN_UUID.toString(),
            new HttpSession(MessageFactory.LOGIN_UUID.toString()));
    }

    @Test
    @DisplayName("login page 반환 테스트")
    void doGet() throws IOException {
        // given
        Request request = createRequest(new HashMap<>(), HttpMethod.GET).build();
        Response response = new Response();

        // when
        LOGIN_CONTROLLER.doGet(request, response);

        // then
        assertThat(response.message()).isEqualTo(createResponseOK());
    }

    @Test
    @DisplayName("로그인을 성공하면 index.html 을 반환한다.")
    void doPost() {
        // given
        String expected = MessageFactory.createResponseFound("index.html");

        Response response = getPostSuccessResponse();

        // then
        for (String message : expected.split(NEW_LINE)) {
            assertThat(response.message()).contains(message);
        }
    }

    @Test
    @DisplayName("로그인이 된 상태에서 페이지를 요청하면 index.html 로 리다이렉트 한다.")
    void doGetCookie() throws IOException {
        // given
        Map<String, String> header = new HashMap<>();
        header.put("Cookie", "JSESSIONID=" + MessageFactory.LOGIN_UUID);
        Request request = createRequest(new HashMap<>(), HttpMethod.GET)
            .header(header)
            .httpSession(HttpSessions.getSession(MessageFactory.LOGIN_UUID.toString()))
            .build();
        Response response = new Response();

        // when
        LOGIN_CONTROLLER.doGet(request, response);

        // then
        assertThat(response.message())
            .isEqualTo(MessageFactory.createResponseFound("index.html"));
    }

    @Test
    @DisplayName("로그인에 실패하면 에러가 발생한다. ")
    void doPostException() {
        Map<String, String> body = new HashMap<>();
        body.put("account", "error");
        body.put("password", "password");

        Request request = createRequest(body, HttpMethod.POST).build();
        Response response = new Response();

        assertThatThrownBy(() -> LOGIN_CONTROLLER.doPost(request, response))
            .isExactlyInstanceOf(UnauthorizedException.class);
    }

    private Builder createRequest(Map<String, String> body, HttpMethod httpMethod) {
        return new Request.Builder()
            .uri("/login")
            .header(new HashMap<>())
            .httpVersion("HTTP/1.1")
            .method(httpMethod)
            .body(body)
            .httpSession(new HttpSession(UUID.randomUUID().toString()));
    }

    private String createResponseOK() throws IOException {
        final String responseBody = FileConverter.fileToString("/login.html");

        return String.join(NEW_LINE, "HTTP/1.1 200 OK",
            "Content-Type: text/html;charset=utf-8",
            "Content-Length: " + responseBody.getBytes().length,
            "",
            responseBody);
    }

    private Response getPostSuccessResponse() {
        Map<String, String> body = new HashMap<>();
        body.put("account", "gugu");
        body.put("password", "password");

        Request request = createRequest(body, HttpMethod.POST).build();
        Response response = new Response();

        LOGIN_CONTROLLER.doPost(request, response);
        return response;
    }
}
