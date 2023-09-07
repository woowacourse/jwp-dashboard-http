package org.apache.coyote.handler.controller.login;

import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import org.apache.coyote.handler.RequestController;
import org.apache.coyote.http.LoginManager;
import org.apache.coyote.http.common.ContentType;
import org.apache.coyote.http.common.HttpBody;
import org.apache.coyote.http.common.HttpHeader;
import org.apache.coyote.http.request.HttpCookie;
import org.apache.coyote.http.request.HttpRequest;
import org.apache.coyote.http.request.QueryString;
import org.apache.coyote.http.response.HttpResponse;
import org.apache.coyote.http.response.StatusCode;
import org.apache.coyote.http.response.StatusLine;
import org.apache.coyote.http.session.Session;
import org.apache.coyote.http.session.SessionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.UUID;

import static org.apache.coyote.handler.controller.Path.LOGIN;
import static org.apache.coyote.handler.controller.Path.MAIN;
import static org.apache.coyote.handler.controller.Path.UNAUTHORIZED;
import static org.apache.coyote.http.common.HttpHeader.CONTENT_TYPE;
import static org.apache.coyote.http.common.HttpHeader.COOKIE;

public class LoginController extends RequestController {

    private static final LoginManager loginManager = new SessionManager();

    private static final String TARGET_URI = "login";
    private static final Logger log = LoggerFactory.getLogger(LoginController.class);

    @Override
    public boolean supports(final HttpRequest httpRequest) {
        return httpRequest.containsRequestUri(TARGET_URI);
    }

    @Override
    protected void doPost(final HttpRequest request, final HttpResponse response) throws Exception {
        final Map<String, String> bodyParams = request.getParsedBody();
        final String account = bodyParams.get("account");
        final String password = bodyParams.get("password");

        User user = null;
        try {
            user = InMemoryUserRepository.findByAccount(account)
                    .orElseThrow(() -> new IllegalArgumentException("잘못된 계정입니다. 다시 입력해주세요."));

            if (!user.checkPassword(password)) {
                throw new IllegalArgumentException("잘못된 비밀번호입니다. 다시 입력해주세요.");
            }
            log.info("로그인 성공! user = {}", user);
        } catch (final IllegalArgumentException e) {
            log.warn("login error = {}", e);
            response.mapToRedirect(UNAUTHORIZED.getPath());
            return;
        }

        final UUID uuid = UUID.randomUUID();
        setSession(uuid.toString(), Map.of("account", user.getAccount()));

        response.changeStatusLine(StatusLine.from(StatusCode.FOUND));
        response.addHeader(HttpHeader.LOCATION, MAIN.getPath());
        response.addHeader(HttpHeader.SET_COOKIE, "JSESSIONID=" + uuid);
        response.changeBody(HttpBody.empty());
    }

    private void setSession(final String jSessionId, final Map<String, String> sessionData) {
        final Session session = new Session(jSessionId);
        for (final Map.Entry<String, String> entry : sessionData.entrySet()) {
            session.add(entry.getKey(), entry.getValue());
        }
        loginManager.add(session);
    }

    @Override
    protected void doGet(final HttpRequest request, final HttpResponse response) throws Exception {
        if (request.containsHeader(COOKIE)) {
            final HttpCookie cookies = HttpCookie.from(request.getHeader(COOKIE));
            if (isAlreadyLogined(cookies.get("JSESSIONID"))) {
                response.mapToRedirect(MAIN.getPath());
                return;
            }
        }

        if (request.hasQueryString()) {
            final QueryString queryString = request.getQueryString();

            final String account = queryString.get("account");
            final String password = queryString.get("password");

            try {
                final User user = InMemoryUserRepository.findByAccount(account)
                        .orElseThrow(() -> new IllegalArgumentException("잘못된 계정입니다. 다시 입력해주세요."));

                if (!user.checkPassword(password)) {
                    throw new IllegalArgumentException("잘못된 비밀번호입니다. 다시 입력해주세요.");
                }
                log.info("로그인 성공! user = {}", user);
            } catch (final IllegalArgumentException e) {
                log.warn("login error = {}", e);
                response.mapToRedirect(UNAUTHORIZED.getPath());
                return;
            }

            response.mapToRedirect(MAIN.getPath());
            return;
        }

        response.changeStatusLine(StatusLine.from(StatusCode.OK));
        response.addHeader(CONTENT_TYPE, ContentType.HTML.getValue());
        response.changeBody(HttpBody.file(LOGIN.getPath()));
    }

    private boolean isAlreadyLogined(final String jSessionId) {
        return loginManager.isAlreadyLogined(jSessionId);
    }
}
