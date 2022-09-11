package nextstep.jwp.controller;

import static nextstep.jwp.controller.ResourceUrls.INDEX_HTML;
import static nextstep.jwp.controller.ResourceUrls.LOGIN_HTML;
import static nextstep.jwp.controller.ResourceUrls.UNAUTHORIZED_HTML;
import static org.apache.coyote.http11.header.HttpHeaderType.LOCATION;
import static org.apache.coyote.http11.http.HttpVersion.HTTP11;
import static org.apache.coyote.http11.http.response.HttpStatus.REDIRECT;

import java.util.Map;
import java.util.NoSuchElementException;
import nextstep.jwp.application.AuthorizeService;
import nextstep.jwp.application.UserService;
import nextstep.jwp.dto.UserLoginRequest;
import org.apache.catalina.session.SessionManager;
import org.apache.catalina.webutils.Parser;
import org.apache.coyote.http11.header.HttpCookie;
import org.apache.coyote.http11.header.HttpHeader;
import org.apache.coyote.http11.http.request.HttpRequest;
import org.apache.coyote.http11.http.response.HttpResponse;

public class LoginController extends ResourceController {

    private final AuthorizeService authorizeService = AuthorizeService.getInstance();
    private final UserService userService = UserService.getInstance();

    @Override
    public HttpResponse service(final HttpRequest httpRequest) {
        if (httpRequest.isGetMethod()) {
            return doGet(httpRequest);
        }
        return doPost(httpRequest);
    }

    protected HttpResponse doGet(final HttpRequest httpRequest) {
        if (authorizeService.isAuthorized(httpRequest)) {
            final HttpHeader location = HttpHeader.of(LOCATION.getValue(), INDEX_HTML.getValue());
            return HttpResponse.of(HTTP11, REDIRECT, location);
        }
        return generateResourceResponse(LOGIN_HTML.getValue());
    }

    protected HttpResponse doPost(final HttpRequest httpRequest) {
        final String body = httpRequest.getBody();
        return generateLoginResponse(body);
    }

    protected HttpResponse generateLoginResponse(final String body) {
        final Map<String, String> queryParams = Parser.parseQueryParams(body);
        try {
            final UserLoginRequest userLoginRequest = getUserLoginRequest(queryParams);
            userService.login(userLoginRequest);
            final HttpHeader location = HttpHeader.of(LOCATION.getValue(), INDEX_HTML.getValue());
            final HttpCookie cookie = SessionManager.createCookie();
            final HttpHeader cookieHeader = HttpHeader.of("Set-Cookie", cookie.toHeaderValue());
            return HttpResponse.of(HTTP11, REDIRECT, location, cookieHeader);
        } catch (IllegalArgumentException exception) {
            final HttpHeader location = HttpHeader.of(LOCATION.getValue(), UNAUTHORIZED_HTML.getValue());
            return HttpResponse.of(HTTP11, REDIRECT, location);
        } catch (NoSuchElementException exception) {
            final HttpHeader location = HttpHeader.of(LOCATION.getValue(), LOGIN_HTML.getValue());
            return HttpResponse.of(HTTP11, REDIRECT, location);
        }
    }

    private UserLoginRequest getUserLoginRequest(final Map<String, String> queryParams) {
        validateLoginParams(queryParams);
        return new UserLoginRequest(queryParams.get("account"),
                queryParams.get("password"));
    }

    private void validateLoginParams(final Map<String, String> queryParams) {
        if (!queryParams.containsKey("account") || !queryParams.containsKey("password")) {
            throw new IllegalArgumentException("account와 password 정보가 입력되지 않았습니다.");
        }
    }
}
