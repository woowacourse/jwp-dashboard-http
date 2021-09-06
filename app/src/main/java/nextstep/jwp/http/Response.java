package nextstep.jwp.http;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Response {

    private static final String NEW_LINE = "\r\n";
    private static final String CONTENT_LENGTH = "Content-Length: ";
    private static final String JSESSIONID = "JSESSIONID";

    private final List<String> header = new ArrayList<>();
    private String body = "";
    private HttpStatus httpStatus = HttpStatus.OK;

    public byte[] getBytes() {
        return message().getBytes();
    }

    public static Response create302Found(String location) {
        Response response = new Response();
        response.set302Found(location);
        return response;
    }

    public void setErrorResponse(String errorMessage, HttpStatus httpStatus) {
        addHeader("Content-Type", "application/json");
        body = errorMessage;
        this.httpStatus = httpStatus;
    }

    public void set200OK(Request request, String responseBody) {
        header.add("Content-Type: " + request.acceptType() + ";charset=utf-8");
        header.add(CONTENT_LENGTH + responseBody.getBytes().length);
        httpStatus = HttpStatus.OK;
        body = responseBody;
    }

    public void addHeader(String key, String value) {
        header.add(String.format("%s: %s", key, value));
    }

    public void set302Found(String location) {
        header.add("Location: " + location);
        httpStatus = HttpStatus.FOUND;
    }

    public void addCookie(Request request) {
        if (Objects.isNull(request.getCookie(JSESSIONID))) {
            addHeader("Set-Cookie", "JSESSIONID=" + request.getHttpSession().getId());
        }
    }

    public String message() {
        return String.join(NEW_LINE,
            String.format("HTTP/1.1 %d %s", httpStatus.getStatus(), httpStatus.getStatusMessage()),
            String.join(NEW_LINE, header),
            "",
            body
        );
    }
}
