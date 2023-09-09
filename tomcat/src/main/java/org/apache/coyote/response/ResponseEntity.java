package org.apache.coyote.response;

import org.apache.coyote.common.FileType;
import org.apache.coyote.common.HttpVersion;
import org.apache.coyote.http11.FileUtil;
import org.apache.coyote.request.Request;

import java.util.List;

public class ResponseEntity {


    private final ResponseStartLine responseStartLine;
    private final ResponseHeader responseHeader;
    private final String responseBody;

    private ResponseEntity(final ResponseStartLine responseStartLine, final ResponseHeader responseHeader, final String responseBody) {
        this.responseStartLine = responseStartLine;
        this.responseHeader = responseHeader;
        this.responseBody = responseBody;
    }

    public static ResponseEntity fromStatic(final Request request, final ResponseStatus responseStatus) {
        final ResponseStartLine responseStartLine = ResponseStartLine.from(request.httpVersion(), responseStatus);
        final String responseBody = FileUtil.getResource(request.httpVersion(), request.getRequestUrl());
        final ResponseContentType contentType = new ResponseContentType(request.getContentType());
        final ResponseContentLength contentLength = new ResponseContentLength(responseBody.getBytes().length);
        final ResponseHeader responseHeader = ResponseHeader.from(List.of(contentType, contentLength));
        return new ResponseEntity(responseStartLine, responseHeader, responseBody);
    }

    public static ResponseEntity fromString(final Request request, final String responseBody, final ResponseStatus responseStatus) {
        final ResponseStartLine responseStartLine = ResponseStartLine.from(request.httpVersion(), responseStatus);
        final ResponseContentType contentType = new ResponseContentType(FileType.TEXT.getContentType());
        final ResponseContentLength responseContentLength = new ResponseContentLength(responseBody.length());
        final ResponseHeader responseHeader = ResponseHeader.from(List.of(contentType, responseContentLength));
        return new ResponseEntity(responseStartLine, responseHeader, responseBody);
    }

    public static ResponseEntity fromViewPath(final HttpVersion httpVersion, final String viewPath, final ResponseStatus responseStatus) {
        final ResponseStartLine responseStartLine = ResponseStartLine.from(httpVersion, responseStatus);
        final String responseBody = FileUtil.getResourceFromViewPath(httpVersion, viewPath);
        final ResponseContentType contentType = new ResponseContentType(FileType.HTML.getContentType());
        final ResponseContentLength contentLength = new ResponseContentLength(responseBody.getBytes().length);
        final ResponseHeader responseHeader = ResponseHeader.from(List.of(contentType, contentLength));
        return new ResponseEntity(responseStartLine, responseHeader, responseBody);
    }

    public static ResponseEntity fromViewPathWithRedirect(final HttpVersion httpVersion, final String viewPath, final ResponseStatus responseStatus, final String redirectPath) {
        final ResponseStartLine responseStartLine = ResponseStartLine.from(httpVersion, responseStatus);
        final String responseBody = FileUtil.getResourceFromViewPath(httpVersion, viewPath);
        final ResponseLocation responseLocation = new ResponseLocation(redirectPath);
        final ResponseContentType contentType = new ResponseContentType(FileType.HTML.getContentType());
        final ResponseContentLength contentLength = new ResponseContentLength(responseBody.getBytes().length);
        final ResponseHeader responseHeader = ResponseHeader.from(List.of(contentType, contentLength, responseLocation));
        return new ResponseEntity(responseStartLine, responseHeader, responseBody);
    }

    @Override
    public String toString() {
        return String.join(System.lineSeparator(),
                responseStartLine + " ",
                responseHeader + " ",
                "",
                responseBody);
    }
}
