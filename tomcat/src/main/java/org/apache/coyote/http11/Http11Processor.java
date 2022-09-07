package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;
import java.util.NoSuchElementException;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.exception.UncheckedServletException;
import nextstep.jwp.model.User;
import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);
    private static final int PATH_INDEX = 1;

    private final Socket connection;

    public Http11Processor(final Socket connection) {
        this.connection = connection;
    }

    @Override
    public void run() {
        process(connection);
    }

    @Override
    public void process(final Socket connection) {
        try (InputStream inputStream = connection.getInputStream();
             InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
             BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
             OutputStream outputStream = connection.getOutputStream()) {

            HttpRequest httpRequest = HttpRequest.from(bufferedReader);
            HttpResponse httpResponse = executeRequestAndGetResponse(httpRequest);

            outputStream.write(httpResponse.getResponse().getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException | URISyntaxException e) {
            log.error(e.getMessage(), e);
        }
    }

    private HttpResponse executeRequestAndGetResponse(HttpRequest httpRequest) throws IOException, URISyntaxException {
        URI uri = new URI(httpRequest.getRequestLine().getPath());
        String requestPath = uri.getPath();

        if ("/".equals(requestPath)) {
            return new HttpResponse(StatusCode.getStatusCode(200), ContentType.HTML.getContentType(), "Hello world!");
        }

        if ("/login".equals(requestPath)) {
            return getLoginResponse(httpRequest, uri);
        }

        if ("/register".equals(requestPath)) {
            return doRegisterRequest(httpRequest, uri);
        }

        return HttpResponse.of(StatusCode.getStatusCode(200), requestPath);
    }

    private HttpResponse getLoginResponse(HttpRequest httpRequest, URI uri) throws IOException {
        if ("GET".equals(httpRequest.getRequestLine().getMethod())
                && uri.getQuery() == null) {
            return HttpResponse.of(StatusCode.getStatusCode(200),
                    uri.getPath().concat("." + ContentType.HTML.getExtension()));
        }
        if ("POST".equals(httpRequest.getRequestLine().getMethod())) {
            String body = httpRequest.getRequestBody().getBody();
            return doLoginRequest(new QueryMapper(body));
        }
        return doLoginRequest(new QueryMapper(uri));
    }

    private HttpResponse doLoginRequest(QueryMapper queryMapper) throws IOException {
        Map<String, String> parameters = queryMapper.getParameters();

        User user = InMemoryUserRepository.findByAccount(parameters.get("account"))
                .orElseThrow(NoSuchElementException::new);

        if (user.checkPassword(parameters.get("password"))) {
            log.info("user : " + user);
            return HttpResponse.of(StatusCode.getStatusCode(302), "/index.html");
        }
        return HttpResponse.of(StatusCode.getStatusCode(200), "/401.html");
    }

    private HttpResponse doRegisterRequest(HttpRequest httpRequest, URI uri) throws IOException {
        if ("GET".equals(httpRequest.getRequestLine().getMethod())
                && uri.getQuery() == null) {
            return HttpResponse.of(StatusCode.getStatusCode(200),
                    uri.getPath().concat("." + ContentType.HTML.getExtension()));
        }
        if ("POST".equals(httpRequest.getRequestLine().getMethod())) {
            String body = httpRequest.getRequestBody().getBody();
            QueryMapper queryMapper = new QueryMapper(body);
            Map<String, String> parameters = queryMapper.getParameters();

            User user = new User(parameters.get("account"), parameters.get("password"), parameters.get("email"));
            InMemoryUserRepository.save(user);
            return HttpResponse.of(StatusCode.getStatusCode(200), "/index.html");
        }
        return HttpResponse.of(StatusCode.getStatusCode(200), "/401.html");
    }
}
