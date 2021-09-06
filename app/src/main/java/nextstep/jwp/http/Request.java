package nextstep.jwp.http;

import java.util.Map;

public class Request {

    private final HttpMethod method;
    private final Uri uri;
    private final String httpVersion;
    private final RequestHeader header;
    private final Map<String, String> body;
    private final HttpCookie httpCookie;
    private final HttpSession httpSession;

    private Request(Builder builder) {
        this.method = builder.method;
        this.uri = builder.uri;
        this.httpVersion = builder.httpVersion;
        this.header = builder.header;
        this.body = builder.body;
        this.httpCookie = builder.httpCookie;
        this.httpSession = builder.httpSession;
    }

    public String getParameter(String key) {
        return header.getParameter(key);
    }

    public String getRequestBody(String key) {
        return body.get(key);
    }

    public boolean isUriMatch(String target) {
        return uri.getResourceUri().equals(target);
    }

    public boolean isUriFile() {
        return uri.isUriFile();
    }

    public boolean isEqualsHttpMethod(HttpMethod httpMethod) {
        return this.method == httpMethod;
    }

    public String acceptType() {
        return header.acceptType(uri);
    }

    public String getUri() {
        return uri.getResourceUri();
    }

    public HttpMethod getMethod() {
        return method;
    }

    public HttpSession getHttpSession() {
        return httpSession;
    }

    public String getCookie(String key) {
        return httpCookie.getCookie(key);
    }

    public String getHttpVersion() {
        return httpVersion;
    }

    public static class Builder {

        private HttpMethod method;
        private Uri uri;
        private String httpVersion = "";
        private RequestHeader header;
        private Map<String, String> body;
        private HttpCookie httpCookie;
        private HttpSession httpSession;

        public Builder method(HttpMethod method) {
            this.method = method;
            return this;
        }

        public Builder uri(String uri) {
            this.uri = Uri.valueOf(uri);
            return this;
        }

        public Builder httpVersion(String httpVersion) {
            this.httpVersion = httpVersion;
            return this;
        }

        public Builder header(Map<String, String> header) {
            this.header = new RequestHeader(header);
            return this;
        }

        public Builder header(RequestHeader header) {
            this.header = header;
            return this;
        }

        public Builder cookie(HttpCookie cookie) {
            this.httpCookie = cookie;
            return this;
        }

        public Builder body(Map<String, String> body) {
            this.body = body;
            return this;
        }

        public Builder httpSession(HttpSession httpSession) {
            this.httpSession = httpSession;
            return this;
        }

        public Request build() {
            return new Request(this);
        }
    }
}
