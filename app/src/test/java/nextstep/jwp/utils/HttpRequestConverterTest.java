package nextstep.jwp.utils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import nextstep.jwp.MockSocket;
import nextstep.jwp.exception.BadRequestException;
import nextstep.jwp.http.HttpMethod;
import nextstep.jwp.http.Request;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("HttpRequestConverterTest")
class HttpRequestConverterTest {

    @Test
    @DisplayName("BufferedReader 를 이용하여 Request 를 만든다.")
    void createdRequest() throws IOException {
        // given
        BufferedReader bufferedReader = createBufferedReader(createGetRequest());
        // when
        Request request = HttpRequestConverter.createdRequest(bufferedReader);
        // then
        assertThat(request.getMethod()).isEqualTo(HttpMethod.GET);
        assertThat(request.getUri()).isEqualTo("/index.html");
        bufferedReader.close();
    }

    @Test
    @DisplayName("잘못된 요청이 들어오면 에러가 발생한다.")
    void createdRequestException() throws IOException {
        BufferedReader bufferedReader = createBufferedReader("");
        assertThatThrownBy(() -> HttpRequestConverter.createdRequest(bufferedReader))
            .isExactlyInstanceOf(BadRequestException.class);
    }

    private BufferedReader createBufferedReader(String message) throws IOException {
        final MockSocket connection = new MockSocket(message);
        final InputStream inputStream = connection.getInputStream();
        final BufferedReader bufferedReader = new BufferedReader(
            new InputStreamReader(inputStream));
        inputStream.close();
        return bufferedReader;
    }

    private String createGetRequest() {
        return String.join("\r\n",
            "GET /index.html HTTP/1.1",
            "Host: http://localhost:8080",
            "Connection: keep-alive",
            "Accept: text/html",
            "",
            "");
    }
}