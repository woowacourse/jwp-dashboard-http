package nextstep.jwp.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.UUID;
import nextstep.jwp.exception.UnauthorizedException;
import nextstep.jwp.http.HttpSession;
import nextstep.jwp.http.HttpSessions;
import nextstep.jwp.http.Request;
import nextstep.jwp.model.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("LoginServiceTest")
class LoginServiceTest {

    private static final LoginService LOGIN_SERVICE = new LoginService();

    @Test
    @DisplayName("로그인 성공여부를 판별한다.")
    void loginValidate() {
        User user = new User(0L, "gugu", "password", "");

        assertThatCode(() -> LOGIN_SERVICE.login(user)).doesNotThrowAnyException();
    }

    @Test
    @DisplayName("아이디가 존재하지 않다면 에러가 발생한다.")
    void loginValidateIdException() {
        User user = new User(0L, "error", "password", "");

        assertThatThrownBy(() -> LOGIN_SERVICE.login(user))
            .isExactlyInstanceOf(UnauthorizedException.class);
    }

    @Test
    @DisplayName("아이디가 존재하지 않다면 에러가 발생한다.")
    void loginValidatePasswordException() {
        User user = new User(0L, "gugu", "error", "");

        assertThatThrownBy(() -> LOGIN_SERVICE.login(user))
            .isExactlyInstanceOf(UnauthorizedException.class);
    }

    @Test
    @DisplayName("로그인이 되어있는지 확인있다면 true 를 반환한다..")
    void isLoginTrue() {
        // given
        String uuid = UUID.randomUUID().toString();
        HttpSession httpSession = new HttpSession(uuid);

        // when
        HttpSessions.add(uuid, httpSession);
        Request request = new Request.Builder()
            .httpSession(httpSession)
            .build();

        // then
        assertThat(LOGIN_SERVICE.isLogin(request)).isTrue();
    }

    @Test
    @DisplayName("로그인이 되어있는지 확인있다면 false 를 반환한다.")
    void isLoginFalse() {
        // given
        String uuid = UUID.randomUUID().toString();
        HttpSession httpSession = new HttpSession(uuid);

        // when
        Request request = new Request.Builder()
            .httpSession(httpSession)
            .build();

        // then
        assertThat(LOGIN_SERVICE.isLogin(request)).isFalse();
    }
}