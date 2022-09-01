package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.exception.UncheckedServletException;
import nextstep.jwp.model.User;
import org.apache.coyote.Processor;
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
        process(connection);
    }

    @Override
    public void process(final Socket connection) {
        try (final var inputStream = connection.getInputStream();
             final var outputStream = connection.getOutputStream()) {

            BufferedReader inputBufferedReader = new BufferedReader(new InputStreamReader(inputStream));

            String uriPath = inputBufferedReader.readLine().split(" ")[1];
            if (uriPath.contains("?")) {
                String[] uriPaths = uriPath.split("\\?");
                uriPath = uriPaths[0] + ".html";
                String queryString = uriPaths[1];
                String[] queryParams = queryString.split("&");
                Map<String, String> query = new HashMap<>();
                for (String queryParam : queryParams) {
                    String[] splitQuery = queryParam.split("=");
                    query.put(splitQuery[0], splitQuery[1]);
                }
                Optional<User> user = InMemoryUserRepository.findByAccount(query.get("account"));
                user.ifPresent(value -> log.info(value.toString()));
            }
            var responseBody = getResponseBody(uriPath);
            String contentType = getContentType(uriPath);

            final var response = String.join("\r\n",
                    "HTTP/1.1 200 OK ",
                    "Content-Type: " + contentType + ";charset=utf-8 ",
                    "Content-Length: " + responseBody.getBytes().length + " ",
                    "",
                    responseBody);

            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException | URISyntaxException e) {
            log.error(e.getMessage(), e);
        }
    }

    private String getResponseBody(String uriPath) throws URISyntaxException, IOException {
        if (!uriPath.equals("/")) {
            String fileName = "static" + uriPath;
            final URL resource = getClass().getClassLoader().getResource(fileName);
            final File file = Paths.get(resource.toURI()).toFile();
            return new String(Files.readAllBytes(file.toPath()));
        }
        return "Hello world!";
    }

    private String getContentType(String uriPath) {
        if (uriPath.equals("/")) {
            return "text/html";
        }
        String extension = uriPath.split("\\.")[1];
        if (extension.equals("css")) {
            return "text/css";
        }
        if (extension.equals("js")) {
            return "application/x-javascript";
        }
        if (extension.equals("ico")) {
            return "image/*";
        }
        return "text/html";
    }
}
