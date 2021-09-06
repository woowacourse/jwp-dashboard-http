package nextstep.jwp.controller;

import java.io.IOException;
import nextstep.jwp.http.HttpSession;
import nextstep.jwp.http.HttpSessions;
import nextstep.jwp.http.Request;
import nextstep.jwp.http.Response;
import nextstep.jwp.model.User;
import nextstep.jwp.service.LoginService;
import nextstep.jwp.service.RegisterService;
import nextstep.jwp.utils.FileConverter;

public class RegisterController extends AbstractController {

    private static final String INDEX_HTML = "/index.html";

    private final RegisterService registerService;
    private final LoginService loginService;

    public RegisterController(RegisterService registerService,
        LoginService loginService) {
        this.registerService = registerService;
        this.loginService = loginService;
    }

    @Override
    protected void doGet(Request request, Response response) throws IOException {
        if (loginService.isLogin(request)) {
            response.set302Found(INDEX_HTML);
            return;
        }
        String responseBody = FileConverter.fileToString("/register.html");
        response.set200OK(request, responseBody);
    }

    @Override
    protected void doPost(Request request, Response response) {
        String account = request.getRequestBody("account");
        String password = request.getRequestBody("password");
        String email = request.getRequestBody("email");
        User user = new User(0, account, password, email);
        registerService.save(user);

        addSession(request, user);

        response.set302Found(INDEX_HTML);
    }

    private void addSession(Request request, User user) {
        HttpSession httpSession = request.getHttpSession();
        httpSession.setAttribute(httpSession.getId(), user);
        HttpSessions.add(httpSession.getId(), httpSession);
    }
}
