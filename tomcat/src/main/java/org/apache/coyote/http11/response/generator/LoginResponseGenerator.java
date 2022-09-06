package org.apache.coyote.http11.response.generator;

import static org.apache.coyote.http11.request.HttpMethod.POST;

import java.util.Optional;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoginResponseGenerator implements ResponseGenerator {

    private static final Logger log = LoggerFactory.getLogger(LoginResponseGenerator.class);

    private static final String LOGIN_REQUEST = "/login";
    private static final String ACCOUNT_KEY = "account";
    private static final String PASSWORD_KEY = "password";
    private static final String LOGIN_SUCCESS_REDIRECT_URI = "http://localhost:8080/index.html";
    private static final String LOGIN_FAILURE_REDIRECT_URI = "http://localhost:8080/401.html";

    @Override
    public boolean isSuitable(HttpRequest httpRequest) {
        return httpRequest.hasRequestPathOf(LOGIN_REQUEST) && httpRequest.hasHttpMethodOf(POST);
    }

    @Override
    public HttpResponse generate(HttpRequest httpRequest) {
        String account = httpRequest.getParamValueOf(ACCOUNT_KEY);
        String password = httpRequest.getParamValueOf(PASSWORD_KEY);

        Optional<User> user = InMemoryUserRepository.findByAccount(account);
        if (user.isPresent()) {
            return responseAfterPasswordCheck(user.get(), password);
        }
        return HttpResponse.found(LOGIN_FAILURE_REDIRECT_URI);
    }

    private HttpResponse responseAfterPasswordCheck(User user, String password) {
        if (user.checkPassword(password)) {
            log.info(user.toString());
            return HttpResponse.found(LOGIN_SUCCESS_REDIRECT_URI);
        }
        return HttpResponse.found(LOGIN_FAILURE_REDIRECT_URI);
    }
}
