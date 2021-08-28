package nextstep.jwp;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.Socket;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

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

            String request = new String(inputStream.readAllBytes());
            String[] requestSplit = request.split("\n\n");
            String header = requestSplit[0];

            List<String> lines = header.lines().collect(Collectors.toList());

            // 리퀘스트 라인(헤더 첫줄)
            String requestLine = lines.get(0);
            String uriPath = requestLine.split(" ")[1];
            String uri = uriPath.split("\\?")[0];

            String responseBody = "Hello world!";
            if (!uri.equals("/")) {
                responseBody = readFile(uri);
            }

            final String response = String.join("\r\n",
                    "HTTP/1.1 200 OK ",
                    "Content-Type: text/html;charset=utf-8 ",
                    "Content-Length: " + responseBody.getBytes().length + " ",
                    "",
                    responseBody);

            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (IOException exception) {
            log.error("Exception stream", exception);
        } finally {
            close();
        }
    }

    private String readFile(String uriPath) throws IOException {
        String[] paths = uriPath.split("/");
        String fileName = paths[paths.length - 1];
        URL resource = getClass().getClassLoader().getResource("static/" + fileName);

        if (resource == null) {
            throw new FileNotFoundException();
        }
        Path path = new File(resource.getPath()).toPath();
        return Files.readString(path);
    }

    private void close() {
        try {
            connection.close();
        } catch (IOException exception) {
            log.error("Exception closing socket", exception);
        }
    }
}
