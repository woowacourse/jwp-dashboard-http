package nextstep;

public class Fixture {
    public static String makeGetRequest(String uri) {
        return String.join("\r\n",
                "GET " + uri + " HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "",
                "");
    }

    public static String makePostRequest(String uri, String body) {
        return String.join("\r\n",
                "POST " + uri + " HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "Content-Length: " + body.getBytes().length,
                "",
                body,
                "",
                "");
    }
}
