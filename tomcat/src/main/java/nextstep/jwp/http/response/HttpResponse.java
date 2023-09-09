package nextstep.jwp.http.response;

import nextstep.jwp.http.common.Cookie;
import nextstep.jwp.http.common.HttpBody;
import nextstep.jwp.http.common.HttpCookies;
import nextstep.jwp.http.common.HttpStatus;

public class HttpResponse {

    private static final String RESPONSE_LINE_FORMAT = "%s %s %s ";
    private static final String COOKIE_HEADER_FORMAT = "Set-Cookie: %s";

    private final HttpStatusLine httpStatusLine;
    private final HttpResponseHeaders httpResponseHeaders;
    private final HttpCookies httpCookies;
    private final HttpBody httpBody;

    private HttpResponse(HttpStatusLine httpStatusLine, HttpResponseHeaders httpResponseHeaders,
            HttpCookies httpCookies, HttpBody httpBody) {
        this.httpStatusLine = httpStatusLine;
        this.httpResponseHeaders = httpResponseHeaders;
        this.httpCookies = httpCookies;
        this.httpBody = httpBody;
    }

    public static HttpResponse createEmptyResponse() {
        return new HttpResponse(HttpStatusLine.createDefaultStatusLine(), HttpResponseHeaders.createEmptyHeaders(),
                HttpCookies.createEmptyCookies(), HttpBody.createEmptyHttpBody());
    }

    public void clearHeadersAndBody() {
        httpResponseHeaders.clear();
        httpBody.clear();
    }

    public void setStatus(HttpStatus httpStatus) {
        httpStatusLine.setHttpStatus(httpStatus);
    }

    public void setHeader(String key, String value) {
        httpResponseHeaders.addHeader(key, value);
    }

    public void setCookie(String key, Cookie cookie) {
        httpCookies.addCookie(key, cookie);
    }

    public void setContentType(String value) {
        httpResponseHeaders.setContentType(value);
    }

    public void setLocation(String value) {
        httpResponseHeaders.setLocation(value);
    }

    public void setBody(String body) {
        httpBody.setBody(body);

        updateContentLength();
    }

    private void updateContentLength() {
        httpResponseHeaders.setContentLength(String.valueOf(httpBody.getBytesLength()));
    }

    public void setDeletingJSessionCookie() {
        Cookie cookie = Cookie.of("", "Max-Age=0");

        httpCookies.addCookie("JSESSIONID", cookie);
    }

    public byte[] getBytes() {
        String httpVersion = httpStatusLine.getHttpVersion().getValue();
        String httpStatusCode = httpStatusLine.getHttpStatus().getCode();
        String httpStatusMessage = httpStatusLine.getHttpStatus().getMessage();

        String response = String.join("\r\n",
                String.format(RESPONSE_LINE_FORMAT, httpVersion, httpStatusCode, httpStatusMessage),
                httpResponseHeaders.getHeaders(), String.format(COOKIE_HEADER_FORMAT, httpCookies.getCookies()), "",
                httpBody.getBody());

        return response.getBytes();
    }

}
