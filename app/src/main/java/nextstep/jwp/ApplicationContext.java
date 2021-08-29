package nextstep.jwp;

import java.util.HashMap;
import nextstep.jwp.http.request.HttpRequest;
import nextstep.jwp.presentation.Controller;

public class ApplicationContext {

    private final HashMap<String, Object> controllers;

    public ApplicationContext(HashMap<String, Object> controllers) {
        this.controllers = controllers;
    }

    public Controller getController(HttpRequest request) {
        String path = request.getPath();

        if (controllers.containsKey(path)) {
            return (Controller) controllers.get(path);
        }

        return null;
    }
}
