package org.apache.coyote.http11;

import nextstep.jwp.exception.UncheckedServletException;
import org.apache.coyote.Processor;
import org.apache.coyote.handler.FrontHandler;
import org.apache.coyote.http.HttpHeaders;
import org.apache.coyote.http.HttpMethod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

import static org.apache.coyote.http.HttpMethod.POST;
import static org.apache.coyote.http.HttpMethod.from;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);

    private final Socket connection;
    private final FrontHandler frontHandler;

    public Http11Processor(final Socket connection) {
        this.connection = connection;
        this.frontHandler = new FrontHandler();
    }

    @Override
    public void run() {
        log.info("connect host: {}, port: {}", connection.getInetAddress(), connection.getPort());
        process(connection);
    }

    @Override
    public void process(final Socket connection) {
        try (final var inputStream = connection.getInputStream();
             final var outputStream = connection.getOutputStream()) {
            final InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            final BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            final String firstLine = bufferedReader.readLine();
            if (firstLine == null) {
                return;
            }

            final HttpHeaders headers = parseHeader(bufferedReader);

            final String[] parsedFirstLine = firstLine.split(" ");
            final HttpMethod httpMethod = from(parsedFirstLine[0]);
            final String requestBody = parseRequestBody(httpMethod, headers, bufferedReader);

            final String response = frontHandler.handle(firstLine, headers, requestBody);

            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private String parseRequestBody(final HttpMethod httpMethod, final HttpHeaders headers, final BufferedReader bufferedReader) throws IOException {
        String requestBody = "";
        if (httpMethod == POST) {
            final int contentLength = Integer.parseInt(headers.get("Content-Length"));
            char[] buffer = new char[contentLength];
            bufferedReader.read(buffer, 0, contentLength);
            requestBody = new String(buffer);
        }
        return requestBody;
    }

    private HttpHeaders parseHeader(final BufferedReader bufferedReader) throws IOException {
        final Map<String, String> headers = new HashMap<>();
        String header = bufferedReader.readLine();
        while (!"".equals(header)) {
            final String[] parsedHeader = header.split(": ");
            headers.put(parsedHeader[0], parsedHeader[1]);
            header = bufferedReader.readLine();
        }

        return new HttpHeaders(headers);
    }
}
