package nextstep.jwp;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class RequestHandlerTest {

    private static final String NEW_LINE = "\r\n";
    private static final String DEFAULT_BODY = "account=gugu&password=password&email=hkkang%40woowahan.com";
    private static final String TEXT_HTML = "text/html";
    private static final String INDEX_HTML = "index.html";

    @Test
    @DisplayName("index 테스트")
    void index() throws IOException {
        final String httpRequest = MessageFactory.createGetRequest(INDEX_HTML, "*/*");

        final MockSocket socket = getMockSocket(httpRequest);

        String expected = MessageFactory.createResponseOK(INDEX_HTML, TEXT_HTML);
        testCheck(socket, expected);
    }

    @Test
    @DisplayName("login Get 테스트")
    void loginGet() throws IOException {
        final String httpRequest = MessageFactory.createGetRequest("login", "*/*");

        final MockSocket socket = getMockSocket(httpRequest);

        String expected = MessageFactory.createResponseOK("login.html", TEXT_HTML);
        testCheck(socket, expected);
    }

    @Test
    @DisplayName("login Post 테스트")
    void loginPost() {
        final String httpRequest = MessageFactory.createPostRequest("login", DEFAULT_BODY);

        final MockSocket socket = getMockSocket(httpRequest);

        String expected = MessageFactory.createResponseFound(INDEX_HTML);
        testCheck(socket, expected);
    }

    @Test
    @DisplayName("register Get 테스트")
    void registerGet() throws IOException {
        final String httpRequest = MessageFactory.createGetRequest("register", "*/*");

        final MockSocket socket = getMockSocket(httpRequest);

        String expected = MessageFactory.createResponseOK("register.html", TEXT_HTML);
        testCheck(socket, expected);
    }

    @Test
    @DisplayName("register Post 테스트")
    void registerPost() {
        String body = "account=test&password=password&email=mungto%40woowahan.com";
        final String httpRequest = MessageFactory.createPostRequest("register", body);

        final MockSocket socket = getMockSocket(httpRequest);

        String expected = MessageFactory.createResponseFound(INDEX_HTML);

        testCheck(socket, expected);
    }

    @Test
    @DisplayName("css 테스트")
    void css() throws IOException {
        final String styleCss = "css/styles.css";
        final String type = "text/css";
        final String httpRequest = MessageFactory.createGetRequest(styleCss, type);

        final MockSocket socket = getMockSocket(httpRequest);

        String expected = MessageFactory.createResponseOK(styleCss, type);
        testCheck(socket, expected);
    }

    @Test
    @DisplayName("javascript 테스트")
    void javascript() throws IOException {
        final String scripts = "js/scripts.js";
        final String type = "application/javascript";
        final String httpRequest = MessageFactory.createGetRequest(scripts, type);

        final MockSocket socket = getMockSocket(httpRequest);

        String expected = MessageFactory.createResponseOK(scripts, type);
        testCheck(socket, expected);
    }

    @Test
    @DisplayName("잘못된 요청을 보내면 400이 반환된다.")
    void badRequest() {
        final String httpRequest = "";

        final MockSocket socket = getMockSocket(httpRequest);

        String expected = String.join(NEW_LINE, "HTTP/1.1 400 Bad Request",
            "Content-Type: application/json",
            "",
            "{\"message\": \"잘못된 request 입니다.\"}");
        testCheck(socket, expected);
    }

    @Test
    @DisplayName("요청처리를 하지 않은 요청을 하면 404가 반환된다.")
    void notFound() {
        final String httpRequest = MessageFactory.createGetRequest("getError", "*/*");

        final MockSocket socket = getMockSocket(httpRequest);

        String expected = MessageFactory.createResponseFound("404.html");
        testCheck(socket, expected);
    }

    @Test
    @DisplayName("존재하는 유저로 회원가입 요청을 하면 409가 반환된다.")
    void conflict() {
        final String httpRequest = MessageFactory.createPostRequest("register", DEFAULT_BODY);

        final MockSocket socket = getMockSocket(httpRequest);

        String expected = String.join(NEW_LINE,
            "HTTP/1.1 409 Conflict",
            "Content-Type: application/json",
            "",
            "{\"message\": \"이미 존재하는 아이디 입니다.\"}"
        );
        testCheck(socket, expected);
    }

    @Test
    @DisplayName("login 실패시 401로 리다이랙트한다.")
    void unAuthorized() {
        String body = "account=fail&password=password&email=hkkang%40woowahan.com";
        final String httpRequest = MessageFactory.createPostRequest("login", body);

        final MockSocket socket = getMockSocket(httpRequest);

        String expected = MessageFactory.createResponseFound("401.html");
        testCheck(socket, expected);
    }

    private MockSocket getMockSocket(String httpRequest) {
        final MockSocket socket = new MockSocket(httpRequest);
        final RequestHandler requestHandler = new RequestHandler(socket);

        requestHandler.run();
        return socket;
    }

    private void testCheck(MockSocket socket, String expected) {
        for (String message : expected.split(NEW_LINE)) {
            assertThat(socket.output()).contains(message);
        }
    }
}
