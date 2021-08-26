package nextstep.jwp.utils;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class StringUtilsTest {

    @DisplayName("문자열을 구분자를 기준으로 리스트로 분리한다.")
    @Test
    void splitWithSeparator() {
        String s = "aaaa\r\nb\r\ncc\r\n";
        List<String> pieces = StringUtils.splitWithSeparator(s, "\r\n");
        assertThat(pieces).containsExactly("aaaa", "b", "cc");
    }

    @DisplayName("문자열을 첫 구분자를 기준으로 두 조각으로 분리한다.")
    @Test
    void splitTwoPiecesWithSeparator() {
        String s = "aaaa\r\nb\r\ncc\r\n";
        List<String> pieces = StringUtils.splitTwoPiecesWithSeparator(s, "\r\n");
        assertThat(pieces).containsExactly("aaaa", "b\r\ncc\r\n");
    }

    @DisplayName("BufferedReader를 문자열로 변환한다.")
    @Test
    void convertToString() throws IOException {
        String s = "middle\r\n" +
                "bear\r\n" +
                "small\r\n" +
                "bear\r\n";

        byte[] bytes = s.getBytes();
        InputStreamReader inputStreamReader = new InputStreamReader(new ByteArrayInputStream(bytes));
        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

        assertThat(StringUtils.convertToString(bufferedReader)).isEqualTo(s);
    }
}