package nextstep.jwp.http;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Objects;
import nextstep.jwp.controller.FrontControllerServlet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RequestHandler implements Runnable {

    private static final Logger log = LoggerFactory.getLogger(RequestHandler.class);

    private final Socket connection;

    public RequestHandler(Socket connection) {
        this.connection = Objects.requireNonNull(connection);
    }

    @Override
    public void run() {
        log.debug("New Client Connect! Connected IP : {}, Port : {}", connection.getInetAddress(), connection.getPort());

        try (final InputStream inputStream = connection.getInputStream();
            final OutputStream outputStream = connection.getOutputStream()) {
            HttpRequestStreamReader requestReader = new HttpRequestStreamReader(inputStream);
            HttpRequest httpRequest = new HttpRequest(
                requestReader.getStatusLine(),
                requestReader.getRequestLines(),
                requestReader.getBodyLine()
            );

            if (!httpRequest.isEmptyLine()) {
                HttpResponse httpResponse = getHttpResponse(httpRequest);
                outputStream.write(httpResponse.getBytes());
            }
            outputStream.flush();
        } catch (IOException exception) {
            log.error("Exception stream", exception);
        } finally {
            close();
        }
    }

    private HttpResponse getHttpResponse(HttpRequest httpRequest) {
        ViewResolver viewResolver = new ViewResolver(httpRequest);
        if (viewResolver.isExisting()) {
            return viewResolver.resolve();
        }
        FrontControllerServlet frontControllerServlet = new FrontControllerServlet(httpRequest);
        return frontControllerServlet.process();
    }

    private void close() {
        try {
            connection.close();
        } catch (IOException exception) {
            log.error("Exception closing socket", exception);
        }
    }
}
