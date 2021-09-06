package nextstep.jwp.controller;

import java.io.IOException;
import nextstep.jwp.http.HttpSession;
import nextstep.jwp.http.HttpSessions;
import nextstep.jwp.http.Request;
import nextstep.jwp.http.Response;
import nextstep.jwp.model.User;
import nextstep.jwp.service.LoginService;
import nextstep.jwp.utils.FileConverter;

public class LoginController extends AbstractController {

    private static final String INDEX_HTML = "/index.html";

    private final LoginService loginService;

    public LoginController(LoginService loginService) {
        this.loginService = loginService;
    }

    @Override
    protected void doGet(Request request, Response response) throws IOException {
        if (loginService.isLogin(request)) {
            response.set302Found(INDEX_HTML);
            return;
        }
        String responseBody = FileConverter.fileToString("/login.html");
        response.set200OK(request, responseBody);
    }

    @Override
    protected void doPost(Request request, Response response) {
        String account = request.getRequestBody("account");
        String password = request.getRequestBody("password");
        User user = loginService.login(new User(account, password));

        addSession(request, user);

        response.set302Found(INDEX_HTML);
        response.addHeader("Location", INDEX_HTML);
    }

    private void addSession(Request request, User user) {
        HttpSession httpSession = request.getHttpSession();
        httpSession.setAttribute(httpSession.getId(), user);
        HttpSessions.add(httpSession.getId(), httpSession);
    }
}
