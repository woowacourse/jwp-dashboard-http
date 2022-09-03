package org.apache.mvc.handlerchain;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.HttpStatus;
import org.apache.coyote.http11.response.ResponseBody;
import org.apache.coyote.http11.response.headers.ContentLength;
import org.apache.coyote.http11.response.headers.ContentType;
import org.apache.util.FileUtil;
import org.apache.util.UrlUtil;

public class StaticFileRequestHandlerChain implements RequestHandlerChain {

    private static final String LOCATION = "static";

    private final RequestHandlerChain next;

    public StaticFileRequestHandlerChain(RequestHandlerChain next) {
        this.next = next;
    }

    @Override
    public HttpResponse handle(HttpRequest request) {
        String filePath = UrlUtil.joinUrl(LOCATION, request.getPath());
        try {
            File file = FileUtil.loadFile(filePath);
            return HttpResponse.from(HttpStatus.OK, readAsString(file))
                    .addHeader(ContentType.findWithExtension(file.getName()));
        } catch (IllegalArgumentException | IOException e) {
            return next.handle(request);
        }
    }

    private String readAsString(File file) throws IOException {
        return new String(Files.readAllBytes(file.toPath()));
    }
}
