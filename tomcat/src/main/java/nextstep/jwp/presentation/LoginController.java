package nextstep.jwp.presentation;

import static nextstep.jwp.presentation.StaticResource.INDEX_PAGE;
import static nextstep.jwp.presentation.StaticResource.UNAUTHORIZED_PAGE;
import static org.apache.catalina.Session.JSESSIONID;

import nextstep.jwp.exception.UnauthorizedException;
import nextstep.jwp.model.User;
import nextstep.jwp.service.UserService;
import org.apache.catalina.Session;
import org.apache.coyote.HttpRequest;
import org.apache.coyote.HttpResponse;
import org.apache.coyote.constant.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoginController extends AbstractController {

    private static final LoginController INSTANCE = new LoginController(UserService.getInstance());

    private static final Logger LOG = LoggerFactory.getLogger(LoginController.class);

    private final UserService userService;

    private LoginController(final UserService userService) {
        this.userService = userService;
    }

    public static LoginController getInstance() {
        return INSTANCE;
    }

    @Override
    protected void doGet(final HttpRequest request, final HttpResponse response) throws Exception {
        final Session session = request.getSession(false);
        final User user = getUser(session);
        if (user != null) {
            redirectIndex(response);
        }

        response.setBody(StaticResource.ofRequest(request));
    }

    @Override
    protected void doPost(final HttpRequest request, final HttpResponse response) throws Exception {
        try {
            final User user = userService.login(request);
            setLoginSession(request, response, user);
            redirectIndex(response);
        } catch (final UnauthorizedException unauthorizedException) {
            redirectNoAuth(response);
        } catch (final RuntimeException runtimeException) {
            LOG.error(runtimeException.getMessage());
        }

        response.setBody(StaticResource.ofRequest(request));
    }

    private User getUser(final Session session) {
        if (session == null) {
            return null;
        }
        return (User) session.getAttribute("user");
    }

    private void setLoginSession(final HttpRequest request, final HttpResponse response, final User user) {
        final Session session = request.getSession(true);
        session.setAttribute("user", user);
        response.addSetCookie(JSESSIONID, session.getId());
    }

    private void redirectNoAuth(final HttpResponse response) {
        response.setStatus(HttpStatus.FOUND);
        response.setLocation(UNAUTHORIZED_PAGE);
    }

    private void redirectIndex(final HttpResponse response) {
        response.setStatus(HttpStatus.FOUND);
        response.setLocation(INDEX_PAGE);
    }
}
