package nextstep.jwp.db;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.Optional;
import nextstep.jwp.exception.UsernameConflictException;
import nextstep.jwp.model.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("InMemoryUserRepositoryTest")
class InMemoryUserRepositoryTest {

    @Test
    @DisplayName("user id가 0이라면 새로운 id가 정의된다.")
    void save() {
        // given
        User user = new User(0, "mungto", "password", "mungto@test.com");
        // when
        InMemoryUserRepository.save(user);
        Optional<User> result = InMemoryUserRepository.findByAccount("mungto");
        // then
        assertThat(result.orElseThrow().getId()).isNotZero();
    }

    @Test
    @DisplayName("user account가 중복이라면 에러가 발생한다.")
    void saveException() {
        User user = new User(0, "gugu", "password", "mungto@test.com");

        assertThatThrownBy(() -> InMemoryUserRepository.save(user))
            .isExactlyInstanceOf(UsernameConflictException.class);
    }

    @Test
    @DisplayName("account 가 존재하면 User 객체를 반환한다.")
    void findByAccount() {
        Optional<User> result = InMemoryUserRepository.findByAccount("gugu");
        // then
        assertThat(result).isPresent();
    }

    @Test
    @DisplayName("account 가 존재하지 않는다면 null 을 반환한다.")
    void findByAccountNull() {
        Optional<User> result = InMemoryUserRepository.findByAccount("abc");
        // then
        assertThat(result).isNotPresent();
    }
}