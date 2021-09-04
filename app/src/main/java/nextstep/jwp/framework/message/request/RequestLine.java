package nextstep.jwp.framework.message.request;

import nextstep.jwp.framework.common.HttpMethod;
import nextstep.jwp.framework.common.HttpVersion;
import nextstep.jwp.framework.exception.HttpMessageConvertFailureException;
import nextstep.jwp.framework.message.StartLine;
import nextstep.jwp.utils.StringUtils;

import java.util.List;
import java.util.Objects;

public class RequestLine implements StartLine {

    private static final int REQUEST_LINE_ITEM_COUNT = 3;

    private final HttpMethod httpMethod;
    private final String requestUri;
    private final HttpVersion httpVersion;

    private RequestLine(String httpMethod, String requestUri, String httpVersion) {
        this(HttpMethod.valueOf(httpMethod), requestUri, HttpVersion.from(httpVersion));
    }

    public RequestLine(HttpMethod httpMethod, String requestUri, HttpVersion httpVersion) {
        this.httpMethod = httpMethod;
        this.requestUri = requestUri;
        this.httpVersion = httpVersion;
    }

    public static RequestLine from(String requestLine) {
        List<String> words = StringUtils.splitWithSeparator(requestLine, " ");
        if (words.size() != REQUEST_LINE_ITEM_COUNT) {
            throw new HttpMessageConvertFailureException(
                    String.format("RequestLine이 적절하지 않습니다.(%s)", requestLine)
            );
        }
        return new RequestLine(words.get(0), words.get(1), words.get(2));
    }

    public String asString() {
        String httpMethodValue = this.httpMethod.name();
        String httpVersionValue = this.getHttpVersion().getValue();
        return StringUtils.concatNewLine(
                StringUtils.joinWithBlank(httpMethodValue, requestUri, httpVersionValue)
        );
    }

    public HttpMethod getHttpMethod() {
        return httpMethod;
    }

    public String getRequestUri() {
        return requestUri;
    }

    public HttpVersion getHttpVersion() {
        return httpVersion;
    }

    @Override
    public byte[] toBytes() {
        return asString().getBytes();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RequestLine that = (RequestLine) o;
        return Objects.equals(httpMethod, that.httpMethod) && Objects.equals(requestUri, that.requestUri) && Objects.equals(httpVersion, that.httpVersion);
    }

    @Override
    public int hashCode() {
        return Objects.hash(httpMethod, requestUri, httpVersion);
    }
}
