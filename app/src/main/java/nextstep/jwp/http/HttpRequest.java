package nextstep.jwp.http;

import static java.util.stream.Collectors.toMap;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class HttpRequest {

    private static final int NONE_QUERY = -1;

    private final List<String> headerLines;

    public HttpRequest(List<String> headerLines) {
        this.headerLines = headerLines;
    }

    public static HttpRequest readFromInputStream(InputStream inputStream) throws IOException {
        final BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        final List<String> headerLine = new ArrayList<>();
        while (reader.ready()) {
            final String oneLine = reader.readLine();
            headerLine.add(oneLine);
        }
        return new HttpRequest(headerLine);
    }

    public String method() {
        final String firstLine = headerLines.get(0);
        final String[] splitFirstLine = firstLine.split(" ");
        return splitFirstLine[0];
    }

    public String getRequestURLWithoutQuery() {
        final String firstLine = headerLines.get(0);
        final String[] splitFirstLine = firstLine.split(" ");
        final String rawURL = splitFirstLine[1];
        final int index = rawURL.indexOf("?");

        if (index == NONE_QUERY) {
            return rawURL;
        }
        return rawURL.substring(0, index);
    }

    public Map<String, String> body() {
        final String rawBody = headerLines.get(headerLines.size() - 1);
        return Arrays.stream(rawBody.split("&"))
            .map(singleBody -> singleBody.split("="))
            .collect(toMap(unitBody -> unitBody[0], unitBody -> unitBody[1]));
    }

    public boolean isResource() {
        final String firstLine = headerLines.get(0);
        final String[] splitFirstLine = firstLine.split(" ");
        final String rawURL = splitFirstLine[1];
        String[] splitURL = rawURL.split("\\.");
        return splitURL.length != 1;
    }

    public String resourceType() {
        final String firstLine = headerLines.get(0);
        final String[] splitFirstLine = firstLine.split(" ");
        final String rawURL = splitFirstLine[1];
        String[] splitURL = rawURL.split("\\.");
        return splitURL[splitURL.length - 1];
    }
}
