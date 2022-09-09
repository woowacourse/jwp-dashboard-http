package nextstep.jwp.controller;

import java.util.Map;
import org.apache.coyote.http11.request.HttpRequest;

public class RequestMapping {

    private static final Map<String, Controller> CONTROLLERS = Map.of(
            "/", new IndexController(),
            "/login", new LoginController(),
            "/register", new RegisterController()
    );

    public Controller getController(HttpRequest request) {
        if (CONTROLLERS.containsKey(request.getPath())) {
            return CONTROLLERS.get(request.getPath());
        }

        return new StaticFileController();
    }
}
