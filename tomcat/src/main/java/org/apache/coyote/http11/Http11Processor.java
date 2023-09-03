package org.apache.coyote.http11;

import nextstep.jwp.exception.UncheckedServletException;
import org.apache.coyote.http11.request.HttpRequestParser;
import org.apache.coyote.http11.response.Http11Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.util.stream.Collectors;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);

    private final Socket connection;
    private final HttpRequestParser httpRequestParser;
    private final HttpDispatcher httpDispatcher;

    public Http11Processor(final Socket connection, final HttpRequestParser httpRequestParser, final HttpDispatcher httpDispatcher) {
        this.connection = connection;
        this.httpRequestParser = httpRequestParser;
        this.httpDispatcher = httpDispatcher;
    }

    @Override
    public void run() {
        log.info("connect host: {}, port: {}", this.connection.getInetAddress(), this.connection.getPort());
        this.process(this.connection);
    }

    @Override
    public void process(final Socket connection) {
        try (final var inputStream = connection.getInputStream();
             final var outputStream = connection.getOutputStream()) {

            final var request = httpRequestParser.parse(inputStream);
            final var response = httpDispatcher.handle(request);

            if (request.notContainJsessionId()) {
                response.addHeader("Set-Cookie", "JSESSIONID=656cef62-e3c4-40bc-a8df-94732920ed46");
            }

            sendResponse(outputStream, response);
        } catch (final IOException | UncheckedServletException exception) {
            log.error(exception.getMessage(), exception);
        }
    }

    private void sendResponse(final OutputStream outputStream, final Http11Response response) throws IOException {
        final String httpResponse = makeHttpResponse(response);
        outputStream.write(httpResponse.getBytes());
        outputStream.flush();
    }

    private String makeHttpResponse(final Http11Response response) {
        final StringBuilder httpResponse = new StringBuilder();

        httpResponse.append("HTTP/1.1 ").append(response.getStatusCode().getResponse()).append("\r\n");
        httpResponse.append("Content-Type: ").append(response.getContentType().getResponse()).append(";charset=utf-8\r\n");
        httpResponse.append("Content-Length: ").append(response.getResponseBody().getBytes().length).append("\r\n");

        if (!response.getOtherHeader().isEmpty()) {
            final String additionalHeaders = response.getOtherHeader().entrySet().stream()
                    .map(entry -> entry.getKey() + ": " + entry.getValue())
                    .collect(Collectors.joining("\r\n"));
            httpResponse.append(additionalHeaders).append("\r\n");
        }

        httpResponse.append("\r\n");
        httpResponse.append(response.getResponseBody());

        return httpResponse.toString();
    }
}
