package org.apache.coyote.http11.controller;

import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import org.apache.catalina.SessionManager;
import org.apache.coyote.http11.request.HttpCookie;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.HttpResponseHeader;
import org.apache.coyote.http11.response.HttpResponseStatus;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class LoginPostController extends AbstractController {
    public static final String JSESSIONID = "JSESSIONID";

    @Override
    public boolean isSupported(HttpRequest request) {
        return request.isPOST() && request.isSamePath("/login");
    }

    @Override
    protected void doPost(HttpRequest request, HttpResponse response) throws Exception {
        if (request.isNotExistBody()) {
            throw new IllegalArgumentException("로그인 정보가 입력되지 않았습니다.");
        }
        final HttpCookie cookie = request.getCookie();
        Map<String, String> parsedRequestBody = parseRequestBody(request);
        User user = InMemoryUserRepository.findByAccount(parsedRequestBody.get("account"))
                .orElseThrow(() -> new IllegalArgumentException("입력한 회원 ID가 존재하지 않습니다."));
        if (isLoginFail(user, parsedRequestBody)) {
            handle401(request, response);
            return;
        }
        if (!cookie.isExist(JSESSIONID)) {
            String jSessionId = String.valueOf(UUID.randomUUID());
            String setCookie = JSESSIONID + "=" + jSessionId;
            SessionManager.instanceOf().addLoginSession(jSessionId, user);
            HttpResponseHeader responseHeader = new HttpResponseHeader.Builder(
                    readContentType(request.getAccept(), request.getPath()), String.valueOf(0))
                    .addLocation("/index.html")
                    .addSetCookie(setCookie)
                    .build();
            response.updateResponse(HttpResponseStatus.FOUND, responseHeader, "");
            return;
        }
        HttpResponseHeader responseHeader = new HttpResponseHeader.Builder(
                readContentType(request.getAccept(), request.getPath()), String.valueOf(0))
                .addLocation("/index.html")
                .build();
        response.updateResponse(HttpResponseStatus.FOUND, responseHeader, "");

    }

    private boolean isLoginFail(User user, Map<String, String> parsedRequestBody) {
        return !user.checkPassword(parsedRequestBody.get("password"));
    }

    private Map<String, String> parseRequestBody(HttpRequest request) {
        Map<String, String> parsedRequestBody = new HashMap<>();
        String[] queryTokens = request.getBody().split("&");
        for (String queryToken : queryTokens) {
            putRequestBodyToken(queryToken, parsedRequestBody);
        }
        return parsedRequestBody;
    }

    private void putRequestBodyToken(String queryToken, Map<String, String> parsedRequestBody) {
        int equalSeparatorIndex = queryToken.indexOf("=");
        if (equalSeparatorIndex != -1) {
            parsedRequestBody.put(queryToken.substring(0, equalSeparatorIndex),
                    queryToken.substring(equalSeparatorIndex + 1));
        }
    }

    private void handle401(HttpRequest request, HttpResponse response) throws URISyntaxException, IOException {
        String responseBody = readHtmlFile(getClass().getResource("/static/401.html"));
        HttpResponseHeader responseHeader = new HttpResponseHeader.Builder(
                readContentType(request.getAccept(), request.getPath()),
                String.valueOf(responseBody.getBytes().length))
                .build();
        response.updateResponse(HttpResponseStatus.UNAUTHORIZATION, responseHeader, responseBody);
    }
}
