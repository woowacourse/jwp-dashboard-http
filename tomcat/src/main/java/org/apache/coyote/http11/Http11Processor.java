package org.apache.coyote.http11;

import java.io.IOException;
import java.net.Socket;
import java.util.Optional;

import org.apache.coyote.Processor;
import org.apache.coyote.http11.exception.FileNotFoundException;
import org.apache.coyote.http11.http.ContentType;
import org.apache.coyote.http11.http.Header;
import org.apache.coyote.http11.http.HttpRequest;
import org.apache.coyote.http11.http.HttpResponse;
import org.apache.coyote.http11.http.StatusCode;
import org.apache.coyote.http11.util.StaticResourceUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.exception.UncheckedServletException;
import nextstep.jwp.model.User;

public class Http11Processor implements Runnable, Processor {

	private static final String UNAUTHORIZED_HTML = "401.html";
	private static final String NOT_FOUND_HTML = "404.html";

	private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);

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
		try (final var inputStream = connection.getInputStream();
			 final var outputStream = connection.getOutputStream()) {

			final var response = makeHttpResponse(new HttpRequest(inputStream));

			String responseMessage = response.getFullMessage();
			outputStream.write(responseMessage.getBytes());
			outputStream.flush();
		} catch (IOException | UncheckedServletException e) {
			log.error(e.getMessage(), e);
		}
	}

	private HttpResponse makeHttpResponse(HttpRequest httpRequest) throws IOException {
		String requestUrl = httpRequest.getUrl();
		if (requestUrl.equals("/")) {
			return HttpResponse.OK()
				.responseBody("Hello world!")
				.setHeader(Header.CONTENT_TYPE, ContentType.HTML.getFormat())
				.build();
		}

		if (httpRequest.isStaticResourceRequest()) {
			return handleStaticRequest(httpRequest);
		}
		return handleApiRequest(httpRequest);
	}

	private HttpResponse handleStaticRequest(HttpRequest httpRequest) throws IOException {
		try {
			return HttpResponse.OK()
				.responseBody(StaticResourceUtil.getContent(httpRequest.getUrl()))
				.setHeader(Header.CONTENT_TYPE, httpRequest.getContentType().getFormat())
				.build();
		} catch (FileNotFoundException e) {
			return responseErrorPage(NOT_FOUND_HTML, 404);
		}
	}

	private HttpResponse handleApiRequest(HttpRequest httpRequest) throws IOException {
		String requestUrl = httpRequest.getUrl();
		if (requestUrl.startsWith("/login")) {
			return checkValidLogin(
				httpRequest.getQueryString("account"),
				httpRequest.getQueryString("password")
			);
		}
		return responseErrorPage(NOT_FOUND_HTML, 404);
	}

	private HttpResponse checkValidLogin(String account, String password) throws IOException {
		Optional<User> findUser = InMemoryUserRepository.findByAccount(account);
		if (findUser.isPresent() && findUser.get().checkPassword(password)) {
			return HttpResponse.FOUND()
				.setHeader(Header.LOCATION, "/index.html")
				.build();
		}
		return responseErrorPage(UNAUTHORIZED_HTML, 401);
	}

	private HttpResponse responseErrorPage(String htmlPath, int statusCode) throws IOException {
		String responseBody = StaticResourceUtil.getContent(htmlPath);
		return HttpResponse.builder()
			.statusCode(StatusCode.from(statusCode))
			.responseBody(responseBody)
			.setHeader(Header.CONTENT_TYPE, ContentType.HTML.getFormat())
			.build();
	}
}
