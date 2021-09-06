package nextstep.jwp.model;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import nextstep.jwp.exception.BadRequestException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("UserTest")
class UserTest {

    private static final User USER
        = new User(0L, "yumi", "password", "yumi@gmail.com");

    @Test
    @DisplayName("account 가 조건을 만족시키지 못한다면 유저생성시 에러가 발생한다.")
    void createUserExceptionWhenAccountValidateFail() {
        assertThatThrownBy(() -> new User("", "password"))
            .isExactlyInstanceOf(BadRequestException.class);
    }

    @Test
    @DisplayName("password 가 조건을 만족시키지 못한다면 유저생성시 에러가 발생한다.")
    void createUserExceptionWhenPasswordValidateFail() {
        assertThatThrownBy(() -> new User("account", ""))
            .isExactlyInstanceOf(BadRequestException.class);
    }

    @Test
    @DisplayName("패스워드가 일치한다면 true 를 반환한다.")
    void checkPasswordWhenTrue() {
        assertThat(USER.checkPassword("password")).isTrue();
    }

    @Test
    @DisplayName("패스워드가 일치한다면 false 를 반환한다.")
    void checkPasswordWhenFalse() {
        assertThat(USER.checkPassword("1234")).isFalse();
    }

    @Test
    @DisplayName("이메일을 가져온다.")
    void email() {
        assertThat(USER.getEmail()).isEqualTo("yumi@gmail.com");
    }
}