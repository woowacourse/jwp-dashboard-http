package nextstep.jwp.controller;

import org.apache.coyote.request.HttpRequest;
import org.apache.coyote.response.HttpResponse;

public abstract class AbstractController implements Controller {

    @Override
    public boolean canProcess(HttpRequest httpRequest) {
        return true;
    }

    @Override
    public void service(HttpRequest httpRequest, HttpResponse httpResponse) {
    }

    protected void doPost(HttpRequest httpRequest, HttpResponse httpResponse) {
        throw new UnsupportedOperationException();
    }

    protected void doGet(HttpRequest httpRequest, HttpResponse httpResponse) {
        throw new UnsupportedOperationException();
    }

    protected void doDelete(HttpRequest httpRequest, HttpResponse httpResponse) {
        throw new UnsupportedOperationException();
    }

    protected void doPut(HttpRequest httpRequest, HttpResponse httpResponse) {
        throw new UnsupportedOperationException();
    }

    protected void doPatch(HttpRequest httpRequest, HttpResponse httpResponse) {
        throw new UnsupportedOperationException();
    }
}