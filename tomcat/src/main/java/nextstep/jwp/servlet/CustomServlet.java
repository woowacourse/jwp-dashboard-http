package nextstep.jwp.servlet;

import nextstep.jwp.controller.AuthController;
import nextstep.jwp.controller.ErrorController;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.service.UserService;
import nextstep.jwp.support.ResourceRegistry;
import org.apache.coyote.servlet.Servlet;
import org.apache.coyote.support.HttpException;
import org.apache.coyote.servlet.request.HttpRequest;
import org.apache.coyote.servlet.response.HttpResponse;

public class CustomServlet implements Servlet {

    private final HandlerMapper handlerMapper;
    private final ErrorController errorController;

    public CustomServlet() {
        final var resourceRegistry = new ResourceRegistry();
        final var authController = new AuthController(new UserService(new InMemoryUserRepository()));
        this.handlerMapper = new HandlerMapper(authController, resourceRegistry);
        this.errorController = new ErrorController(resourceRegistry);
    }

    public HttpResponse service(HttpRequest request) {
        try {
            return handlerMapper.handle(request);
        } catch (HttpException exception) {
            return errorController.handle(exception);
        }
    }
}
