package nextstep.jwp;

import java.io.*;
import java.net.Socket;
import java.util.Objects;

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
             final OutputStream outputStream = connection.getOutputStream();
             final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));) {

            String requestLine = bufferedReader.readLine();

            String requestHttpMethod = requestLine.split(" ")[0];
            String requestUri = requestLine.split(" ")[1];

            final StringBuilder stringBuilder = new StringBuilder();

            String line = null;
            do {
                line = bufferedReader.readLine();
                if (line == null) {
                    return;
                }
                stringBuilder.append(line).append("\r\n");
                log.debug(line);
            } while (!"".equals(line));


            if ("POST".equals(requestHttpMethod)) {
                String requestBody = readRequestBody(bufferedReader, stringBuilder);
                String response = PostRequestUri.createResponse(requestUri, requestBody);
                outputStream.write(response.getBytes());
                outputStream.flush();
            }

            if ("GET".equals(requestHttpMethod)) {
                String response = GetRequestUri.createResponse(requestUri);
                outputStream.write(response.getBytes());
                outputStream.flush();
            }
        } catch (IOException exception) {
            log.error("Exception stream", exception);
        } finally {
            close();
        }
    }

    private String readRequestBody(BufferedReader bufferedReader, StringBuilder stringBuilder) throws IOException {
        HttpHeaders httpHeaders = new HttpHeaders(stringBuilder.toString());
        int contentLength = Integer.parseInt(httpHeaders.get("Content-Length"));
        char[] buffer = new char[contentLength];
        bufferedReader.read(buffer, 0, contentLength);
        return new String(buffer);
    }

    private void close() {
        try {
            connection.close();
        } catch (IOException exception) {
            log.error("Exception closing socket", exception);
        }
    }
}
