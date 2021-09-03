package nextstep.jwp.http;

import java.util.Map;

public class HttpRequest {

    private final String method;
    private final String uri;
    private final String protocol;
    private final Map<String, String> headers;
    private final String body;

    public HttpRequest(String method, String uri, String protocol,
            Map<String, String> headers, String body) {
        this.method = method;
        this.uri = uri;
        this.protocol = protocol;
        this.headers = headers;
        this.body = body;
    }

    public String getMethod() {
        return method;
    }

    public String getUri() {
        return uri;
    }

    public String getBody() {
        return body;
    }
}
