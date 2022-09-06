package org.apache.coyote.http11.request;

import static org.apache.coyote.http11.util.StringUtils.EMPTY;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;
import org.apache.coyote.http11.exception.FormDataValueNotFoundException;

public class HttpRequest {

    private final HttpMethod httpMethod;
    private final RequestPath requestPath;
    private final HttpRequestHeader httpRequestHeader;
    private final FormData formData;

    public HttpRequest(HttpMethod httpMethod, HttpRequestHeader httpRequestHeader,
                       RequestPath requestPath, FormData formData) {
        this.httpMethod = httpMethod;
        this.httpRequestHeader = httpRequestHeader;
        this.requestPath = requestPath;
        this.formData = formData;
    }

    public static HttpRequest from(BufferedReader bufferedReader) throws IOException {
        String requestLine = bufferedReader.readLine();
        RequestPath requestPath = RequestPath.from(requestLine);
        HttpRequestHeader httpRequestHeader = readHeaders(bufferedReader);
        if (!httpRequestHeader.hasContentLength()) {
            return new HttpRequest(
                    HttpMethod.from(requestLine), httpRequestHeader, requestPath, null);
        }
        FormData formData = readFormData(bufferedReader, httpRequestHeader);
        return new HttpRequest(HttpMethod.from(requestLine), httpRequestHeader, requestPath, formData);
    }

    private static HttpRequestHeader readHeaders(BufferedReader bufferedReader) {
        List<String> requestHeaderLines = bufferedReader.lines()
                .takeWhile(line -> !EMPTY.equals(line))
                .collect(Collectors.toList());
        return HttpRequestHeader.from(requestHeaderLines);
    }

    private static FormData readFormData(BufferedReader bufferedReader, HttpRequestHeader httpRequestHeader)
            throws IOException {
        int contentLength = Integer.parseInt(httpRequestHeader.getValueOf("Content-Length"));
        char[] buffer = new char[contentLength];
        bufferedReader.read(buffer, 0, contentLength);
        String formDataLine = new String(buffer);
        return FormData.from(formDataLine);
    }

    public String getPath() {
        return requestPath.getValue();
    }

    public boolean isLoginRequest() {
        return requestPath.containsLoginPath() && httpMethod.isPost();
    }

    public boolean isJsFileRequest() {
        return requestPath.containsJsFileExtension() && httpMethod.isGet();
    }

    public boolean isCssFileRequest() {
        return requestPath.containsCssFileExtension() && httpMethod.isGet();
    }

    public boolean isHtmlFileRequest() {
        return requestPath.containsHtmlFileExtension() && httpMethod.isGet();
    }

    public boolean isRootRequest() {
        return requestPath.isRootPath() && httpMethod.isGet();
    }

    public String getParamValueOf(String key) {
        if (formData == null) {
            throw new FormDataValueNotFoundException();
        }
        return formData.getValues()
                .get(key);
    }
}
