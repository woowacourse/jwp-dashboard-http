package nextstep.jwp.http;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

@DisplayName("HttpStatusTest")
class HttpStatusTest {

    @ParameterizedTest
    @CsvSource(value = {"OK:200", "FOUND:302", "BAD_REQUEST:400", "CONFLICT:409"}, delimiter = ':')
    @DisplayName("status 를 반환한다.")
    void getStatus(String type, int status) {
        HttpStatus httpStatus = HttpStatus.valueOf(type);

        assertThat(httpStatus.getStatus()).isEqualTo(status);
    }

    @ParameterizedTest
    @CsvSource(value = {"OK:OK", "FOUND:Found", "BAD_REQUEST:Bad Request",
        "CONFLICT:Conflict"}, delimiter = ':')
    @DisplayName("message 를 반환한다.")
    void getStatusMessage(String type, String message) {
        HttpStatus httpStatus = HttpStatus.valueOf(type);

        assertThat(httpStatus.getStatusMessage()).isEqualTo(message);
    }
}