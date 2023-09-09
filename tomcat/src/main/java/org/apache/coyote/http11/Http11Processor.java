package org.apache.coyote.http11;

import nextstep.jwp.exception.UncheckedServletException;
import org.apache.catalina.Controller;
import org.apache.coyote.Processor;
import org.apache.coyote.RequestMapping;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.request.HttpRequestParser;
import org.apache.coyote.http11.response.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

import static org.apache.coyote.http11.response.HttpStatusCode.NOT_FOUND;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);
    private final Socket connection;
    private final RequestMapping requestMapping;

    public Http11Processor(final Socket connection, final RequestMapping requestMapping) {
        this.connection = connection;
        this.requestMapping = requestMapping;
    }

    @Override
    public void run() {
        log.info("connect host: {}, port: {}", connection.getInetAddress(), connection.getPort());
        process(connection);
    }

    @Override
    public void process(final Socket connection) {
        try (final var inputStream = connection.getInputStream();
             final var outputStream = connection.getOutputStream();
             final var reader = new BufferedReader(new InputStreamReader(inputStream))) {

            final HttpRequestParser httpRequestParser = new HttpRequestParser(reader);
            final HttpRequest httpRequest = httpRequestParser.parse();

            final Controller controller = requestMapping.getController(httpRequest);
            final HttpResponse httpResponse = HttpResponse.init();
            try {
                controller.service(httpRequest, httpResponse);
            } catch (final Exception e) {
                httpResponse.setStatusCode(NOT_FOUND);
                httpResponse.setBody(e.getMessage());
            }

            outputStream.write(httpResponse.stringify().getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }
}
