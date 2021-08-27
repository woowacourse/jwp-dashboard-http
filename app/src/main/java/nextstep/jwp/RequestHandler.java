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

            final BufferedReader br = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
            String line = br.readLine();

            if (line == null) {
                return;
            }

            String[] requestLine = line.split(" ");

            while (!line.isBlank()) {
                line = br.readLine();
                log.debug("header : {}", line);
            }

            log.debug("request: " + requestLine[1]);

            final URL resource = getClass().getClassLoader().getResource("static/" + requestLine[1]);
            byte[] body = new byte[0];
            if (resource != null) {
                final Path path = new File(resource.getPath()).toPath();
                body = Files.readAllBytes(path);
            }

            final String response = String.join("\r\n",
                    "HTTP/1.1 200 OK ",
                    "Content-Type: text/html;charset=utf-8 ",
                    "Content-Length: " + body.length + " ",
                    "",
                    new String(body));

            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (IOException exception) {
            log.error("Exception stream", exception);
        } finally {
            close();
        }
    }

    private void close() {
        try {
            connection.close();
        } catch (IOException exception) {
            log.error("Exception closing socket", exception);
        }
    }
}
