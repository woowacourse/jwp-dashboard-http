package org.apache.coyote.http11.handler;

import static org.apache.coyote.http11.header.HttpHeaderType.LOCATION;
import static org.apache.coyote.http11.http.HttpVersion.HTTP11;
import static org.apache.coyote.http11.http.response.HttpStatus.REDIRECT;

import java.util.Map;
import nextstep.jwp.application.UserService;
import nextstep.jwp.dto.UserLoginRequest;
import org.apache.catalina.utils.Parser;
import org.apache.coyote.http11.header.HttpHeader;
import org.apache.coyote.http11.http.response.HttpResponse;

abstract class UserHandler implements Handler {

    private final UserService userService = new UserService();

    protected HttpResponse generateLoginResponse(final String body) {
        final Map<String, String> queryParams = Parser.parseQueryParams(body);
        try {
            final UserLoginRequest userLoginRequest = getUserLoginRequest(queryParams);
            userService.login(userLoginRequest);
            final HttpHeader location = HttpHeader.of(LOCATION.getValue(), "/index.html");

            return HttpResponse.of(HTTP11, REDIRECT, location);
        } catch (IllegalArgumentException exception) {
            final HttpHeader location = HttpHeader.of(LOCATION.getValue(), "/401.html");
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
