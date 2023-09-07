package org.apache.coyote.handler;

import org.apache.coyote.http.request.HttpMethod;
import org.apache.coyote.http.request.HttpRequest;
import org.apache.coyote.http.response.HttpResponse;

public abstract class AbstractController implements Controller {

    @Override
    public void service(final HttpRequest request, final HttpResponse httpResponse) throws Exception {
        if (request.isSameRequestMethod(HttpMethod.GET)) {
            doGet(request, httpResponse);
            return;
        }

        if (request.isSameRequestMethod(HttpMethod.POST)) {
            doPost(request, httpResponse);
            return;
        }

        throw new UnsupportedOperationException("지원하지 않는 HTTP Method 입니다.");
    }

    protected abstract void doPost(final HttpRequest request, final HttpResponse response) throws Exception;

    protected abstract void doGet(final HttpRequest request, final HttpResponse response) throws Exception;
}
