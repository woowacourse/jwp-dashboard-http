package org.apache.coyote.http11;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.List;
import org.apache.coyote.Processor;
import org.apache.coyote.http11.controller.BasicController;
import org.apache.coyote.http11.controller.Controller;
import org.apache.coyote.http11.controller.controllermapping.ControllerMatcher;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);

    private final Socket connection;

    public Http11Processor(final Socket connection) {
        this.connection = connection;
    }

    @Override
    public void run() {
        log.info("connect host: {}, port: {}", connection.getInetAddress(), connection.getPort());
        process(connection);
    }

    @Override
    public void process(final Socket connection) {
        try (
                final InputStream inputStream = connection.getInputStream();
                final OutputStream outputStream = connection.getOutputStream()
        ) {
            final HttpRequest httpRequest = HttpRequest.from(inputStream);
            final HttpResponse httpResponse = new HttpResponse(outputStream);

            final Controller controller = new ControllerMatcher().matchController(httpRequest.getUri());
            if (controller == null) {
                throw new RuntimeException("Controller Not Found");
            }

            controller.service(httpRequest, httpResponse);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }
}
