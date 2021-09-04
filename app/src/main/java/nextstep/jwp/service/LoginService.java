package nextstep.jwp.service;

import java.io.IOException;
import java.util.Objects;
import java.util.Optional;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.http.HttpCookie;
import nextstep.jwp.http.HttpRequest;
import nextstep.jwp.http.HttpResponse;
import nextstep.jwp.http.HttpSession;
import nextstep.jwp.http.HttpSessions;
import nextstep.jwp.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoginService {

    private static final Logger LOG = LoggerFactory.getLogger(LoginService.class);
    private static final String JSESSIONID = "JSESSIONID";

    public void login(final HttpRequest httpRequest, final HttpResponse httpResponse) throws IOException {
        userLogin(httpRequest, httpResponse);
    }

    private void userLogin(final HttpRequest httpRequest, final HttpResponse httpResponse) throws IOException {
        if (isValidateUser(httpRequest)) {
            HttpCookie cookie = httpRequest.getCookie();
            sessionMappingClient(httpRequest, cookie);
            httpResponse.redirect302Transfer("/index.html");
        } else {
            httpResponse.redirectWithStatusCode("/401.html", "401");
        }
    }

    private void sessionMappingClient(final HttpRequest httpRequest, final HttpCookie cookie) {
        if (cookie.containsKey(JSESSIONID)) {
            HttpSession httpSession = getHttpSessionByCookie(cookie);
            httpSession.setAttribute("user", getUser(httpRequest));
        }
    }

    private HttpSession getHttpSessionByCookie(final HttpCookie cookie) {
        return HttpSessions.getSession(cookie.getCookieValueByKey(JSESSIONID));
    }

    private boolean isValidateUser(final HttpRequest httpRequest) {
        User user = getUser(httpRequest);
        return user != null && isCollectPassword(httpRequest, user);
    }

    private boolean isCollectPassword(final HttpRequest httpRequest, final User user) {
        if (user.checkPassword(httpRequest.getBodyDataByKey("password"))) {
            LOG.debug("password Collect! : {}", user.getAccount());
            return true;
        }
        LOG.debug("password InCollect!! : {}", user.getAccount());
        return false;
    }

    private User getUser(final HttpRequest httpRequest) {

        String requestUserAccount = httpRequest.getBodyDataByKey("account");
        Optional<User> user = InMemoryUserRepository.findByAccount(httpRequest.getBodyDataByKey("account"));
        if (user.isPresent()) {
            LOG.debug("user Account : {}", user.get().getAccount());
            return user.get();
        }
        LOG.debug("user Not Exist!! : {}", requestUserAccount);
        return null;
    }

    public void doGet(final HttpResponse httpResponse, final HttpRequest httpRequest) throws IOException {
        HttpCookie cookie = httpRequest.getCookie();
        if (cookie.containsKey(JSESSIONID) && isLoggedIn(getHttpSessionByCookie(cookie))) {
            httpResponse.redirect302Transfer("/index.html");
            return;
        }
        httpResponse.transfer("/login.html");
    }

    private boolean isLoggedIn(final HttpSession httpSession) {
        return Objects.nonNull(httpSession.getAttribute("user"));
    }
}
