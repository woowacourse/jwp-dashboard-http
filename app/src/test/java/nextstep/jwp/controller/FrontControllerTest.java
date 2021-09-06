package nextstep.jwp.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.io.IOException;
import java.util.HashMap;
import nextstep.jwp.exception.MethodNotAllowedException;
import nextstep.jwp.exception.NotFoundException;
import nextstep.jwp.http.HttpMethod;
import nextstep.jwp.http.Request;
import nextstep.jwp.http.Response;
import nextstep.jwp.utils.FileConverter;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("FrontControllerTest")
class FrontControllerTest {

    private static final FrontController FRONT_CONTROLLER = new FrontController();

    @Test
    @DisplayName("페이지 반환 테스트")
    void doGet() throws IOException {
        // given
        Request request = createRequest("/index.html", HttpMethod.GET);
        Response response = new Response();
        Response expectedResponse = new Response();
        expectedResponse.set200OK(request, FileConverter.fileToString("/index.html"));

        // when
        FRONT_CONTROLLER.doGet(request, response);

        // then
        assertThat(response.message()).isEqualTo(expectedResponse.message());
    }

    @Test
    @DisplayName("없는 파일 요청시 에러가 발생한다.")
    void doGeException() {
        Request request = createRequest("/testFail.html", HttpMethod.GET);
        Response response = new Response();

        assertThatThrownBy(() -> FRONT_CONTROLLER.doGet(request, response))
            .isExactlyInstanceOf(NotFoundException.class);
    }

    @Test
    @DisplayName("post 시 에러가 발생한다.")
    void doPost() {
        Request request = createRequest("/index.html", HttpMethod.POST);
        Response response = new Response();

        assertThatThrownBy(() -> FRONT_CONTROLLER.doPost(request, response))
            .isExactlyInstanceOf(MethodNotAllowedException.class);
    }

    private Request createRequest(String uri, HttpMethod httpMethod) {
        return new Request.Builder()
            .method(httpMethod)
            .uri(uri)
            .header(new HashMap<>())
            .body(new HashMap<>())
            .httpVersion("HTTP/1.1")
            .build();
    }
}