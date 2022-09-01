package org.apache.coyote.support;

import java.util.Optional;
import nextstep.jwp.model.User;
import org.apache.exception.HttpException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HttpRequestHandler {

    private static final Logger log = LoggerFactory.getLogger(HttpRequestHandler.class);

    private final ResourceView resourceView = new ResourceView();

    public String handle(HttpRequest request) {
        try {
            if (request.isGet()) {
                logAccount(request);
                return resourceView.findStaticResource(request.getUri());
            }
            throw new UnsupportedOperationException("Not implemented");
        } catch (HttpException e) {
            return resourceView.findErrorPage(e);
        }
    }

    private void logAccount(HttpRequest request) {
        Optional<User> account = request.checkLoginAccount();
        if (account.isEmpty()) {
            return;
        }
        log.info(account.get().toString());
    }
}
