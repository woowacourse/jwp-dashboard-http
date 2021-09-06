package nextstep.jwp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Objects;
import nextstep.jwp.controller.Controller;
import nextstep.jwp.controller.ControllerAdvice;
import nextstep.jwp.exception.AbstractHttpException;
import nextstep.jwp.http.Request;
import nextstep.jwp.http.Response;
import nextstep.jwp.utils.HttpRequestConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RequestHandler implements Runnable {

    private static final Logger LOG = LoggerFactory.getLogger(RequestHandler.class);
    private static final RequestMapping REQUEST_MAPPING = new RequestMapping();

    private final Socket connection;

    public RequestHandler(Socket connection) {
        this.connection = Objects.requireNonNull(connection);
    }

    @Override
    public void run() {
        LOG.debug("New Client Connect! Connected IP : {}, Port : {}", connection.getInetAddress(),
            connection.getPort());
        LOG.debug("쓰레드 이름 : {}", Thread.currentThread().getName());

        try (final InputStream inputStream = connection.getInputStream();
            final OutputStream outputStream = connection.getOutputStream();
            final BufferedReader bufferedReader = new BufferedReader(
                new InputStreamReader(inputStream))) {

            Response response = messageConvert(bufferedReader);

            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (IOException exception) {
            LOG.error("Exception stream", exception);
        } finally {
            close();
        }
    }

    private Response messageConvert(BufferedReader bufferedReader) throws IOException {
        Response response = new Response();
        try {
            final Request request = HttpRequestConverter.createdRequest(bufferedReader);
            response.addCookie(request);
            Controller controller = REQUEST_MAPPING.getController(request);
            controller.service(request, response);
            return response;
        } catch (AbstractHttpException exception) {
            ControllerAdvice.errorHandle(exception, response);
            return response;
        } catch (Exception exception) {
            LOG.error("알수없는 에러가 발생 : {}", exception.getMessage());
            response.set302Found("/500.html");
            return response;
        }
    }

    private void close() {
        try {
            connection.close();
        } catch (IOException exception) {
            LOG.error("Exception closing socket", exception);
        }
    }
}
