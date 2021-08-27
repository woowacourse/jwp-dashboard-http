package nextstep.jwp.http.request;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * GET /test.html HTTP/1.1        // Request Line
 * Host: localhost:8000           // Request Headers Connection:
 * keep-alive Upgrade-Insecure-Request: 1
 * Content-Type: text/html
 * Content-Length: 345
 *
 * something1=123&something2=123   // Request Message Body
 */

public class HttpRequest {

    private final RequestLine requestLine;
    private final List<String> headers;
    private final String messageBody;

    public HttpRequest(RequestLine requestLine, List<String> headers, String messageBody) {
        this.requestLine = requestLine;
        this.headers = headers;
        this.messageBody = messageBody;
    }

    public HttpRequest(RequestLine requestLine, List<String> headers) {
        this(requestLine, headers, "");
    }

    public static HttpRequest of(List<String> lines) {
        RequestLine requestLine = RequestLine.of(lines.get(0));

        List<String> requestHeaders = new ArrayList<>();
        for (int i = 1; i < lines.size(); i++) {
            String line = lines.get(i);
            requestHeaders.add(line);
        }

        // TODO :: requestMessageBody
        return new HttpRequest(requestLine, requestHeaders);
    }

    public static HttpRequest of(BufferedReader bufferedReader) throws IOException {
        final List<String> lines = new ArrayList<>();

        String tempLine;
        while (!Objects.isNull(tempLine = bufferedReader.readLine())) {
            lines.add(tempLine);
        }
        return of(lines);
    }

    public RequestLine getRequestLine() {
        return requestLine;
    }

    public List<String> getHeaders() {
        return headers;
    }

    public String getMessageBody() {
        return messageBody;
    }
}
