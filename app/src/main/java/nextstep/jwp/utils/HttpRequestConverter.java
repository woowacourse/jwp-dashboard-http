package nextstep.jwp.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import nextstep.jwp.exception.BadRequestException;
import nextstep.jwp.http.HttpCookie;
import nextstep.jwp.http.HttpMethod;
import nextstep.jwp.http.HttpSession;
import nextstep.jwp.http.HttpSessions;
import nextstep.jwp.http.Request;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HttpRequestConverter {

    private static final String CONTENT_LENGTH = "Content-Length";
    private static final Logger LOG = LoggerFactory.getLogger(HttpRequestConverter.class);

    private HttpRequestConverter() {
    }

    public static Request createdRequest(BufferedReader bufferedReader) {
        try {
            String line = bufferedReader.readLine();

            String[] requestUri = line.split(" ");
            HttpMethod httpMethod = HttpMethod.valueOf(requestUri[0]);
            String uri = requestUri[1];
            String httpVersion = requestUri[2];
            Map<String, String> header = getHeader(bufferedReader);
            Map<String, String> body = getBody(bufferedReader, header, httpMethod);
            HttpCookie httpCookie = getCookie(header.get("Cookie"));
            HttpSession httpSession = getHttpSession(httpCookie);

            return new Request.Builder()
                .method(httpMethod)
                .uri(uri)
                .httpVersion(httpVersion)
                .header(header)
                .cookie(httpCookie)
                .body(body)
                .httpSession(httpSession)
                .build();
        } catch (Exception e) {
            LOG.error("Request Error : {}", e.getMessage());
            throw new BadRequestException();
        }
    }

    private static Map<String, String> getHeader(BufferedReader bufferedReader) throws IOException {
        Map<String, String> header = new HashMap<>();

        String line = bufferedReader.readLine();
        while (!"".equals(line) && Objects.nonNull(line)) {
            int index = line.indexOf(":");
            String key = line.substring(0, index);
            String value = line.substring(index + 2);
            header.put(key, value);
            line = bufferedReader.readLine();
        }
        return header;
    }

    private static Map<String, String> getBody(BufferedReader bufferedReader,
        Map<String, String> header,
        HttpMethod httpMethod) throws IOException {

        Map<String, String> requestBody = new HashMap<>();

        if (httpMethod.isBody() && header.containsKey(CONTENT_LENGTH)) {
            int contentLength = Integer.parseInt(header.get(CONTENT_LENGTH));
            char[] buffer = new char[contentLength];
            int read = bufferedReader.read(buffer, 0, contentLength);
            if (read == -1) {
                return requestBody;
            }
            String line = new String(buffer);
            String[] bodyDates = line.split("&");
            for (String body : bodyDates) {
                String[] keyValue = body.split("=");
                requestBody.put(keyValue[0], keyValue[1]);
            }
        }
        return requestBody;
    }

    private static HttpCookie getCookie(String cookie) {
        Map<String, String> cookies = new HashMap<>();
        if (Objects.nonNull(cookie)) {
            for (String date : cookie.split(";")) {
                String[] keyValue = date.split("=");
                cookies.put(keyValue[0].trim(), keyValue[1].trim());
            }
        }
        return new HttpCookie(cookies);
    }

    private static HttpSession getHttpSession(HttpCookie httpCookie) {
        String sessionId = httpCookie.getCookie("JSESSIONID");
        if (Objects.isNull(sessionId)) {
            sessionId = UUID.randomUUID().toString();
        }
        HttpSession session = HttpSessions.getSession(sessionId);
        if (Objects.nonNull(session)) {
            return session;
        }
        return new HttpSession(sessionId);
    }
}
