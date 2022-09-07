package org.apache.coyote.http;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

public class HttpResponse {

    private static final String TEXT_HTML = "text/html";
    private static final String NEW_LINE = "\r\n";
    private static final String FILE_EXTENSION = ".";

    private final Map<String, String> headers;
    private HttpStatusCode statusCode;
    private String responseBody;

    public HttpResponse(final HttpStatusCode statusCode, final Map<String, String> headers) {
        this.statusCode = statusCode;
        this.headers = headers;
        this.responseBody = "";
    }

    public static HttpResponse init(final HttpStatusCode statusCode) {
        final Map<String, String> headers = new LinkedHashMap<>();

        return new HttpResponse(statusCode, headers);
    }

    public HttpResponse setBody(final String responseBody) {
        this.responseBody = responseBody;
        addContentTypeAndLength(TEXT_HTML);
        return this;
    }

    private void addContentTypeAndLength(final String mimeType) {
        headers.put("Content-Type", mimeType + ";charset=utf-8");
        headers.put("Content-Length", String.valueOf(responseBody.getBytes().length));
    }

    public HttpResponse setBodyByPath(final String path) {
        try (final BufferedReader reader = toReaderByPath(path)) {
            final String body = reader.lines()
                    .collect(Collectors.joining("\n"));

            this.responseBody = body + "\n";
            addContentTypeAndLength(toMimeType(path));
            return this;
        } catch (final Exception e) {
            e.printStackTrace();
            changeStatusCode(HttpStatusCode.NOT_FOUND);
            return setBodyByPath("/404.html");
        }
    }

    private BufferedReader toReaderByPath(final String path) throws FileNotFoundException {
        final String resourcePath = toResourcePath(path);
        final File file = new File(resourcePath);

        return new BufferedReader(new FileReader(file));
    }

    private String toResourcePath(final String path) {
        String resourcePath = "static/" + path;
        if (!resourcePath.contains(FILE_EXTENSION)) {
            resourcePath += ".html";
        }

        return HttpResponse.class.getClassLoader()
                .getResource(resourcePath)
                .getPath();
    }

    private String toMimeType(final String path) throws IOException {
        final Path resourcePath = Path.of(toResourcePath(path));

        return Files.probeContentType(resourcePath);
    }

    private void changeStatusCode(final HttpStatusCode statusCode) {
        this.statusCode = statusCode;
    }

    public HttpResponse setLocationAsHome() {
        headers.put("Location", "/index.html");
        return this;
    }

    public HttpResponse setSessionId(final String sessionId) {
        headers.put("Set-Cookie", "JSESSIONID=" + sessionId);
        return this;
    }

    public byte[] toResponseBytes() {
        final StringBuilder stringBuilder = new StringBuilder()
                .append(statusCode.getResponseStartLine())
                .append(" ")
                .append(NEW_LINE);

        for (final Entry<String, String> entry : headers.entrySet()) {
            stringBuilder.append(entry.getKey())
                    .append(": ")
                    .append(entry.getValue())
                    .append(" ")
                    .append(NEW_LINE);
        }

        return stringBuilder.append(NEW_LINE)
                .append(responseBody)
                .toString()
                .getBytes();
    }
}
