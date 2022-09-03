package nextstep.jwp.servlet;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.apache.coyote.servlet.response.HttpResponse;
import org.apache.coyote.servlet.response.HttpResponse.HttpResponseBuilder;
import org.apache.coyote.support.HttpException;
import org.apache.coyote.support.HttpStatus;

public class ViewResolver {

    public HttpResponse findStaticResource(String uri) {
        final var path = toResourcePath(uri);
        return toHttpResponse(HttpStatus.OK, path);
    }

    public HttpResponse findStaticResource(ViewResource viewResource) {
        final var status = viewResource.getStatus();
        final var path = toResourcePath(viewResource.getUri());
        return toHttpResponse(status, path);
    }

    private Path toResourcePath(String uri) {
        try {
            final var classLoader = getClass().getClassLoader();
            final var url = classLoader.getResource("static" + uri);
            final var file = new File(url.getFile());
            return file.toPath();
        } catch (NullPointerException e) {
            throw HttpException.ofNotFound(e);
        }
    }

    private HttpResponse toHttpResponse(HttpStatus status, Path path) {
        try {
            return new HttpResponseBuilder(status)
                    .setContentType(String.format("%s;charset=utf-8", Files.probeContentType(path)))
                    .setMessageBody(new String(Files.readAllBytes(path)))
                    .build();
        } catch (IOException e) {
            throw HttpException.ofInternalServerError(e);
        }
    }
}
