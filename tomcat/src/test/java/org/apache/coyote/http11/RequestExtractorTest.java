package org.apache.coyote.http11;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import org.apache.coyote.http11.message.HttpMethod;
import org.apache.coyote.http11.message.HttpVersion;
import org.apache.coyote.http11.message.request.Request;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import support.StubSocket;

@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class RequestExtractorTest {

    @Test
    void 입력_스트림으로부터_Request를_파싱할_수_있다() throws IOException {
        // given
        Socket socket = new StubSocket("GET / HTTP/1.1\r\nHost: localhost:8080\r\n\r\n");

        // when
        InputStream inputStream = socket.getInputStream();
        Request request = RequestExtractor.extract(inputStream);

        // then
        assertThat(request.getHttpVersion()).isEqualTo(HttpVersion.V_1_1);
        assertThat(request.getMethod()).isEqualTo(HttpMethod.GET);
        assertThat(request.getHeaders().get("Host")).isEqualTo("localhost:8080");
    }
}
