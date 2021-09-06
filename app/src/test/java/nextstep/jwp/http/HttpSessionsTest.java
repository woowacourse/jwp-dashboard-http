package nextstep.jwp.http;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("HttpSessionsTest")
class HttpSessionsTest {

    @Test
    @DisplayName("아이템을 추가하고 가져온다")
    void addAndGet() {
        UUID uuid = UUID.randomUUID();
        HttpSession httpSession = new HttpSession(uuid.toString());

        HttpSessions.add(uuid.toString(), httpSession);

        assertThat(HttpSessions.getSession(uuid.toString()))
            .isNotNull();
    }
}