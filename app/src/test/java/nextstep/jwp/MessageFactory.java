package nextstep.jwp;

import java.io.IOException;
import java.util.UUID;
import nextstep.jwp.utils.FileConverter;

public class MessageFactory {

    private static final String NEW_LINE = "\r\n";
    private static final String HOST = "Host: http://localhost:8080";
    private static final String CONNECTION = "Connection: keep-alive";
    public static final UUID LOGIN_UUID = java.util.UUID.randomUUID();

    private MessageFactory() {
    }

    public static String createGetRequest(String url, String accept) {
        String header = String.join(NEW_LINE,
            "GET /" + url + " HTTP/1.1",
            HOST,
            CONNECTION,
            "Accept: " + accept);

        return getJoinMessage(header, "");
    }

    public static String createGetRequestWithCookie(String url, String accept, String cookie) {
        String header = String.join(NEW_LINE,
            "GET /" + url + " HTTP/1.1",
            HOST,
            CONNECTION,
            "Accept: " + accept,
            "Cookie: " + cookie);

        return getJoinMessage(header, "");
    }

    public static String createPostRequest(String url, String body) {
        String header = String.join(NEW_LINE,
            "POST /" + url + " HTTP/1.1",
            HOST,
            CONNECTION,
            "Content-Length: " + body.length());

        return getJoinMessage(header, body);
    }

    public static String createResponseOK(String fileName, String contentType) throws IOException {
        final String responseBody = FileConverter.fileToString("/" + fileName);

        return String.join(NEW_LINE, "HTTP/1.1 200 OK",
            "Content-Type: " + contentType + ";charset=utf-8",
            "Content-Length: " + responseBody.getBytes().length,
            "",
            responseBody);
    }

    public static String createResponseFound(String url) {
        return String.join(NEW_LINE,
            "HTTP/1.1 302 Found",
            "Location: /" + url,
            "",
            ""
        );
    }

    private static String getJoinMessage(String header, String body) {
        return String.join(NEW_LINE,
            header,
            "",
            body);
    }
}
