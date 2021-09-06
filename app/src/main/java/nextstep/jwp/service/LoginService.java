package nextstep.jwp.service;

import java.util.Objects;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.exception.UnauthorizedException;
import nextstep.jwp.http.HttpSession;
import nextstep.jwp.http.HttpSessions;
import nextstep.jwp.http.Request;
import nextstep.jwp.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoginService {

    private static final Logger LOG = LoggerFactory.getLogger(LoginService.class);

    public User login(User user) {
        User expectedUser = InMemoryUserRepository
            .findByAccount(user.getAccount())
            .orElseThrow(UnauthorizedException::new);

        if (expectedUser.checkPassword(user.getPassword())) {
            LOG.info("{} login success", user.getAccount());
            return user;
        }
        throw new UnauthorizedException();
    }

    public boolean isLogin(Request request) {
        HttpSession session = request.getHttpSession();
        return Objects.nonNull(HttpSessions.getSession(session.getId()));
    }
}
