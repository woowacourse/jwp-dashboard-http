package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import nextstep.jwp.exception.UncheckedServletException;
import org.apache.coyote.Processor;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.HttpResponseMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Http11Processor implements Runnable, Processor {

    private static final Logger LOG = LoggerFactory.getLogger(Http11Processor.class);
    private static final HttpResponseMapper RESOLVER = new HttpResponseMapper();

    private final Socket connection;

    public Http11Processor(final Socket connection) {
        this.connection = connection;
    }

    @Override
    public void run() {
        process(connection);
    }

    @Override
    public void process(final Socket connection) {
        try (final InputStream inputStream = connection.getInputStream();
             final OutputStream outputStream = connection.getOutputStream();
             final BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {

            HttpRequest request = new HttpRequest(toRequestLines(reader));
            HttpResponse response = RESOLVER.resolveException(request);
            logIO(request, response);

            outputStream.write(response.getResponse().getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            LOG.error(e.getMessage(), e);
        }
    }

    private void logIO(HttpRequest request, HttpResponse httpResponse) {
        LOG.info("\n\n###### ----REQUEST---- ###### \n\n" + request.getLines() +
                "\n\n###### ----RESPONSE---- ###### \n\n" + httpResponse.getHeader() + "\n\n");
    }

    private List<String> toRequestLines(final BufferedReader reader) throws IOException {
        List<String> lines = new ArrayList<>();
        String line = " ";
        while (!line.isEmpty()) {
            line = reader.readLine();
            lines.add(line);
        }
        return lines;
    }
}
