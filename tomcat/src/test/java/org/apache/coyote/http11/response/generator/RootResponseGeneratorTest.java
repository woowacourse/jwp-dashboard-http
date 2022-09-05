package org.apache.coyote.http11.response.generator;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class RootResponseGeneratorTest {

    private static final RootResponseGenerator ROOT_RESPONSE_GENERATOR = new RootResponseGenerator();

    @DisplayName("처리할 수 있는 HttpRequest인지 반환한다.")
    @ParameterizedTest
    @CsvSource({"GET /index.html HTTP/1.1, false", "GET / HTTP/1.1, true"})
    void isSuitable(String request, boolean expected) {
        boolean actual = ROOT_RESPONSE_GENERATOR.isSuitable(HttpRequest.from(request));

        assertThat(actual).isEqualTo(expected);
    }

    @DisplayName("HttpResponse를 반환한다.")
    @Test
    void generate() throws IOException {
        HttpRequest httpRequest = HttpRequest.from("GET / HTTP/1.1");

        HttpResponse httpResponse = ROOT_RESPONSE_GENERATOR.generate(httpRequest);

        assertThat(httpResponse.getResponse())
                .contains("200 OK")
                .contains("Hello world!");
    }
}