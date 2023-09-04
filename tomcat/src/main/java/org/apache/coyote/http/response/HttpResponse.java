package org.apache.coyote.http.response;

import static org.apache.coyote.http.HttpHeader.HEADER_KEY.CONTENT_LENGTH;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.List;
import org.apache.coyote.http.HttpHeader;
import org.apache.coyote.http.HttpHeaderConverter;
import org.apache.coyote.http.MediaType;
import org.apache.coyote.http.cookie.Cookie;

public class HttpResponse {

    private StatusCode statusCode;
    private final HttpHeader header;
    private String body;
    private String forwardPath;
    private boolean isCompleted;

    public HttpResponse() {
        this.header = new HttpHeader();
        this.isCompleted = false;
    }

    public byte[] getBytes() {
        String response = statusCode.renderStatusLine()
                + HttpHeaderConverter.encode(header)
                + body;

        return response.getBytes(StandardCharsets.UTF_8);
    }

    public void forward(String forwardPath) {
        this.forwardPath = forwardPath;
        this.isCompleted = false;
    }

    public boolean isCompleted() {
        return isCompleted;
    }

    public void addHeader(String key, String value) {
        header.setValue(key, value);
    }

    public boolean contains(String key) {
        return !header.getValue(key).isEmpty();
    }

    public List<String> getHeader(String key) {
        return header.getValue(key);
    }

    public void setStatusCode(StatusCode statusCode) {
        this.statusCode = statusCode;
        this.isCompleted = true;
    }

    public MediaType getMediaType() {
        return header.getMediaType();
    }

    public void setMediaType(MediaType mediaType) {
        header.setMediaType(mediaType);
    }

    public void setBody(String body) {
        this.body = body;
        byte[] bytes = body.getBytes(StandardCharsets.UTF_8);
        addHeader(CONTENT_LENGTH.value, String.valueOf(bytes.length));
    }

    public String getForwardPath() {
        return forwardPath;
    }

    public void addCookie(Cookie cookie) {
        header.addCookie(cookie);
    }

    public void setCharset(Charset charset) {
        header.setCharset(charset);
    }

    @Override
    public String toString() {
        return "HttpResponse{" +
            "statusCode=" + statusCode +
            ", header=" + header +
            ", body='" + body + '\'' +
            ", forwardPath='" + forwardPath + '\'' +
            ", isCompleted=" + isCompleted +
            '}';
    }
}
