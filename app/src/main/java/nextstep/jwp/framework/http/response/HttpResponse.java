package nextstep.jwp.framework.http.response;

import static nextstep.jwp.framework.http.request.HttpRequest.LINE_DELIMITER;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import nextstep.jwp.framework.http.common.FileUtils;
import nextstep.jwp.framework.http.common.HttpHeaders;
import nextstep.jwp.framework.http.common.HttpStatus;
import nextstep.jwp.framework.http.common.ProtocolVersion;
import nextstep.jwp.framework.http.request.HttpRequestLine;

public class HttpResponse {

    private static final String SPACE = " ";

    private ProtocolVersion protocolVersion;
    private HttpStatus status;
    private HttpHeaders headers;
    private URL resourceURL;

    public void create(HttpRequestLine requestLine, HttpHeaders headers, HttpStatus status) {
        this.protocolVersion = requestLine.getProtocolVersion();
        this.headers = headers;
        this.status = status;
        this.resourceURL = requestLine.url(status);
        headers.putContentType(FileUtils.fileExtension(new File(resourceURL.getPath())));
    }

    public byte[] getBytes() throws IOException {
        headers.setContentLength(resource().length());
        return String.join(LINE_DELIMITER,
            statusLine(),
            headers.convertHeaderToResponse(),
            "",
            resource()
        ).getBytes(StandardCharsets.UTF_8);
    }

    private String statusLine() {
        return protocolVersion.getProtocolVersion() + SPACE + status.value() + SPACE + status.getReasonPhrase() + SPACE;
    }

    public String resource() throws IOException {
        final Path path = new File(resourceURL.getPath()).toPath();
        return Files.readString(path);
    }
}
