package org.apache.coyote.http11.message;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;

@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class HeadersTest {

    @Test
    void HTTP_헤더_형식을_통해_생성할_수_있다() {
        // given
        Map<String, String> rawHeaders = Map.of(
                "Content-Type", "content-type",
                "Content-Length", "content-length"
        );

        // when
        Headers headers = new Headers(rawHeaders);

        // then
        assertThat(headers.get("Content-Type")).isEqualTo("content-type");
    }

    @Test
    void 쿠키를_조회할_수_있다() {
        // given
        Map<String, String> rawHeaders = Map.of(
                "Content-Type", "content-type",
                "Content-Length", "content-length",
                "Cookie", "name=teo; age=25"
        );

        // when
        Headers headers = new Headers(rawHeaders);
        Cookie cookie = headers.getCookie();

        // then
        assertThat(cookie.getValue("name")).isEqualTo("teo");
        assertThat(cookie.getValue("age")).isEqualTo("25");
    }

    @Test
    void 문자열로_변환_시_HTTP_헤더_형식을_따른다() {
        // given
        Map<String, String> rawHeaders = Map.of(
                "Content-Type", "content-type",
                "Content-Length", "content-length"
        );

        // when
        Headers headers = new Headers(rawHeaders);

        String contentType = "Content-Type: content-type ";
        String contentLength = "Content-Length: content-length ";

        // then
        assertThat(headers.toString()).contains(contentLength);
        assertThat(headers.toString()).contains(contentType);
    }
}