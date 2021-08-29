package nextstep.jwp.http;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class HttpRequest {

    private final static int NONE_QUERY = -1;

    private List<String> headerLines;

    public HttpRequest(List<String> headerLines) {
        this.headerLines = headerLines;
    }

    public static HttpRequest readFromInputStream(InputStream inputStream) throws IOException {
        final BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        final List<String> headerLine = new LinkedList<>();
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

    public String getRequestURL() {
        final String firstLine = headerLines.get(0);
        final String[] splitFirstLine = firstLine.split(" ");
        return splitFirstLine[1];
    }

    public Map<String, String> body() {
        final String rawBody = headerLines.get(headerLines.size() - 1);
        final String[] splitBody = rawBody.split("&");
        final Map<String, String> bodyQuery = new HashMap<>();
        for (String singleBody : splitBody) {
            final String[] unitBody = singleBody.split("=");
            bodyQuery.put(unitBody[0], unitBody[1]);
        }
        return bodyQuery;
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
