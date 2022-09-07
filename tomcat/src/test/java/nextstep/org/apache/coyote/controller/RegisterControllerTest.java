package nextstep.org.apache.coyote.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.io.IOException;
import java.net.URISyntaxException;
import org.apache.coyote.controller.Controller;
import org.apache.coyote.controller.RegisterController;
import org.apache.coyote.exception.DuplicateAccountRegisterException;
import org.apache.coyote.http11.request.HttpMethod;
import org.apache.coyote.http11.request.Request;
import org.apache.coyote.http11.response.Response;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import support.RequestFixture;
import support.StubSocket;

class RegisterControllerTest {

    private StubSocket stubSocket;
    private Controller registerController;

    @BeforeEach
    void setUp() {
        registerController = new RegisterController();
    }

    @AfterEach
    void tearDown() throws IOException {
        stubSocket.close();
    }

    @Test
    void isRunnable() throws IOException {
        // given
        final String requestString = RequestFixture.create(HttpMethod.GET, "/register", "");
        stubSocket = new StubSocket(requestString);
        final Request request = Request.of(stubSocket.getInputStream());

        // when
        final boolean actual = registerController.isRunnable(request);

        // then
        assertThat(actual).isTrue();
    }

    @Test
    void registerSuccess() throws IOException, URISyntaxException {
        // given
        final String requestString = RequestFixture.create(HttpMethod.POST, "/register", "account=accountName&password=password&email=gugu@naver.com");
        stubSocket = new StubSocket(requestString);
        final Request request = Request.of(stubSocket.getInputStream());
        final Response response = Response.of(stubSocket.getOutputStream());

        // when
        registerController.run(request, response);

        // then
        assertAll(
                () -> assertThat(stubSocket.output()).contains("FOUND"),
                () -> assertThat(stubSocket.output()).contains("/index.html")
        );
    }

    @Test
    void registerFailure() throws IOException, URISyntaxException {
        final String requestString = RequestFixture.create(HttpMethod.POST, "/register", "account=gugu&password=wrongPassword");
        stubSocket = new StubSocket(requestString);
        final Request request = Request.of(stubSocket.getInputStream());
        final Response response = Response.of(stubSocket.getOutputStream());

        // when
        assertThatThrownBy(() ->         registerController.run(request, response))
                .isExactlyInstanceOf(DuplicateAccountRegisterException.class);
    }
}
