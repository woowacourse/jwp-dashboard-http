package org.apache.coyote.http11;

import org.apache.coyote.Processor;
import org.apache.coyote.http11.controller.AuthController;
import org.apache.coyote.http11.controller.RegisterController;
import org.apache.coyote.http11.request.HttpHeaders;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.request.RequestLine;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UncheckedIOException;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);

    private final Socket connection;

    private final AuthController authController = new AuthController();
    private final RegisterController registerController = new RegisterController();

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
        try (var inputStream = connection.getInputStream();
             var outputStream = connection.getOutputStream()) {

            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

            HttpRequest httpRequest = readHttpRequest(bufferedReader);
            HttpResponse httpResponse = new HttpResponse(httpRequest.httpVersion());

            if (httpRequest.path().equals("/login") || httpRequest.path().equals("/login.html")) {
                authController.service(httpRequest, httpResponse);
            } else if (httpRequest.path().equals("/register") || httpRequest.path().equals("/register.html")) {
                registerController.service(httpRequest, httpResponse);
            } else {
                httpResponse.setHttpStatus(HttpStatus.OK).setResponseFileName(httpRequest.path());
            }

            httpResponse.setBody(readFile(httpResponse.getResponseFileName()));
            httpResponse.addHeader("Content-Length", String.valueOf(httpResponse.getBody().getBytes().length));
            httpResponse.addHeader("Content-Type", getContentTypeHeaderFrom(httpRequest));

            BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(outputStream);
            bufferedOutputStream.write(httpResponse.format().getBytes());
            bufferedOutputStream.flush();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    private HttpRequest readHttpRequest(BufferedReader bufferedReader) throws IOException {
        List<String> lines = readRequestHeaders(bufferedReader);
        RequestLine requestLine = RequestLine.from(lines.get(0));
        HttpHeaders httpHeaders = HttpHeaders.from(lines.subList(1, lines.size()));
        String requestBody = readRequestBody(bufferedReader, httpHeaders.contentLength());
        return new HttpRequest(requestLine, httpHeaders, requestBody);
    }

    private List<String> readRequestHeaders(BufferedReader bufferedReader) throws IOException {
        List<String> lines = new ArrayList<>();
        String line = "";
        while (!(line = bufferedReader.readLine()).equals("")) {
            lines.add(line);
        }
        return lines;
    }

    private static String getContentTypeHeaderFrom(HttpRequest httpRequest) {
        List<String> acceptHeaderValues = httpRequest.header("Accept");
        if (acceptHeaderValues != null && acceptHeaderValues.contains("text/css")) {
            return "text/css;charset=utf-8";
        }
        return "text/html;charset=utf-8";
    }

    private static String readRequestBody(BufferedReader bufferedReader, int contentLength) throws IOException {
        if (contentLength <= 0) {
            return "";
        }
        char[] buffer = new char[contentLength];
        bufferedReader.read(buffer, 0, contentLength);
        return new String(buffer);
    }

    private String readFile(String fileName) {
        String filePath = this.getClass().getClassLoader().getResource("static" + fileName).getPath();
        try (Stream<String> lines = Files.lines(Paths.get(filePath))) {
            return lines.collect(Collectors.joining("\n", "", "\n"));
        } catch (IOException | UncheckedIOException e) {
            return "Hello world!";
        }
    }
}
