package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import org.apache.catalina.Controller;
import org.apache.catalina.RequestMapping;
import org.apache.coyote.Processor;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.request.HttpRequestParser;
import org.apache.coyote.http11.response.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Http11Processor implements Runnable, Processor {

    private static final Logger LOGGER = LoggerFactory.getLogger(Http11Processor.class);
    private final Socket connection;

    public Http11Processor(final Socket connection) {
        this.connection = connection;
    }

    @Override
    public void run() {
        LOGGER.info("connect host: {}, port: {}", connection.getInetAddress(), connection.getPort());
        process(connection);
    }

    @Override
    public void process(final Socket connection) {
        try (InputStream inputStream = connection.getInputStream();
                OutputStream outputStream = connection.getOutputStream();
                BufferedReader br = new BufferedReader(new InputStreamReader(inputStream))) {
            HttpRequest request = HttpRequestParser.parse(br);
            HttpResponse response = HttpResponse.createEmptyResponse();

            Controller controller = RequestMapping.getController(request);
            controller.service(request, response);

            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (IOException e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

}
