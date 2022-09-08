package nextstep.jwp.ui;

import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.ResponseEntity;

public class HomeController extends AbstractController {

    private static final String WELCOME_MESSAGE = "Hello world!";

    @Override
    protected void doGet(HttpRequest request, HttpResponse response) {
        response.initResponseValues(ResponseEntity.body(WELCOME_MESSAGE));
    }
}
