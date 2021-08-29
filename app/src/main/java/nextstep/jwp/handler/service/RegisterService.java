package nextstep.jwp.handler.service;

import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.handler.dto.RegisterRequest;
import nextstep.jwp.handler.exception.UserException;
import nextstep.jwp.model.User;

public class RegisterService {

    public void register(RegisterRequest registerRequest) {
        User user = registerRequest.toEntity();

        checkIsDuplicatedAccount(user.getAccount());
        InMemoryUserRepository.save(user);
    }

    private void checkIsDuplicatedAccount(String account) {
        InMemoryUserRepository.findByAccount(account).ifPresent(user -> {
            throw new UserException();
        });
    }
}
