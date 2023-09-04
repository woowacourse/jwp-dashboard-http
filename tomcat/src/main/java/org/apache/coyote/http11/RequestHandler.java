package org.apache.coyote.http11;

import static org.apache.coyote.http11.common.ContentType.HTML;
import static org.apache.coyote.http11.common.Method.GET;
import static org.apache.coyote.http11.common.Method.POST;
import static org.apache.coyote.http11.common.Status.BAD_REQUEST;
import static org.apache.coyote.http11.common.Status.FOUND;
import static org.apache.coyote.http11.common.Status.NOT_FOUND;
import static org.apache.coyote.http11.common.Status.OK;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;
import java.util.regex.Pattern;
import org.apache.coyote.http11.common.Session;
import org.apache.coyote.http11.request.Request;
import org.apache.coyote.http11.response.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RequestHandler {

    private static final Pattern RESOURCE_PATTERN_FILE_EXTENSION = Pattern.compile(".*\\.[^.]+");
    private static final Logger log = LoggerFactory.getLogger(RequestHandler.class);
    private static final HandlerAdaptor handlerAdaptor = new HandlerAdaptor();

    private RequestHandler() {
    }

    public static Response handle(Request request) throws IOException {
        if (request.getMethod() == GET) {
            log.info("get request: {}", request);
            return get(request);
        }
        if (request.getMethod() == POST) {
            log.info("post request: {}", request);
            return post(request);
        }
        return Response.of(BAD_REQUEST, "text/plain", "");
    }

    private static Response get(Request request) throws IOException {
        String uri = request.getUri();
        Session session = request.getSession();

        /// TODO: 2023/09/04 리팩터링
        if ("/".equals(uri)) {
            return Response.of(OK, "text/html", "Hello world!");
        }
        if ("/login".equals(uri)) {
            if (isLoginUser(session)) {
                return redirectLoginUser();
            }
            return getResponseForStaticResource("/login.html");
        }
        if ("/register".equals(uri)) {
            if (isLoginUser(session)) {
                return redirectLoginUser();
            }
            return getResponseForStaticResource("/register.html");
        }

        if (isStaticResourceURI(uri)) {
            return getResponseForStaticResource(uri);
        }

        return handlerAdaptor.getMapping(request);
    }

    private static boolean isLoginUser(Session session) {
        return Objects.nonNull(session) && Objects.nonNull(session.getAttribute("user"));
    }

    private static Response redirectLoginUser() {
        Response redirect = Response.of(FOUND, HTML.toString(), "");
        redirect.addLocation("/index.html");
        return redirect;
    }

    private static Response getResponseForStaticResource(String uri) throws IOException {
        final var url = RequestHandler.class.getClassLoader().getResource("static" + uri);
        return createResponse(url);
    }

    private static Response createResponse(URL url) throws IOException {
        if (Objects.isNull(url)) {
            log.warn("static resource url is null");
            return Response.of(NOT_FOUND, "text/plain", "");
        }
        log.info("static resource url found: {}", url.getPath());

        final var path = Paths.get(url.getPath());
        final var responseBody = new String(Files.readAllBytes(path));
        return Response.of(OK, Files.probeContentType(path), responseBody);
    }

    private static boolean isStaticResourceURI(String uri) {
        Path path = Paths.get(uri);
        String fileName = path.getFileName().toString();
        return RESOURCE_PATTERN_FILE_EXTENSION
                .matcher(fileName)
                .matches();
    }

    private static Response post(Request request) {

        return handlerAdaptor.postMapping(request);
    }

}
