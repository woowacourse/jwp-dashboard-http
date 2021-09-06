package nextstep.jwp.utils;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("FileConverterTest")
class FileConverterTest {

    @Test
    @DisplayName("파일 변환 테스트")
    void fileToString() throws IOException {

        String result = FileConverter.fileToString("/index.html");

        assertThat(result).isNotNull().isNotEqualTo("");
    }
}