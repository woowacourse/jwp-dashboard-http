package nextstep.jwp.controller;

import nextstep.jwp.domain.Uri;
import nextstep.jwp.domain.request.HttpRequest;
import nextstep.jwp.domain.request.RequestBody;
import nextstep.jwp.domain.request.RequestLine;
import nextstep.jwp.domain.response.HttpResponse;
import nextstep.jwp.domain.response.HttpStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class LoginControllerTest {

    private static RequestLine getLoginRequestLine = new RequestLine("GET", Uri.of("/login"), "HTTP/1.1");
    private static RequestLine postLoginRequestLine = new RequestLine("POST", Uri.of("/login"), "HTTP/1.1");

    @DisplayName("Get 요청 테스트 - login.html을 반환한다.")
    @Test
    void doGet() throws IOException {
        //given
        HttpRequest request = HttpRequest.of(getLoginRequestLine, null, null);
        //when
        LoginController controller = new LoginController();
        HttpResponse httpResponse = controller.doGet(request);
        //then
        assertThat(httpResponse.getHttpStatus()).isEqualTo(HttpStatus.OK);
        assertThat(httpResponse.getBody().length()).isEqualTo(3717);
    }

    @DisplayName("POST 요청 테스트")
    @Test
    void doPost() throws IOException {
        //given
        Map<String, String> bodyParamMap = new HashMap<>();
        bodyParamMap.put("account", "gugu");
        bodyParamMap.put("password", "password");

        RequestBody requestBody = RequestBody.of(bodyParamMap);
        HttpRequest request = HttpRequest.of(postLoginRequestLine, null, requestBody);
        //when
        LoginController controller = new LoginController();
        HttpResponse httpResponse = controller.doPost(request);
        //then
        assertThat(httpResponse.getHttpStatus()).isEqualTo(HttpStatus.FOUND);
    }
}