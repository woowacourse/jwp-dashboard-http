package org.apache.coyote.http11;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class HttpResponseBuilder {

    private static final String STATIC_DIRECTORY = "static";
    private static final String LINE_FEED = "\r\n";
    private static final String SPACE = " ";
    private static final List<String> STATIC_PATH = List.of(".css", ".js", ".ico", ".html", ".svg");

    public String buildStaticFileOkResponse(HttpRequest httpRequest, String path) throws IOException {
        String status = HttpStatus.OK.getHttpStatusCode() + SPACE + HttpStatus.OK.getHttpStatusMessage();
        String protocol = httpRequest.getProtocol();
        String contentType = joinContentType(ContentType.findType(path));
        String content = getContent(path);
        String contentLength = joinContentLength(content);

        return protocol + SPACE + status + SPACE + LINE_FEED +
                findCookie(httpRequest) +
                contentType + SPACE + LINE_FEED +
                contentLength + SPACE + LINE_FEED +
                LINE_FEED +
                content;
    }

    private String joinContentLength(String content) {
        return "Content-Length: " + content.getBytes().length;
    }

    private String joinContentType(String path) {
        return "Content-Type: " + path + ";charset=utf-8";
    }

    public String buildStaticFileRedirectResponse(HttpRequest httpRequest, String redirectPath) throws IOException {
        String status = HttpStatus.REDIRECT.getHttpStatusCode() + SPACE + HttpStatus.REDIRECT.getHttpStatusMessage();
        String protocol = httpRequest.getProtocol();
        String contentType = joinContentType(ContentType.HTML.getType());
        String content = getContent(redirectPath);
        String contentLength = joinContentLength(content);

        return protocol + SPACE + status + SPACE + LINE_FEED +
                findCookie(httpRequest) +
                contentType + SPACE + LINE_FEED +
                contentLength + SPACE + LINE_FEED +
                "Location: " + redirectPath + SPACE + LINE_FEED +
                content;
    }

    public String buildStaticFileNotFoundResponse(HttpRequest httpRequest) throws IOException {
        String status = HttpStatus.NOT_FOUND.getHttpStatusCode() + SPACE + HttpStatus.NOT_FOUND.getHttpStatusMessage();
        String protocol = httpRequest.getProtocol();
        String contentType = joinContentType(ContentType.HTML.getType());
        String content = getContent("/404.html");
        String contentLength = joinContentLength(content);

        return protocol + SPACE + status + SPACE + LINE_FEED +
                findCookie(httpRequest) +
                contentType + SPACE + LINE_FEED +
                contentLength + SPACE + LINE_FEED +
                LINE_FEED +
                content;
    }

    public String buildCustomResponse(HttpRequest httpRequest, String content) {
        String status = HttpStatus.OK.getHttpStatusCode() + SPACE + HttpStatus.OK.getHttpStatusMessage();
        String protocol = httpRequest.getProtocol();
        String contentType = joinContentType(ContentType.HTML.getType());
        String contentLength = joinContentLength(content);

        return protocol + SPACE + status + SPACE + LINE_FEED +
                findCookie(httpRequest) +
                contentType + SPACE + LINE_FEED +
                contentLength + SPACE + LINE_FEED +
                LINE_FEED +
                content;
    }

    private String getContent(String path) throws IOException {
        URL resource = getClass().getClassLoader().getResource(STATIC_DIRECTORY + path);
        return new String(Files.readAllBytes(new File(resource.getFile()).toPath()));
    }

    private String findCookie(HttpRequest httpRequest) {
        Map<String, String> cookies = httpRequest.findCookies();
        if (!isStaticPath(httpRequest.getPath()) && !cookies.isEmpty()) {

            StringBuilder cookieHeader = new StringBuilder();
            Set<Map.Entry<String, String>> entries = cookies.entrySet();
            String collect = entries.stream()
                    .map(entry -> "Set-Cookie: " + entry.getKey() + "=" + entry.getValue())
                    .reduce((cookie1, cookie2) -> cookie1 + "; " + SPACE + LINE_FEED + cookie2)
                    .orElse("");
            cookieHeader.append(collect);

            return cookieHeader.toString() + SPACE + LINE_FEED;
        }
        return "";
    }

    private boolean isStaticPath(String path) {
        return STATIC_PATH.stream().anyMatch(path::endsWith);
    }

}
