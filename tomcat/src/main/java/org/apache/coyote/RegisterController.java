package org.apache.coyote;

import java.io.IOException;
import java.net.URISyntaxException;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import org.apache.coyote.exception.DuplicateAccountRegisterException;
import org.apache.coyote.http11.Request;
import org.apache.coyote.http11.RequestBody;
import org.apache.coyote.http11.Response;

public class RegisterController implements Controller {
    @Override
    public boolean isRunnable(final Request request) {
        return request.hasPath("/register");
    }

    @Override
    public void run(final Request request, final Response response) throws IOException, URISyntaxException {
        if (request.getMethod().equals(HttpMethod.GET)) {
            response.write(HttpStatus.OK, "/register.html");
            return;
        }
        if (request.getMethod().equals(HttpMethod.POST)) {
            register(request.getBody());
            response.addHeader("Location", "/index.html");
            response.write(HttpStatus.FOUND);
        }
    }

    private void register(final RequestBody body) {
        final String account = body.get("account");
        if (InMemoryUserRepository.findByAccount(account).isPresent()) {
            throw new DuplicateAccountRegisterException();
        }
        final User user = new User(account, body.get("password"), body.get("email"));
        InMemoryUserRepository.save(user);
    }
}
