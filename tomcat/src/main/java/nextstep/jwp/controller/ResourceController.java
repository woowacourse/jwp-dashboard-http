package nextstep.jwp.controller;

import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

import nextstep.jwp.util.FileReader;

public class ResourceController implements Controller {

    private static final Controller instance = new ResourceController();

    private ResourceController() {
    }

    public static Controller getInstance() {
        return instance;
    }

    @Override
    public HttpResponse doGet(HttpRequest request) {
        String uri = request.getUri();
        return HttpResponse.ok(uri, FileReader.read(uri));
    }

    @Override
    public HttpResponse doPost(HttpRequest request) {
        return HttpResponse.notFound();
    }

}
