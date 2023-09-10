package nextstep.jwp.response;

import nextstep.jwp.model.HttpCookies;

public class HttpResponse {

    private static final String DELIMITER = "\r\n";

    private final ResponseHeaders responseHeaders = new ResponseHeaders();
    private final HttpCookies cookies = new HttpCookies();
    private StatusLine statusLine;
    private String responseBody;

    public HttpResponse() {
    }

    public void setStatusLine(final StatusLine statusLine) {
        this.statusLine = statusLine;
    }

    public void setResponseBody(final String body) {
        this.responseBody = body;
        setContentLengthHeader(body);
    }

    private void setContentLengthHeader(final String content) {
        if (content == null) {
            return;
        }
        responseHeaders.save("Content-Length", String.valueOf(content.getBytes().length));
    }

    public void addHeader(final String name, final String value) {
        responseHeaders.save(name, value);
    }

    public void addCookie(final String name, final String value) {
        cookies.save(name, value);
    }

    public String toResponse() {
        return String.join(DELIMITER,
                statusLine.toResponse(),
                responseHeaders.toResponse(),
                "",
                responseBody);
    }
}
