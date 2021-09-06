package nextstep.jwp.service;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import nextstep.jwp.exception.UsernameConflictException;
import nextstep.jwp.model.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("RegisterServiceTest")
class RegisterServiceTest {

    private static final RegisterService REGISTER_SERVICE = new RegisterService();

    @Test
    @DisplayName("회원가입을 한다.")
    void save() {
        User user = new User(0L, "test1", "password", "");

        assertThatCode(() -> REGISTER_SERVICE.save(user)).doesNotThrowAnyException();
    }

    @Test
    @DisplayName("중복된 아이디로 가입하면 에러가 발생한다.")
    void saveException() {
        User user = new User(0L, "gugu", "password", "");

        assertThatThrownBy(() -> REGISTER_SERVICE.save(user))
            .isExactlyInstanceOf(UsernameConflictException.class);
    }
}