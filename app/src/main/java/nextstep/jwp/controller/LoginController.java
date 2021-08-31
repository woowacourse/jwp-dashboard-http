package nextstep.jwp.controller;

import java.io.FileNotFoundException;
import java.util.NoSuchElementException;

import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.exception.LoginException;
import nextstep.jwp.handler.HttpBody;
import nextstep.jwp.handler.HttpSession;
import nextstep.jwp.handler.request.HttpRequest;
import nextstep.jwp.handler.response.HttpResponse;
import nextstep.jwp.model.User;
import nextstep.jwp.util.File;
import nextstep.jwp.util.FileReader;

public class LoginController extends AbstractController {

    @Override
    protected void doGet(HttpRequest httpRequest, HttpResponse httpResponse) throws FileNotFoundException {
        try {
            HttpSession session = httpRequest.getSession();
            if (session.containsAttribute("user")) {
                File file = FileReader.readFile("/index.html");
                httpResponse.redirect("index.html", file);
                return;
            }
        } catch (NoSuchElementException e) {
            File file = FileReader.readHtmlFile(httpRequest.getRequestUrl());
            httpResponse.ok(file);
            return;
        }

        if (httpRequest.isUriContainsQuery()) {
            doGetWithQuery(httpRequest, httpResponse);
            return;
        }
        File file = FileReader.readHtmlFile(httpRequest.getRequestUrl());
        httpResponse.ok(file);
    }

    private void doGetWithQuery(HttpRequest httpRequest, HttpResponse httpResponse) {}

    @Override
    protected void doPost(HttpRequest httpRequest, HttpResponse httpResponse) throws FileNotFoundException {
        HttpBody httpBody = httpRequest.getBody();
        String account = httpBody.getBodyParams("account");
        String password = httpBody.getBodyParams("password");

        try {
            User user = findUser(account);
            if (!user.checkPassword(password)) {
                throw new LoginException("User의 정보와 입력한 정보가 일치하지 않습니다.");
            }

            final HttpSession httpSession = httpRequest.getSession();
            httpSession.setAttribute("user", user);

            File file = FileReader.readFile("/index.html");
            httpResponse.redirect("/index.html", file);
        } catch (LoginException | NoSuchElementException e) {
            File file = FileReader.readErrorFile("/401.html");
            httpResponse.unauthorized("/401.html", file);
        }
    }

    private User findUser(String account) {
        return InMemoryUserRepository.findByAccount(account)
                                     .orElseThrow(() -> { throw new LoginException("해당 User가 존재하지 않습니다."); });
    }
}
