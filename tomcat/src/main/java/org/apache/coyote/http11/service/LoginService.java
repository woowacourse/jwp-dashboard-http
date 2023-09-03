package org.apache.coyote.http11.service;

import java.util.Optional;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoginService {

    private static final Logger log = LoggerFactory.getLogger(LoginService.class);

    public boolean checkUser(String account, String password) {
        Optional<User> user = InMemoryUserRepository.findByAccount(account);
        if (user.isPresent()) {
            return checkPassword(user.get(), password);
        }
        return false;
    }

    private boolean checkPassword(User user, String password) {
        boolean validLogin = user.checkPassword(password);
        if (validLogin) {
            log.info("{}", user);
        }
        return validLogin;
    }
}
