package nextstep.org.apache.coyote.http11;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import nextstep.jwp.controller.LoginController;
import nextstep.jwp.dto.LoginResponseDto;
import nextstep.jwp.exception.UncheckedServletException;
import nextstep.org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);

    private static final String RESOURCES_PATH_PREFIX = "static";
    private static final int ACCEPT_HEADER_BEST_CONTENT_TYPE_INDEX = 0;
    private static final String NOT_FOUND_DEFAULT_MESSAGE = "404 Not Found";

    private final Socket connection;
    private final HandlerMapper handlerMapper;

    public Http11Processor(final Socket connection) {
        this.connection = connection;
        this.handlerMapper = new HandlerMapper();
    }

    @Override
    public void run() {
        log.info("connect host: {}, port: {}", connection.getInetAddress(), connection.getPort());
        process(connection);
    }

    @Override
    public void process(final Socket connection) {
        try (
                InputStream inputStream = connection.getInputStream();
                OutputStream outputStream = connection.getOutputStream()
        ) {
            Http11Request http11Request = new Http11Request(inputStream);
            Cookies cookies = http11Request.getCookies();

            Http11Response response = null;
            String requestPathInfo = http11Request.getPathInfo();

            Object handler = handlerMapper.mapHandler(requestPathInfo);
            if (Objects.nonNull(handler)
                    && http11Request.getMethod().equals("POST")
                    && requestPathInfo.equals("/login")) {
                LoginController loginController = (LoginController) handler;
                LoginResponseDto loginDto = loginController.login(
                        cookies,
                        http11Request.getParsedBodyValue("account"),
                        http11Request.getParsedBodyValue("password"));

                response = new Http11Response(Status.FOUND)
                        .setHeader("Location", loginDto.getRedirectUrl())
                        .setCookies(cookies);
            }

            if (Objects.nonNull(handler)
                    && http11Request.getMethod().equals("POST")
                    && requestPathInfo.equals("/register")) {
                LoginController loginController = (LoginController) handler;
                LoginResponseDto loginDto = loginController.register(
                        http11Request.getParsedBodyValue("account"),
                        http11Request.getParsedBodyValue("password"),
                        http11Request.getParsedBodyValue("email")
                );

                response = new Http11Response(Status.FOUND)
                        .setHeader("Location", loginDto.getRedirectUrl());
            }

            if (http11Request.getMethod().equals("GET") && Objects.isNull(response)) {
                String contentType = selectFirstContentTypeOrDefault(
                        http11Request.getHeader("Accept"));

                // Todo: createResponseBody() pageController로 위임해보기
                // Todo: 헤더에 담긴 sessionId 유효성 검증
                if (requestPathInfo.contains("/login") && cookies.hasCookie("JSESSIONID")) {
                    response = new Http11Response(Status.FOUND)
                            .setHeader("Location", "/index.html");
                    writeResponse(outputStream, response);
                    return;
                }

                Optional<String> responseBody = createResponseBody(requestPathInfo);
                if (responseBody.isEmpty()) {
                    String notFoundPageBody = createResponseBody("/404.html")
                            .orElse(NOT_FOUND_DEFAULT_MESSAGE);

                    response = new Http11Response(Status.NOT_FOUND)
                            .setHeader("Content-Type", contentType + ";charset=utf-8")
                            .setHeader("Content-Length", String.valueOf(
                                    notFoundPageBody.getBytes(StandardCharsets.UTF_8).length))
                            .setBody(notFoundPageBody);
                    writeResponse(outputStream, response);
                    return;
                }

                response = new Http11Response(Status.OK)
                        .setHeader("Content-Type", contentType + ";charset=utf-8")
                        .setHeader("Content-Length", String.valueOf(
                                responseBody.get().getBytes(StandardCharsets.UTF_8).length))
                        .setBody(responseBody.get());
            }

            writeResponse(outputStream, response);
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private void writeResponse(OutputStream outputStream, Http11Response response)
            throws IOException {
        outputStream.write(response.createResponseAsByteArray());
        outputStream.flush();
    }

    private String selectFirstContentTypeOrDefault(String acceptHeader) {
        if (Objects.isNull(acceptHeader)) {
            return "text/html";
        }
        List<String> acceptHeaderValues = Arrays.asList(acceptHeader.split(","));
        return acceptHeaderValues.get(ACCEPT_HEADER_BEST_CONTENT_TYPE_INDEX);
    }

    private Optional<String> createResponseBody(String requestPath) throws IOException {
        if (requestPath.equals("/")) {
            return Optional.of("Hello world!");
        }

        String resourceName = RESOURCES_PATH_PREFIX + requestPath;
        if (!resourceName.contains(".")) {
            resourceName += ".html";
        }
        URL resource = getClass().getClassLoader().getResource(resourceName);

        if (Objects.isNull(resource)) {
            return Optional.empty();
        }
        return Optional.of(new String(Files.readAllBytes(new File(resource.getFile()).toPath())));
    }
}
