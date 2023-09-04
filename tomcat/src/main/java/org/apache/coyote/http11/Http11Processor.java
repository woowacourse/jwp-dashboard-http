package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import nextstep.jwp.exception.UncheckedServletException;
import org.apache.coyote.Processor;
import org.apache.coyote.http11.common.Cookies;
import org.apache.coyote.http11.common.Session;
import org.apache.coyote.http11.request.Request;
import org.apache.coyote.http11.response.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Http11Processor implements Runnable, Processor {

    private static final SessionManager SESSION_MANAGER = new SessionManager();
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

    // TODO Request, Response 객체를 전달하고 set하는 방식으로 수정하기
    @Override
    public void process(final Socket connection) {
        try (final var inputStream = connection.getInputStream();
             final var outputStream = connection.getOutputStream()) {
            log.info("process start");

            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            final var request = Request.read(bufferedReader)
                    .orElseThrow(() -> new IllegalStateException("invalid request"));
            log.info("request: {}", request);
            final var response = RequestHandler.handle(request);
            checkSessionId(request, response);

            outputStream.write(response.getBytes());
            log.info("write response: {}", response);
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private void checkSessionId(Request request, Response response) {
        if (!request.hasCookieByName("JSESSIONID")) {
            Session session = new Session();
            response.addSetCookie(Cookies.ofJSessionId(session.getId()));
            SESSION_MANAGER.add(session);
        }
    }

}
