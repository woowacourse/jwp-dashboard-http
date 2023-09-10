package org.apache.coyote.http11;

import nextstep.jwp.exception.UncheckedServletException;
import org.apache.coyote.http11.request.HttpRequestParser;
import org.apache.coyote.http11.response.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;

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
             final var outputStream = connection.getOutputStream();
             final var reader = new BufferedReader(new InputStreamReader(inputStream))) {

            final var request = httpRequestParser.parse(reader);
            final var response = new HttpResponse(request.getVersion());
            httpDispatcher.doDispatch(request, response);

            sendResponse(outputStream, response);
        } catch (final IOException | UncheckedServletException exception) {
            log.error(exception.getMessage(), exception);
        }
    }

    private void sendResponse(final OutputStream outputStream, final HttpResponse response) throws IOException {
        final String httpResponse = ResponseGenerator.makeResponse(response);
        outputStream.write(httpResponse.getBytes());
        outputStream.flush();
    }
}
