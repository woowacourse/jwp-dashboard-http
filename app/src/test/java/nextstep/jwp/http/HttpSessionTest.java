package nextstep.jwp.http;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.UUID;
import nextstep.jwp.model.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("HttpSessionTest")
class HttpSessionTest {

    private final UUID uuid = UUID.randomUUID();
    private final HttpSession httpSession = new HttpSession(uuid.toString());

    @Test
    @DisplayName("생성시 등록한 아이디를 가져온다.")
    void getId() {
        assertThat(httpSession.getId()).isEqualTo(uuid.toString());
    }

    @Test
    @DisplayName("요소를 저장하고 가져온다.")
    void setAndGetAttribute() {
        User user = new User("user", "password");
        httpSession.setAttribute("user", user);

        assertThat(httpSession.getAttribute("user")).isSameAs(user);
    }
}